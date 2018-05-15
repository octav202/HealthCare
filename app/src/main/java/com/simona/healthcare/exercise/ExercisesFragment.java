package com.simona.healthcare.exercise;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.simona.healthcare.R;
import com.simona.healthcare.utils.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class ExercisesFragment extends Fragment {

    private static final String TAG = "HEALTH_";

    private ListView mListView;
    private ExercisesAdapter mAdapter;
    private Context mContext;
    private int mProgramId;

    public static ExercisesFragment newInstance(int selectedId) {
        ExercisesFragment fragment = new ExercisesFragment();
        Bundle args = new Bundle();
        args.putInt("selectedId", selectedId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mProgramId = getArguments().getInt("selectedId");
        Log.d(TAG, "onCreateView() " + mProgramId);

        mContext = getActivity().getApplicationContext();
        View view = inflater.inflate(R.layout.exercises_fragment, container, false);
        mListView = view.findViewById(R.id.exercisesList);

        // Get all exercises from database
        List<Exercise> mExercises = DatabaseHelper.getInstance(mContext).getExercises();

        // Load fetched exercises
        mAdapter = new ExercisesAdapter(mContext, mExercises);
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mAdapter != null) {
                    mAdapter.onItemSelected(position);
                }
            }
        });
        return view;
    }

    /**
     * Add Exercise
     */
    public void add() {
        final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        LinearLayout layout= new LinearLayout(getActivity().getApplicationContext());
        layout.setOrientation(LinearLayout.VERTICAL);

        // Name
        final EditText nameInput = new EditText(getActivity());
        nameInput.setHint(R.string.name);

        // Sets
        final EditText setsInput = new EditText(getActivity());
        setsInput.setHint(R.string.sets);

        // Reps
        final EditText repsInput = new EditText(getActivity());
        repsInput.setHint(R.string.reps);

        // Set Duration
        final EditText setDurationInput = new EditText(getActivity());
        setDurationInput.setHint(R.string.set_duration);

        // Break Duration
        final EditText breakInput = new EditText(getActivity());
        breakInput.setHint(R.string.break_duration);

        // Description
        final EditText descriptionInput = new EditText(getActivity());
        descriptionInput.setHint(R.string.description);

        layout.addView(nameInput);
        layout.addView(setsInput);
        layout.addView(repsInput);
        layout.addView(setDurationInput);
        layout.addView(breakInput);
        layout.addView(descriptionInput);
        alert.setView(layout);

        alert.setTitle(R.string.add_exercise);

        alert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                // Collect input
                String name = nameInput.getText().toString().trim();
                String sets = setsInput.getText().toString().trim();
                String reps = repsInput.getText().toString().trim();
                String setDuration = setDurationInput.getText().toString().trim();
                String breakDuration = breakInput.getText().toString().trim();
                String description = descriptionInput.getText().toString().trim();

                // Create Exercise object using the collected input
                Exercise ex = new Exercise();
                ex.setName(name);
                ex.setSets(Integer.parseInt(sets));
                ex.setRepsPerSet(Integer.parseInt(reps));
                ex.setSetDuration(Integer.parseInt(setDuration));
                ex.setBreak(Integer.parseInt(breakDuration));
                ex.setDescription(description);

                // Add exercise to database
                if (DatabaseHelper.getInstance(mContext).addExercise(ex)) {
                    // Exercise was added - refresh adapter
                    mAdapter.setData(DatabaseHelper.getInstance(mContext).getExercises());
                } else {
                    Toast.makeText(mContext, "Add Exercise Failed!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        alert.setNegativeButton(R.string.cancel,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                });
        alert.create().show();
    }

    /**
     * Delete Exercise
     */
    public void delete() {

    }
}
