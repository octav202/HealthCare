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
import android.widget.Toast;

import com.simona.healthcare.MainActivity;
import com.simona.healthcare.R;
import com.simona.healthcare.exercise.ExercisesFragment;
import com.simona.healthcare.utils.DatabaseHelper;

import java.util.List;

public class ProgramsFragment extends Fragment {

    private static final String TAG = "HEALTH_";

    private ListView mListView;
    private ProgramsAdapter mAdapter;
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
                // Position starts with 0, Days start with 1 -> Mon-1, Tue-2, Wed-3
                mAdapter.filterByDay(tab.getPosition() + 1);
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        List<Program> programs = DatabaseHelper.getInstance(mContext).getPrograms();


        mAdapter = new ProgramsAdapter(mContext, programs);
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mAdapter != null) {
                    Toast.makeText(mContext, "Selected " + id, Toast.LENGTH_SHORT).show();
                }
            }
        });

        mListView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(mContext, "Selected " + id, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return view;
    }

    /**
     * Add Program
     */
    public void add() {
        new AddProgramDialog(getActivity(), new AddProgramDialog.AddProgramDialogCallback() {
            @Override
            public void onProgramAdded() {
                // Program added, update list
                List<Program> programs = DatabaseHelper.getInstance(mContext).getPrograms();
                mAdapter.setData(programs);
            }
        }).show();
    }

    public void delete() {

    }

}
