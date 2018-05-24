package com.simona.healthcare.exercise;

import android.app.Activity;
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

import com.simona.healthcare.MainActivity;
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

        mAdapter.setCallback(new ExercisesAdapter.ImageCallback() {
            @Override
            public void onImageRequested(int id) {
                // Open Camera/Gallery to pick photo
                MainActivity activity = (MainActivity) getActivity();
                activity.openGallery(id);
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mAdapter != null) {
                    Exercise ex = (Exercise) mAdapter.getItem(position);
                    edit(ex);
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
        setsInput.setInputType(InputType.TYPE_CLASS_NUMBER);

        // Reps
        final EditText repsInput = new EditText(getActivity());
        repsInput.setHint(R.string.reps);
        repsInput.setInputType(InputType.TYPE_CLASS_NUMBER);

        // Break Duration
        final EditText breakInput = new EditText(getActivity());
        breakInput.setHint(R.string.break_duration);
        breakInput.setInputType(InputType.TYPE_CLASS_NUMBER);

        // Description
        final EditText descriptionInput = new EditText(getActivity());
        descriptionInput.setHint(R.string.description);

        layout.addView(nameInput);
        layout.addView(setsInput);
        layout.addView(repsInput);
        layout.addView(breakInput);
        layout.addView(descriptionInput);
        alert.setView(layout);

        alert.setTitle(R.string.add_exercise);

        // Ok Button
        alert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                // Collect input
                String name = nameInput.getText().toString().trim();
                String sets = setsInput.getText().toString().trim();
                String reps = repsInput.getText().toString().trim();
                String breakDuration = breakInput.getText().toString().trim();
                String description = descriptionInput.getText().toString().trim();

                // Create Exercise object using the collected input
                Exercise ex = new Exercise();
                ex.setName(name);
                ex.setSets(Integer.parseInt(sets));
                ex.setRepsPerSet(Integer.parseInt(reps));
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

        // Cancel Button
        alert.setNegativeButton(R.string.cancel,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                });
        alert.create().show();
    }

    private void edit(final Exercise exercise) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        LinearLayout layout= new LinearLayout(getActivity().getApplicationContext());
        layout.setOrientation(LinearLayout.VERTICAL);

        // Name
        final EditText nameInput = new EditText(getActivity());
        nameInput.setText(exercise.getName());

        // Sets
        final EditText setsInput = new EditText(getActivity());
        setsInput.setText(String.valueOf(exercise.getSets()));

        // Reps
        final EditText repsInput = new EditText(getActivity());
        repsInput.setText(String.valueOf(exercise.getRepsPerSet()));

        // Break Duration
        final EditText breakInput = new EditText(getActivity());
        breakInput.setText(String.valueOf(exercise.getBreak()));

        // Description
        final EditText descriptionInput = new EditText(getActivity());
        descriptionInput.setText(exercise.getDescription());

        layout.addView(nameInput);
        layout.addView(setsInput);
        layout.addView(repsInput);
        layout.addView(breakInput);
        layout.addView(descriptionInput);
        alert.setView(layout);

        alert.setTitle(R.string.add_exercise);

        // Ok Button
        alert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                // Collect input
                String name = nameInput.getText().toString().trim();
                String sets = setsInput.getText().toString().trim();
                String reps = repsInput.getText().toString().trim();
                String breakDuration = breakInput.getText().toString().trim();
                String description = descriptionInput.getText().toString().trim();

                // Create Exercise object using the collected input
                exercise.setName(name);
                exercise.setSets(Integer.parseInt(sets));
                exercise.setRepsPerSet(Integer.parseInt(reps));
                exercise.setBreak(Integer.parseInt(breakDuration));
                exercise.setDescription(description);

                // Add exercise to database
                if (DatabaseHelper.getInstance(mContext).updateExercise(exercise)) {
                    // Exercise was added - refresh adapter
                    mAdapter.setData(DatabaseHelper.getInstance(mContext).getExercises());
                } else {
                    Toast.makeText(mContext, "Add Exercise Failed!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Delete Button
        alert.setNegativeButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Delete exercise from database
                if (DatabaseHelper.getInstance(mContext).deleteExercise(exercise)) {
                    // Exercise was deleted - refresh adapter
                    mAdapter.setData(DatabaseHelper.getInstance(mContext).getExercises());
                } else {
                    Toast.makeText(mContext, "Add Exercise Failed!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Cancel Button
        alert.setNeutralButton(R.string.cancel,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                });

        alert.create().show();
    }

    /**
     * Called from the activity after an image was stored for an exercise
     */
    public void updateAdapter() {
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }
}
