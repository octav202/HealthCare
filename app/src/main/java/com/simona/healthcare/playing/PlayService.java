package com.simona.healthcare.playing;

import android.app.Notification;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.simona.healthcare.R;
import com.simona.healthcare.exercise.Exercise;
import com.simona.healthcare.program.Program;
import com.simona.healthcare.utils.Constants;
import com.simona.healthcare.utils.DatabaseHelper;
import com.simona.healthcare.utils.Utils;
import com.simona.healthcare.widget.PlayWidgetProvider;

import java.util.ArrayList;
import java.util.Locale;
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
import static com.simona.healthcare.utils.Constants.ACTION_WIDGET_NEXT;
import static com.simona.healthcare.utils.Constants.ACTION_WIDGET_PLAY;
import static com.simona.healthcare.utils.Constants.ACTION_WIDGET_PREVIOUS;
import static com.simona.healthcare.utils.Constants.SERVICE_ACTION_EXTRA;
import static com.simona.healthcare.utils.Constants.STATE_PAUSED;
import static com.simona.healthcare.utils.Constants.STATE_PLAYING;
import static com.simona.healthcare.utils.Constants.STATE_STOPPED;
import static com.simona.healthcare.utils.Constants.TAG;

public class PlayService extends Service {

    public static final String TAG = Constants.TAG + PlayService.class.getSimpleName();
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
        startForeground(1,new Notification());

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

        String action = intent.getStringExtra(SERVICE_ACTION_EXTRA);

        if (action != null) {
            switch (action) {
                case ACTION_WIDGET_PREVIOUS:
                    previous();
                    break;
                case ACTION_WIDGET_PLAY:
                    play();
                    break;
                case ACTION_WIDGET_NEXT:
                    next();
                    break;
                default:
                    break;
            }
        }

        return START_STICKY;
    }

    /**
     * Set Current Program.
     * Set null for no program.
     * @param program
     */
    public synchronized void setProgram(Program program) {

        stopProgram();
        mProgram = program;
        if (program != null) {
            PlayBarFragment.getInstance().setupViewForProgram();
            updateWidget(new Operation(TYPE_TTS_PROGRAM, 0, program.getName(), null));
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

        if (mProgram == null) {
            Log.e(TAG, "No Program Selected");
            return;
        }

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
        Log.d(TAG, "sendState() " + state);
        PlayBarFragment.getInstance().setState(state);
        updateWidget(null);

    }

    private void sendOperation(Operation op) {
        Log.d(TAG, "sendOperation() " + op);
        PlayBarFragment.getInstance().handleOperation(op);
        updateWidget(op);

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

        if (mProgram == null) {
            Log.e(TAG, "No Program Selected");
            return;
        }

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

        if (mProgram == null) {
            Log.e(TAG, "No Program Selected");
            return;
        }

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
                    playSound(text, op.getType());
                    break;
                case TYPE_TTS_EXERCISE:
                    playSound(e.getName(), op.getType());
                    break;
                case TYPE_TTS_EXERCISE_SETS_AND_REPS:
                    playSound(op.getInfo(), op.getType());
                    break;
                case TYPE_TTS_STOP:
                    playSound(op.getInfo(), op.getType());
                    break;
                case TYPE_TTS_START:
                    playSound(op.getInfo(), op.getType());
                    break;
                case TYPE_TTS_SET:
                    String setNumber = mContext.getResources().getString(R.string.set) + " " + op.getInfo();
                    playSound(setNumber, op.getType());
                    break;
                case TYPE_TTS_REP:
                    playSound(op.getInfo(), op.getType());
                    break;
                case TYPE_BREAK_UNIT:
                    playSound(op.getInfo(), op.getType());
                    break;
                case TYPE_TTS_PROGRAM_OVER:
                    playSound(op.getInfo(), op.getType());
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

    public Operation getOperation() {
        if (mProgram !=null && mOperations != null) {
            return mOperations.get(mOperationIndex.get());
        }

        return null;
    }


    private void playSound(final String text, int type) {
        if (Utils.getProgramTTS(mContext)) {

            switch (type) {
                case TYPE_TTS_REP:
                    if (!Utils.getRepsTTS(mContext)) {
                        return;
                    }
                    break;
                case TYPE_BREAK_UNIT:
                    if (!Utils.getBreakTTS(mContext)) {
                        return;
                    }
                    break;
                default:
                    break;
            }

            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        }
    }


    public class LocalBinder extends Binder {
        PlayService getService() {
            return PlayService.this;
        }
    }

    /**
     * Update Widget
     * @param operation
     */
    private void updateWidget(Operation operation) {

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(mContext);
        RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.widget_layout);
        ComponentName thisWidget = new ComponentName(mContext, PlayWidgetProvider.class);

        // Handle Button States
        switch (mState.get()) {
            case STATE_PAUSED:
                remoteViews.setImageViewResource(R.id.playButton,R.drawable.ic_play);
                remoteViews.setViewVisibility(R.id.noProgramLayout, View.GONE);
                remoteViews.setViewVisibility(R.id.currentProgramLayout, View.VISIBLE);
                break;
            case STATE_STOPPED:
                remoteViews.setImageViewResource(R.id.playButton,R.drawable.ic_play);
                remoteViews.setViewVisibility(R.id.noProgramLayout, View.VISIBLE);
                remoteViews.setViewVisibility(R.id.currentProgramLayout, View.GONE);
                break;
            case STATE_PLAYING:
                remoteViews.setImageViewResource(R.id.playButton,R.drawable.ic_pause);
                remoteViews.setViewVisibility(R.id.noProgramLayout, View.GONE);
                remoteViews.setViewVisibility(R.id.currentProgramLayout, View.VISIBLE);
                break;
            default:
                break;
        }

        // Handle TextViews
        if (operation != null) {

            Exercise e = operation.getExercise();

            switch (operation.getType()) {
                case TYPE_TTS_PROGRAM:

                    remoteViews.setViewVisibility(R.id.noProgramLayout, View.GONE);
                    remoteViews.setViewVisibility(R.id.currentProgramLayout, View.VISIBLE);

                    remoteViews.setTextViewText(R.id.programText, operation.getInfo());
                    remoteViews.setTextViewText(R.id.exerciseText, "");
                    remoteViews.setTextViewText(R.id.setText, "");
                    break;
                case TYPE_TTS_EXERCISE:
                    remoteViews.setTextViewText(R.id.exerciseText, e.getName());
                    remoteViews.setTextViewText(R.id.currentRep, "");
                    remoteViews.setTextViewText(R.id.setText, "Set " + "1/" + e.getSets());
                    break;
                case TYPE_TTS_EXERCISE_SETS_AND_REPS:
                    break;
                case TYPE_TTS_STOP:
                    remoteViews.setViewVisibility(R.id.currentRep, View.VISIBLE);
                    remoteViews.setTextViewText(R.id.currentRep, mContext.getResources().getString(R.string.break_text));
                    break;
                case TYPE_TTS_START:
                    break;
                case TYPE_TTS_SET:
                    remoteViews.setTextViewText(R.id.setText, ("Set " + operation.getInfo()) + "/" + e.getSets());
                    remoteViews.setTextViewText(R.id.currentRep, "");
                    remoteViews.setTextViewText(R.id.exerciseText, e.getName());
                    break;
                case TYPE_TTS_REP:
                    remoteViews.setTextViewText(R.id.currentRep, operation.getInfo() + "/" + e.getRepsPerSet());
                    remoteViews.setTextViewText(R.id.exerciseText, e.getName());
                    break;
                case TYPE_BREAK_UNIT:
                    remoteViews.setTextViewText(R.id.exerciseText, mContext.getResources().getString(R.string.break_text));
                    break;
                case TYPE_TTS_PROGRAM_OVER:
                    remoteViews.setViewVisibility(R.id.noProgramLayout, View.VISIBLE);
                    remoteViews.setViewVisibility(R.id.currentProgramLayout, View.GONE);
                    break;
                default:
                    break;
            }


        }
        appWidgetManager.updateAppWidget(thisWidget, remoteViews);

        // Program Over - stop service
        if (operation != null && operation.getType() == TYPE_TTS_PROGRAM_OVER) {
            stopSelf();
        }
    }
}
