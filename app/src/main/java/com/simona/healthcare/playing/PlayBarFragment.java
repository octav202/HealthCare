package com.simona.healthcare.playing;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class PlayBarFragment extends Fragment{

    private static PlayBarFragment sInstance;
    private Context mContext;
    private Program mProgram;
    private ExecutorService mExecutor;

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

        mExecutor.execute(mExerciseRunnable);

    }

    private Runnable mExerciseRunnable = new Runnable() {
        @Override
        public void run() {
            // Exercises
            for (final Exercise e : mProgram.getExercises()) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mExerciseText.setText(e.getName());
                        mTotalText.setText(String.valueOf(e.getSetDuration()));
                        mSeekbar.setMax(e.getSetDuration());
                    }
                });

                // Sets
                for (final AtomicInteger setNumber = new AtomicInteger();
                     setNumber.get() < e.getSets();
                     setNumber.set(setNumber.get() + 1)) {

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mTotalText.setText(String.valueOf(e.getSetDuration()));
                            mSeekbar.setMax(e.getSetDuration());
                            mSetText.setText("Set " + (setNumber.get() + 1) + "/" +e.getSets());
                            mBreakText.setVisibility(View.GONE);
                        }
                    });

                    // TODO Play Sound
                    // Execute Set
                    for (final AtomicInteger reps = new AtomicInteger();
                         reps.get() < e.getSetDuration();
                         reps.set(reps.get() + 1)) {

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mElapsedText.setText(String.valueOf(reps));
                                mSeekbar.setProgress(reps.get());
                            }
                        });

                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                    }

                    // TODO Play break sound
                    // Execute break
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
