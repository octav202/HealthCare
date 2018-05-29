package com.simona.healthcare.playing;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.simona.healthcare.R;
import com.simona.healthcare.exercise.Exercise;
import com.simona.healthcare.program.Program;
import com.simona.healthcare.utils.DatabaseHelper;
import com.simona.healthcare.utils.Utils;
import static com.simona.healthcare.utils.Constants.TAG;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class PlayBarFragment extends Fragment{

    private static PlayBarFragment sInstance;
    private Context mContext;
    private Program mProgram;
    private ExecutorService mTTSExecutor;
    private PlayThread mPlayThread;

    // UI
    private RelativeLayout mCurrentProgramLayout;
    private TextView mNoProgramText;
    private TextView mProgramText;
    private TextView mExerciseText;
    private TextView mSetText;
    private TextView mCurrentRep;
    private TextView mElapsedText;
    private TextView mTotalText;
    private SeekBar mSeekbar;

    // Controls
    private Button mPrevButton;
    private Button mPlayButton;
    private Button mNextButton;

    // TextToSpeech
    TextToSpeech textToSpeech;

    // State
    private AtomicInteger mState = new AtomicInteger();
    private int STATE_PAUSED = 0;
    private int STATE_PLAYING = 1;

    public static PlayBarFragment getInstance() {
        if (sInstance == null) {
            sInstance = new PlayBarFragment();
        }

        return sInstance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.playbar_fragment, container, false);

        mContext = getActivity().getApplicationContext();
        mTTSExecutor = Executors.newSingleThreadExecutor();
        mPlayThread = new PlayThread();

        textToSpeech=new TextToSpeech(mContext, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.US);
                    textToSpeech.setSpeechRate(0.8f);
                }
            }
        });

        mCurrentProgramLayout = view.findViewById(R.id.currentProgramLayout);
        mNoProgramText = view.findViewById(R.id.noProgramText);
        mProgramText = view.findViewById(R.id.programText);
        mExerciseText = view.findViewById(R.id.exerciseText);
        mSetText = view.findViewById(R.id.setText);
        mCurrentRep = view.findViewById(R.id.currentRep);
        mElapsedText = view.findViewById(R.id.startText);
        mTotalText = view.findViewById(R.id.endText);
        mSeekbar = view.findViewById(R.id.seekBar);
        mPrevButton = view.findViewById(R.id.prevButton);
        mPlayButton = view.findViewById(R.id.playButton);
        mNextButton = view.findViewById(R.id.nextButton);

        if (mState.get() == STATE_PAUSED) {
            mPlayButton.setBackground(mContext.getResources().getDrawable(R.drawable.ic_play));
        } else {
            mPlayButton.setBackground(mContext.getResources().getDrawable(R.drawable.ic_pause));
        }

        mPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mState.get() == STATE_PAUSED) {
                    mState.set(STATE_PLAYING);
                    mPlayButton.setBackground(mContext.getResources().getDrawable(R.drawable.ic_pause));
                } else {
                    mState.set(STATE_PAUSED);
                    mPlayButton.setBackground(mContext.getResources().getDrawable(R.drawable.ic_play));
                }
            }
        });

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        // Get current program
        if (mProgram == null) {
            mCurrentProgramLayout.setVisibility(View.GONE);
            mNoProgramText.setVisibility(View.VISIBLE);
        } else {
            setupViewForProgram();
        }

        return view;
    }

    /**
     * Set Current Program.
     * Set null for no program.
     * @param program
     */
    public void setProgram(Program program) {
        mPlayThread.interrupt();
        textToSpeech.stop();
        mProgram = program;

        if (program == null) {
            // Stop Program
            mCurrentProgramLayout.setVisibility(View.GONE);
            mNoProgramText.setVisibility(View.VISIBLE);
        } else {
            mPlayThread = new PlayThread();
            setupViewForProgram();
            startProgram();
        }
    }

    public Program getProgram() {
        return mProgram;
    }

    /**
     * Display program information.
     */
    private void setupViewForProgram() {
        mCurrentProgramLayout.setVisibility(View.VISIBLE);
        mNoProgramText.setVisibility(View.GONE);
        mProgramText.setText(mProgram.getName());
    }

    /**
     * Start current program.
     */
    private void startProgram() {
        // Get exercises for current program
        if (mProgram.getExercises().size() == 0) {
            mProgram.setExercises(DatabaseHelper.getInstance(
                    mContext).getExercisesForProgramId(mProgram.getId()));
        }

        mPlayThread.start();
    }

    /**
     * Text to speech - start, stop, exercise name, sets, reps.
     *
     * @param text
     */
    private void playSound(final String text) {
        if (Utils.getProgramTTS(mContext)) {
            mTTSExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
                }
            });
        }
    }

    /**
     * Text to speech - Rep Number.
     *
     * @param repNumber
     */
    private void playRepNumber(final int repNumber) {
        if (Utils.getRepsTTS(mContext)) {
            mTTSExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    textToSpeech.speak(String.valueOf(repNumber), TextToSpeech.QUEUE_FLUSH, null);
                }
            });
        }
    }

    public class PlayThread extends Thread {

        private static final int REP_DURATION = 1000;
        private static final int TTS_PROGRAM_NAME = 3000;
        private static final int TTS_EXERCISE_NAME = 1000;
        private static final int TTS_SETS_REPS_DESCRIPTION= 4000;
        private static final int TTS_START_DURATION= 2000;
        private static final int TTS_STOP_DURATION= 1000;


        // Playlist
        private AtomicInteger mCurrentExercise = new AtomicInteger(0);
        private AtomicInteger mCurrentSet = new AtomicInteger(0);

        public void pause() {

        }

        public void play() {

        }

        public void incrementExercise() {
            mCurrentExercise.incrementAndGet();
        }

        public void decrementExercise() {
            mCurrentExercise.decrementAndGet();
        }

        @Override
        public synchronized void start() {
            super.start();
        }

        @Override
        public void run() {

            if (mProgram == null) {
                return;
            }

            // Speech - program name
            String programText = mContext.getResources().getString(R.string.starting_program) + " "
                    + mProgram.getName();
            playSound(programText);

            try {
                Thread.sleep(TTS_PROGRAM_NAME);
            } catch (InterruptedException e) {
                return;
            }

            if (mProgram == null) {
                return;
            }

            // Exercises
            for (int ex = mCurrentExercise.get(); ex < mProgram.getExercises().size(); ex++) {

                final Exercise e = mProgram.getExercises().get(ex);
                playExercise(e);
                mCurrentExercise.incrementAndGet();
            }

            playSound(mContext.getResources().getString(R.string.end_program));

            // End of program
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setProgram(null);
                }
            });
        }

        private void playExercise(final Exercise e) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mExerciseText.setText(e.getName());
                    mElapsedText.setText("0");
                    mTotalText.setText(String.valueOf(e.getRepsPerSet()));
                    mSeekbar.setProgress(0);
                    mSeekbar.setMax(e.getRepsPerSet());
                }
            });

            // Exercise Name
            playSound(e.getName());
            try {
                Thread.sleep(TTS_EXERCISE_NAME);
            } catch (InterruptedException exc) {
            }
            // Reps and sets
            playSound(String.format(mContext.getResources().getString(R.string.exercise_tts),
                    e.getSets(), e.getRepsPerSet()));

            playSets(e);

            try {
                Thread.sleep(TTS_SETS_REPS_DESCRIPTION);
            } catch (InterruptedException exc) {
                return;
            }


        }

        private void playSets(final Exercise e) {
            /* ____________________ SETS _____________________*/
            for (final AtomicInteger setNumber = new AtomicInteger(mCurrentSet.get());
                 setNumber.get() <= e.getSets();
                 setNumber.set(setNumber.get() + 1)) {

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mElapsedText.setText("0");
                        mTotalText.setText(String.valueOf(e.getRepsPerSet()));
                        mSeekbar.setMax(e.getRepsPerSet());
                        mSetText.setText("Set " + (setNumber.get()) + "/" +e.getSets());
                        mCurrentRep.setVisibility(View.GONE);
                        mSeekbar.setProgress(0);
                    }
                });

                // Play Sound - Start
                playSound(mContext.getResources().getString(R.string.start_tts));
                try {
                    Thread.sleep(TTS_START_DURATION);
                } catch (InterruptedException e1) {
                    return;
                }

                /* ____________________ REPS _____________________________*/
                for (final AtomicInteger reps = new AtomicInteger(1);
                     reps.get() <= e.getRepsPerSet();
                     reps.set(reps.get() + 1)) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mElapsedText.setText(String.valueOf(reps));
                            mSeekbar.setProgress(reps.get());
                        }
                    });

                    // Play Rep Number
                    playRepNumber(reps.get());
                    try {
                        Thread.sleep(REP_DURATION);
                    } catch (InterruptedException e1) {
                        return;
                    }
                }

                playBreak(e);

            }

            mCurrentSet.incrementAndGet();
        }

        private void playBreak (final Exercise e) {
            /* ____________________ BREAK _____________________*/

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mCurrentRep.setVisibility(View.VISIBLE);
                    mElapsedText.setText(String.valueOf(0));
                    mTotalText.setText(String.valueOf(e.getBreak()));
                    mSeekbar.setMax(e.getBreak());
                    mSeekbar.setProgress(0);
                }
            });

            // Play Sound - Stop
            playSound(mContext.getResources().getString(R.string.stop_tts));

            for (final AtomicInteger br = new AtomicInteger();
                 br.get() < e.getBreak();
                 br.set(br.get() + 1)) {

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mElapsedText.setText(String.valueOf(br.get()));
                        mSeekbar.setProgress(br.get());
                    }
                });

                try {
                    Thread.sleep(TTS_STOP_DURATION);
                } catch (InterruptedException e1) {
                    return;
                }
            }
        }
    }
}
