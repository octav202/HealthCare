package com.simona.healthcare.recipe;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.simona.healthcare.MainActivity;
import com.simona.healthcare.R;
import com.simona.healthcare.exercise.Exercise;
import com.simona.healthcare.exercise.ExercisesAdapter;
import com.simona.healthcare.utils.DatabaseHelper;

import java.util.List;

public class RecipeFragment extends Fragment {

    private static final String TAG = "HEALTH_";

    private ListView mListView;
    private ExercisesAdapter mAdapter;
    private Context mContext;

    public static RecipeFragment newInstance(int selectedId) {
        RecipeFragment fragment = new RecipeFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        mContext = getActivity().getApplicationContext();
        View view = inflater.inflate(R.layout.recipes_fragment, container, false);
        mListView = view.findViewById(R.id.recipesList);

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
