package com.simona.healthcare.exercise;

import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.simona.healthcare.MainActivity;
import com.simona.healthcare.R;
import com.simona.healthcare.utils.DatabaseHelper;

import java.util.List;

import static com.simona.healthcare.utils.Constants.TYPE_EXERCISE;

public class ExercisesFragment extends Fragment {

    private static final String TAG = "HEALTH_";

    private ListView mListView;
    private ExercisesAdapter mAdapter;
    private Context mContext;
    private EditExerciseDialog mDialog;
    private TextView mNoExerciseText;

    public static ExercisesFragment newInstance() {
        ExercisesFragment fragment = new ExercisesFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        mContext = getActivity().getApplicationContext();
        View view = inflater.inflate(R.layout.exercises_fragment, container, false);
        mListView = view.findViewById(R.id.exercisesList);
        mNoExerciseText = view.findViewById(R.id.no_exercise_text);

        // Get all exercises from database
        List<Exercise> exercises = DatabaseHelper.getInstance(mContext).getExercises();

        if (exercises == null || exercises.isEmpty()) {
            mNoExerciseText.setVisibility(View.VISIBLE);
            mListView.setVisibility(View.GONE);
        } else {
            mNoExerciseText.setVisibility(View.GONE);
            mListView.setVisibility(View.VISIBLE);
        }

        // Load fetched exercises
        mAdapter = new ExercisesAdapter(mContext, exercises);
        mListView.setAdapter(mAdapter);

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

        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }

        mDialog = new EditExerciseDialog(getActivity(), new EditExerciseDialog.AddExerciseCallback() {
            @Override
            public void onExerciseEditDone() {
                List<Exercise> exercises = DatabaseHelper.getInstance(mContext).getExercises();

                if (exercises == null || exercises.isEmpty()) {
                    mNoExerciseText.setVisibility(View.VISIBLE);
                    mListView.setVisibility(View.GONE);
                } else {
                    mNoExerciseText.setVisibility(View.GONE);
                    mListView.setVisibility(View.VISIBLE);
                }
                mAdapter.setData(exercises);
            }

            @Override
            public void onAddRecipeImage() {
                MainActivity activity = (MainActivity) getActivity();
                activity.openGallery(TYPE_EXERCISE);
            }
        }, null);
        mDialog.show();
    }

    /**
     * Edit Exercise
     */
    public void edit(Exercise exercise) {

        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }

        mDialog = new EditExerciseDialog(getActivity(), new EditExerciseDialog.AddExerciseCallback() {
            @Override
            public void onExerciseEditDone() {
                List<Exercise> exercises = DatabaseHelper.getInstance(mContext).getExercises();

                if (exercises == null || exercises.isEmpty()) {
                    mNoExerciseText.setVisibility(View.VISIBLE);
                    mListView.setVisibility(View.GONE);
                } else {
                    mNoExerciseText.setVisibility(View.GONE);
                    mListView.setVisibility(View.VISIBLE);
                }
                mAdapter.setData(exercises);
            }

            @Override
            public void onAddRecipeImage() {
                MainActivity activity = (MainActivity) getActivity();
                activity.openGallery(TYPE_EXERCISE);
            }
        }, exercise);
        mDialog.show();
    }


    public void setDialogImageUri(Uri imageUri) {
        if (mDialog != null) {
            mDialog.setImageUri(imageUri);
        }
    }
}
