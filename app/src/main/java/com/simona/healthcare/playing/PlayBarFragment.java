package com.simona.healthcare.playing;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.simona.healthcare.R;
import com.simona.healthcare.exercise.Exercise;
import com.simona.healthcare.program.Program;
import com.simona.healthcare.utils.DatabaseHelper;

import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class PlayBarFragment extends Fragment{

    private static PlayBarFragment sInstance;
    private Context mContext;
    private Program mProgram;
    private ExecutorService mExecutor;
    private ExecutorService mTTSExecutor;

    // UI
    private RelativeLayout mCurrentProgramLayout;
    private TextView mNoProgramText;
    private TextView mProgramText;
    private TextView mExerciseText;
    private TextView mSetText;
    private TextView mBreakText;
    private TextView mElapsedText;
    private TextView mTotalText;
    private SeekBar mSeekbar;

    // TextToSpeech
    TextToSpeech textToSpeech;

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
        mExecutor = Executors.newSingleThreadExecutor();
        mTTSExecutor = Executors.newSingleThreadExecutor();

        textToSpeech=new TextToSpeech(mContext, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.UK);
                    textToSpeech.setSpeechRate(0.8f);
                }
            }
        });

        mCurrentProgramLayout = view.findViewById(R.id.currentProgramLayout);
        mNoProgramText = view.findViewById(R.id.noProgramText);
        mProgramText = view.findViewById(R.id.programText);
        mExerciseText = view.findViewById(R.id.exerciseText);
        mSetText = view.findViewById(R.id.setText);
        mBreakText = view.findViewById(R.id.breakText);
        mElapsedText = view.findViewById(R.id.startText);
        mTotalText = view.findViewById(R.id.endText);
        mSeekbar = view.findViewById(R.id.seekBar);

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
        mProgram = program;
        if (program == null) {
            mCurrentProgramLayout.setVisibility(View.GONE);
            mNoProgramText.setVisibility(View.VISIBLE);
        } else {
            setupViewForProgram();
            startProgram();
        }


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
        if(mProgram.getExercises().size() ==0) {
            mProgram.setExercises(DatabaseHelper.getInstance(
                    mContext).getExercisesForProgramId(mProgram.getId()));
        }

        // Speech - program name
        String programText = mContext.getResources().getString(R.string.starting_program) + " "
                + mProgram.getName();
        playSound(programText);

        mExecutor.execute(mExerciseRunnable);

    }

    /**
     * Text to speech.
     * @param text
     */
    private void playSound(final String text) {
        mTTSExecutor.execute(new Runnable() {
            @Override
            public void run() {
                textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
            }
        });
    }

    private Runnable mExerciseRunnable = new Runnable() {
        @Override
        public void run() {

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
            }

            // Exercises
            for (final Exercise e : mProgram.getExercises()) {
                // Exercise Name
                playSound(e.getName());
                // Reps and sets
                playSound(String.format(mContext.getResources().getString(R.string.exercise_tts),
                        e.getSets(), e.getRepsPerSet()));


                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mExerciseText.setText(e.getName());
                        mTotalText.setText(String.valueOf(e.getRepsPerSet()));
                        mSeekbar.setMax(e.getRepsPerSet());
                    }
                });

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException exc) {
                }

                // Sets
                for (final AtomicInteger setNumber = new AtomicInteger(1);
                     setNumber.get() <= e.getSets();
                     setNumber.set(setNumber.get() + 1)) {

                    /* ____________________ START SET _____________________*/

                    playSound(mContext.getResources().getString(R.string.start_tts));
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mTotalText.setText(String.valueOf(e.getRepsPerSet()));
                            mSeekbar.setMax(e.getRepsPerSet());
                            mSetText.setText("Set " + (setNumber.get()) + "/" +e.getSets());
                            mBreakText.setVisibility(View.GONE);
                        }
                    });

                    // Execute Set
                    for (final AtomicInteger reps = new AtomicInteger(1);
                         reps.get() <= e.getRepsPerSet();
                         reps.set(reps.get() + 1)) {

                        /* ____________________ REPS _____________________*/
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mElapsedText.setText(String.valueOf(reps));
                                mSeekbar.setProgress(reps.get());
                                playSound(String.valueOf(reps.get()));
                            }
                        });

                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                    }

                    /* ____________________ BREAK _____________________*/

                    playSound(mContext.getResources().getString(R.string.stop_tts));
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mBreakText.setVisibility(View.VISIBLE);
                            mElapsedText.setText(String.valueOf(0));
                            mTotalText.setText(String.valueOf(e.getBreak()));
                            mSeekbar.setMax(e.getBreak());
                            mSeekbar.setProgress(0);
                        }
                    });

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
                            Thread.sleep(1000);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                    }

                }
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
    };
}
