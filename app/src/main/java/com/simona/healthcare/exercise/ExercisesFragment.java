package com.simona.healthcare.exercise;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.simona.healthcare.R;

import java.util.ArrayList;
import java.util.List;

public class ExercisesFragment extends Fragment {

    private static final String TAG = "HEALTH_";

    private ListView mListView;
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

        // TODO - get exercises for a category, this is just dummy data
        List<Exercise> exercises = new ArrayList<Exercise>();
        exercises.add(new Exercise(1, "Name1", "Some description for Exercise1",
                1,1,1,1));
        exercises.add(new Exercise(2, "Name2", "Some description for Exercise2",
                2,2,2,2));
        exercises.add(new Exercise(3, "Name3", "Some description for Exercise3",
                4,3,3,3));
        exercises.add(new Exercise(4, "Name4", "Some description for Exercise4",
                5,4,5,4));
        exercises.add(new Exercise(5, "Name5", "Some description for Exercise5",
                6,5,6,5));

        final ExercisesAdapter adapter = new ExercisesAdapter(mContext, exercises);
        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (adapter != null) {
                    adapter.onItemSelected(position);
                }
            }
        });
        return view;
    }
}
