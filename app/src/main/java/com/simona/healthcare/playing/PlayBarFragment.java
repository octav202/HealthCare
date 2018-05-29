package com.simona.healthcare.playing;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
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

import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static com.simona.healthcare.playing.Operation.TYPE_BREAK_UNIT;
import static com.simona.healthcare.playing.Operation.TYPE_TTS_EXERCISE;
import static com.simona.healthcare.playing.Operation.TYPE_TTS_EXERCISE_SETS_AND_REPS;
import static com.simona.healthcare.playing.Operation.TYPE_TTS_PROGRAM;
import static com.simona.healthcare.playing.Operation.TYPE_TTS_PROGRAM_OVER;
import static com.simona.healthcare.playing.Operation.TYPE_TTS_REP;
import static com.simona.healthcare.playing.Operation.TYPE_TTS_SET;
import static com.simona.healthcare.playing.Operation.TYPE_TTS_START;
import static com.simona.healthcare.playing.Operation.TYPE_TTS_STOP;
import static com.simona.healthcare.utils.Constants.TAG;


public class PlayBarFragment extends Fragment{

    private static PlayBarFragment sInstance;
    private Context mContext;
    private Program mProgram;
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
    private int STATE_STOPPED = 2;
    private Handler mHandler;
    private AtomicInteger mOperationIndex = new AtomicInteger();
    private ArrayList<Operation> mOperations;

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
        mHandler = new Handler(getActivity().getMainLooper());

        mTTSExecutor = Executors.newSingleThreadExecutor();

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
        mBreakText = view.findViewById(R.id.breakText);
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
                play();
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

        stopProgram();
        mProgram = program;
        if (program == null) {
            mCurrentProgramLayout.setVisibility(View.GONE);
            mNoProgramText.setVisibility(View.VISIBLE);
        } else {
            // Get exercises for current program
            if (mProgram.getExercises().size() == 0) {
                mProgram.setExercises(DatabaseHelper.getInstance(
                        mContext).getExercisesForProgramId(mProgram.getId()));
            }
            startProgram();
        }
    }

    private void startProgram() {
        Log.d(TAG, "startProgram()");
        setupViewForProgram();
        mOperations = new ArrayList<>();

        mOperations.addAll(Operation.programToOperations(mContext, mProgram));
        mState.set(STATE_PLAYING);
        mPlayButton.setBackground(mContext.getResources().getDrawable(R.drawable.ic_pause));
        mHandler.post(mOperationRunnable);
    }

    private void stopProgram() {
        Log.d(TAG, "stopProgram()");
        mOperationIndex.set(0);
        textToSpeech.stop();
        mHandler.removeCallbacksAndMessages(null);
        mState.set(STATE_STOPPED);
    }

    private void pauseProgram() {
        Log.d(TAG, "pauseProgram()");
        textToSpeech.stop();
        mHandler.removeCallbacksAndMessages(null);
        mState.set(STATE_PAUSED);
    }

    private void play() {
        if (mState.get() == STATE_PAUSED) {
            mState.set(STATE_PLAYING);
            mPlayButton.setBackground(mContext.getResources().getDrawable(R.drawable.ic_pause));

            // Play Program
            startProgram();
        } else if (mState.get() == STATE_PLAYING) {
            mState.set(STATE_PAUSED);
            mPlayButton.setBackground(mContext.getResources().getDrawable(R.drawable.ic_play));

            // Pause Program
            pauseProgram();
        }
    }

    private Runnable mOperationRunnable = new Runnable() {
        @Override
        public void run() {

            if (mState.get() == STATE_STOPPED || mState.get() == STATE_PAUSED) {
                Log.d(TAG, "Stopped/Paused, returning..");
            }

            if (mOperationIndex.get() == mOperations.size()) {
                return;
            }

            Operation op = mOperations.get(mOperationIndex.getAndIncrement());
            Log.d(TAG, "Operation : " + op);

            Exercise e = op.getExercise();

            switch (op.getType()) {
                case TYPE_TTS_PROGRAM:
                    String text = mContext.getResources().getString(R.string.starting_program) + " "
                            + op.getInfo();
                    playSound(text);
                    mProgramText.setText(op.getInfo());
                    break;
                case TYPE_TTS_EXERCISE:
                    playSound(e.getName());

                    // Update UI
                    mExerciseText.setText(e.getName());
                    mElapsedText.setText("0");
                    mTotalText.setText(String.valueOf(e.getRepsPerSet()));
                    mSeekbar.setProgress(0);
                    mSeekbar.setMax(e.getRepsPerSet());
                    break;
                case TYPE_TTS_EXERCISE_SETS_AND_REPS:
                    playSound(op.getInfo());
                    break;
                case TYPE_TTS_STOP:
                    playSound(op.getInfo());
                    mBreakText.setVisibility(View.VISIBLE);
                    break;
                case TYPE_TTS_START:
                    playSound(op.getInfo());
                    mBreakText.setVisibility(View.GONE);
                    break;
                case TYPE_TTS_SET:
                    String setNumber = mContext.getResources().getString(R.string.set) + " " + op.getInfo();
                    playSound(setNumber);

                    // Update UI
                    mElapsedText.setText("0");
                    mTotalText.setText(String.valueOf(e.getRepsPerSet()));
                    mSeekbar.setMax(e.getRepsPerSet());
                    mSetText.setText("Set " + (op.getInfo()) + "/" +e.getSets());
                    mBreakText.setVisibility(View.GONE);
                    mSeekbar.setProgress(0);
                    break;
                case TYPE_TTS_REP:

                    playSound(op.getInfo());

                    // Update UI
                    mElapsedText.setText(op.getInfo());
                    mTotalText.setText(String.valueOf(e.getRepsPerSet()));
                    mBreakText.setVisibility(View.GONE);
                    mSeekbar.setProgress(Integer.parseInt(op.getInfo()));
                    break;
                case TYPE_BREAK_UNIT:

                    mBreakText.setVisibility(View.VISIBLE);
                    mElapsedText.setText(String.valueOf(0));
                    mTotalText.setText(String.valueOf(e.getBreak()));
                    mSeekbar.setMax(e.getBreak());
                    mSeekbar.setProgress(Integer.parseInt(op.getInfo()));
                    // Update UI
                    break;

                case TYPE_TTS_PROGRAM_OVER:
                    playSound(op.getInfo());

                    mCurrentProgramLayout.setVisibility(View.GONE);
                    mNoProgramText.setVisibility(View.VISIBLE);
                    break;
                default:
                    break;
            }

            mHandler.postDelayed(this, op.getDuration());
        }
    };

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
}
