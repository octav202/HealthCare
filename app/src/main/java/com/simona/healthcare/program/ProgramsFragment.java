package com.simona.healthcare.program;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.simona.healthcare.MainActivity;
import com.simona.healthcare.R;
import com.simona.healthcare.exercise.ExercisesFragment;

import java.util.ArrayList;

public class ProgramsFragment extends Fragment {

    private static final String TAG = "HEALTH_";

    private ListView mListView;
    private Context mContext;
    private int mProgramId;

    public static ProgramsFragment newInstance(int selectedId) {
        ProgramsFragment fragment = new ProgramsFragment();
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
        View view = inflater.inflate(R.layout.programs_fragment, container, false);
        mListView = view.findViewById(R.id.programsList);


        TabLayout tabLayout = (TabLayout)view.findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Mo"));
        tabLayout.addTab(tabLayout.newTab().setText("Tu"));
        tabLayout.addTab(tabLayout.newTab().setText("We"));
        tabLayout.addTab(tabLayout.newTab().setText("Th"));
        tabLayout.addTab(tabLayout.newTab().setText("Fr"));
        tabLayout.addTab(tabLayout.newTab().setText("Sa"));
        tabLayout.addTab(tabLayout.newTab().setText("Su"));
        tabLayout.addTab(tabLayout.newTab().setText("All"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //adapter.filterAlarms(tab.getPosition() + 1);
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        ArrayList<Program> programs = new ArrayList<Program>();
        programs.add(new Program(1, "Program1"));
        programs.add(new Program(2, "Program2"));
        programs.add(new Program(3, "Program3"));
        programs.add(new Program(4, "Program4"));
        programs.add(new Program(5, "Program5"));

        final ProgramsAdapter adapter = new ProgramsAdapter(mContext, programs);
        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (adapter != null) {
                    Program selected = (Program) adapter.getItem(position);

                    // Create an instance of Programs
                    Fragment programs = ExercisesFragment.newInstance(selected.getId());
                    MainActivity activity = (MainActivity) getActivity();
                    //activity.loadFragment(programs);
                }
            }
        });

        return view;
    }

}
