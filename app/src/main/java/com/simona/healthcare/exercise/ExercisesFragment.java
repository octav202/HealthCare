package com.simona.healthcare.exercise;

import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

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
    private int mProgramId;
    private EditExerciseDialog mDialog;

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
                List<Exercise> recipes = DatabaseHelper.getInstance(mContext).getExercises();
                mAdapter.setData(recipes);
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
                List<Exercise> recipes = DatabaseHelper.getInstance(mContext).getExercises();
                mAdapter.setData(recipes);
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

    /**
     * Called from the activity after an image was stored for an exercise
     */
    public void updateAdapter() {
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }
}
