package com.simona.healthcare.program;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
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
import com.simona.healthcare.exercise.Exercise;
import com.simona.healthcare.exercise.ExercisesFragment;
import com.simona.healthcare.utils.DatabaseHelper;

import java.util.ArrayList;
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
                //adapter.filterAlarms(tab.getPosition() + 1);
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
                    Program selected = (Program) mAdapter.getItem(position);

                    // Create an instance of Programs
                    Fragment programs = ExercisesFragment.newInstance(selected.getId());
                    MainActivity activity = (MainActivity) getActivity();
                    activity.loadFragment(programs);
                }
            }
        });

        return view;
    }

    /**
     * Add Program
     */
    public void add() {
        final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        LinearLayout layout= new LinearLayout(getActivity().getApplicationContext());
        layout.setOrientation(LinearLayout.VERTICAL);

        // Name
        final EditText nameInput = new EditText(getActivity());
        nameInput.setHint(R.string.name);

        layout.addView(nameInput);
        alert.setView(layout);
        alert.setTitle(R.string.add_program);

        alert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                // Collect input
                String name = nameInput.getText().toString().trim();

                // Create Program object using the collected input
                Program prog = new Program();
                prog.setName(name);


                // Add exercise to database
                if (DatabaseHelper.getInstance(mContext).addProgram(prog)) {
                    // Exercise was added - refresh adapter
                    mAdapter.setData(DatabaseHelper.getInstance(mContext).getPrograms());
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

    public void delete() {

    }

}
