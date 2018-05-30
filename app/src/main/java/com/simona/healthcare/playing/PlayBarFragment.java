package com.simona.healthcare.playing;

import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
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

import java.util.concurrent.ExecutorService;
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
import static com.simona.healthcare.utils.Constants.STATE_PAUSED;
import static com.simona.healthcare.utils.Constants.STATE_PLAYING;
import static com.simona.healthcare.utils.Constants.STATE_STOPPED;
import static com.simona.healthcare.utils.Constants.TAG;


public class PlayBarFragment extends Fragment{

    private static PlayBarFragment sInstance;
    private Context mContext;
    private Program mProgram;
    private PlayService mService;

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

    // State
    private AtomicInteger mState = new AtomicInteger();
    private boolean mVisible;

    public static PlayBarFragment getInstance() {
        if (sInstance == null) {
            sInstance = new PlayBarFragment();
        }

        return sInstance;
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = ((PlayService.LocalBinder) service).getService();
            mProgram = mService.getProgram();
            setupViewForProgram();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        mVisible = true;
        mContext = getActivity().getApplicationContext();
        mContext.bindService(new Intent(mContext, PlayService.class),
                mConnection, Context.BIND_AUTO_CREATE);

        View view = inflater.inflate(R.layout.playbar_fragment, container, false);

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

        mPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mService != null) {
                    mService.previous();
                }
            }
        });

        mPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mService != null) {
                    mService.play();
                }
            }
        });

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mService != null) {
                    mService.next();
                }
            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mVisible = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mContext.unbindService(mConnection);
    }

    /**
     * Set Current Program.
     * Set null for no program.
     * @param program
     */
    public void setProgram(Program program) {
        if (mService != null) {
            mService.setProgram(program);
        }
        mProgram = program;
        setupViewForProgram();

    }

    public void stopProgram() {
        Log.d(TAG, "stopProgram()");
        if (mService != null) {
            mService.stopProgram();
        }

    }

    public Program getProgram() {
        return mProgram;
    }

    /**
     * Display program information.
     */
    public void setupViewForProgram() {
        if (mProgram != null) {
            mCurrentProgramLayout.setVisibility(View.VISIBLE);
            mNoProgramText.setVisibility(View.GONE);
            mProgramText.setText(mProgram.getName());
        } else {
            mCurrentProgramLayout.setVisibility(View.GONE);
            mNoProgramText.setVisibility(View.VISIBLE);
        }
    }

    public void setState(int state) {
        switch (state) {
            case STATE_PAUSED:
            case STATE_STOPPED:
                mPlayButton.setBackground(mContext.getResources().getDrawable(R.drawable.ic_play));
                break;
            case STATE_PLAYING:
                mPlayButton.setBackground(mContext.getResources().getDrawable(R.drawable.ic_pause));
                break;
            default:
                break;
        }
    }

    public void handleOperation(Operation op) {
        Log.d(TAG, "handleOperation() : " + op);
        Exercise e = op.getExercise();

        switch (op.getType()) {
            case TYPE_TTS_PROGRAM:
                mProgramText.setText(op.getInfo());
                mExerciseText.setText("");
                mSeekbar.setProgress(0);
                mSetText.setText("");
                mElapsedText.setText("");
                mTotalText.setText("");
                break;
            case TYPE_TTS_EXERCISE:
                mExerciseText.setText(e.getName());
                mElapsedText.setText("0");
                mTotalText.setText(String.valueOf(e.getRepsPerSet()));
                mSetText.setText("Set " + "1 /" + e.getSets());
                mSeekbar.setProgress(0);
                mSeekbar.setMax(e.getRepsPerSet());
                break;
            case TYPE_TTS_EXERCISE_SETS_AND_REPS:
                break;
            case TYPE_TTS_STOP:
                mBreakText.setVisibility(View.VISIBLE);
                break;
            case TYPE_TTS_START:
                mBreakText.setVisibility(View.GONE);
                break;
            case TYPE_TTS_SET:
                mElapsedText.setText("0");
                mTotalText.setText(String.valueOf(e.getRepsPerSet()));
                mSeekbar.setMax(e.getRepsPerSet());
                mSetText.setText("Set " + (op.getInfo()) + "/" + e.getSets());
                mBreakText.setVisibility(View.GONE);
                mSeekbar.setProgress(0);
                break;
            case TYPE_TTS_REP:
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
                break;
            case TYPE_TTS_PROGRAM_OVER:
                mBreakText.setVisibility(View.GONE);
                mCurrentProgramLayout.setVisibility(View.GONE);
                mNoProgramText.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }

    }
}
