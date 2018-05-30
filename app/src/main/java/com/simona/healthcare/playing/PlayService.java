package com.simona.healthcare.playing;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.simona.healthcare.R;
import com.simona.healthcare.exercise.Exercise;
import com.simona.healthcare.program.Program;
import com.simona.healthcare.utils.DatabaseHelper;
import com.simona.healthcare.utils.Utils;

import java.util.ArrayList;
import java.util.Locale;
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
import static com.simona.healthcare.utils.Constants.SERVICE_ACTION_EXTRA;
import static com.simona.healthcare.utils.Constants.STATE_PAUSED;
import static com.simona.healthcare.utils.Constants.STATE_PLAYING;
import static com.simona.healthcare.utils.Constants.STATE_STOPPED;
import static com.simona.healthcare.utils.Constants.TAG;

public class PlayService extends Service {

    private Context mContext;
    private LocalBinder mBinder = new LocalBinder();

    private Program mProgram;

    // TextToSpeech
    TextToSpeech textToSpeech;

    // State
    private AtomicInteger mState = new AtomicInteger();
    private Handler mHandler;
    private AtomicInteger mOperationIndex = new AtomicInteger();
    private ArrayList<Operation> mOperations;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate()");

        mContext = getApplicationContext();
        mHandler = new Handler();

        textToSpeech=new TextToSpeech(mContext, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.US);
                    textToSpeech.setSpeechRate(0.8f);
                }
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand() " + intent.getStringExtra(SERVICE_ACTION_EXTRA));
        return START_STICKY;
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
            PlayBarFragment.getInstance().setupViewForProgram();
        } else {
            // Get exercises for current program
            if (mProgram.getExercises().size() == 0) {
                mProgram.setExercises(DatabaseHelper.getInstance(
                        mContext).getExercisesForProgramId(mProgram.getId()));
            }
            startProgram();
        }
    }

    public void startProgram() {
        Log.d(TAG, "startProgram()");
        mOperations = new ArrayList<>();
        mOperations.addAll(Operation.programToOperations(mContext, mProgram));
        mState.set(STATE_PLAYING);
        mHandler.post(mOperationRunnable);

        sendState(mState.get());
    }

    public void pauseProgram() {
        Log.d(TAG, "pauseProgram()");
        textToSpeech.stop();
        mHandler.removeCallbacksAndMessages(null);
        mState.set(STATE_PAUSED);

        sendState(mState.get());
    }

    public void stopProgram() {
        Log.d(TAG, "stopProgram()");
        mOperationIndex.set(0);
        textToSpeech.stop();
        mHandler.removeCallbacksAndMessages(null);
        mState.set(STATE_STOPPED);

        sendState(mState.get());
    }

    private void sendState(int state) {
        PlayBarFragment.getInstance().setState(state);

        // TO DO - send to widget
    }

    private void sendOperation(Operation op) {
        PlayBarFragment.getInstance().handleOperation(op);

        // TO DO -send to widget
    }

    public void play() {
        if (mState.get() == STATE_PAUSED) {
            mState.set(STATE_PLAYING);

            // Play Program
            startProgram();
        } else if (mState.get() == STATE_PLAYING) {
            mState.set(STATE_PAUSED);

            // Pause Program
            pauseProgram();
        }
    }

    /**
     * Skip to Previous Exercise
     */
    public void previous() {
        pauseProgram();

        // Find Index of Current Exercise
        for (int i = mOperationIndex.get() - 1; i > 0; i--) {
            Operation o = mOperations.get(i);
            if (o.getType() == TYPE_TTS_EXERCISE) {
                mOperationIndex.set(i);
                break;
            }
        }

        // Find Index of Previous Exercise
        for (int i = mOperationIndex.get() - 1; i > 0; i--) {
            Operation o = mOperations.get(i);
            if (o.getType() == TYPE_TTS_EXERCISE) {
                mOperationIndex.set(i);
                break;
            }
        }

        startProgram();
    }

    /**
     * Skip to Next Exercise.
     */
    public void next() {
        pauseProgram();

        // Find Index of Next Exercise
        for (int i = mOperationIndex.get(); i < mOperations.size(); i++) {
            Operation o = mOperations.get(i);
            if (o.getType() == TYPE_TTS_EXERCISE) {
                mOperationIndex.set(i);
                break;
            }
        }

        startProgram();
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
                    break;
                case TYPE_TTS_EXERCISE:
                    playSound(e.getName());
                    break;
                case TYPE_TTS_EXERCISE_SETS_AND_REPS:
                    playSound(op.getInfo());
                    break;
                case TYPE_TTS_STOP:
                    playSound(op.getInfo());
                    break;
                case TYPE_TTS_START:
                    playSound(op.getInfo());
                    break;
                case TYPE_TTS_SET:
                    String setNumber = mContext.getResources().getString(R.string.set) + " " + op.getInfo();
                    playSound(setNumber);
                    break;
                case TYPE_TTS_REP:
                    playSound(op.getInfo());
                    break;
                case TYPE_BREAK_UNIT:
                    // Update UI
                    break;
                case TYPE_TTS_PROGRAM_OVER:
                    playSound(op.getInfo());
                    break;
                default:
                    break;
            }

            sendOperation(op);

            mHandler.postDelayed(this, op.getDuration());
        }
    };

    public Program getProgram() {
        return mProgram;
    }

    /**
     * Text to speech - start, stop, exercise name, sets, reps.
     *
     * @param text
     */
    private void playSound(final String text) {
        if (Utils.getProgramTTS(mContext)) {
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    public class LocalBinder extends Binder {
        PlayService getService() {
            return PlayService.this;
        }
    }
}
