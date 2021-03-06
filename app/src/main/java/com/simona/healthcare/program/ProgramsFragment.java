package com.simona.healthcare.program;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.simona.healthcare.R;
import com.simona.healthcare.utils.Constants;
import com.simona.healthcare.utils.DatabaseHelper;

import java.util.List;

public class ProgramsFragment extends Fragment {

    public static final String TAG = Constants.TAG + ProgramsFragment.class.getSimpleName();

    private ListView mListView;
    private ProgramsAdapter mAdapter;
    private Context mContext;
    private TextView mNoProgramText;

    public static ProgramsFragment newInstance() {
        ProgramsFragment fragment = new ProgramsFragment();
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        mContext = getActivity().getApplicationContext();
        View view = inflater.inflate(R.layout.programs_fragment, container, false);
        mListView = view.findViewById(R.id.programsList);
        mNoProgramText = view.findViewById(R.id.no_program_text);

        TabLayout tabLayout = (TabLayout)view.findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Lu"));
        tabLayout.addTab(tabLayout.newTab().setText("Ma"));
        tabLayout.addTab(tabLayout.newTab().setText("Mi"));
        tabLayout.addTab(tabLayout.newTab().setText("Jo"));
        tabLayout.addTab(tabLayout.newTab().setText("Vi"));
        tabLayout.addTab(tabLayout.newTab().setText("Sa"));
        tabLayout.addTab(tabLayout.newTab().setText("Du"));
        tabLayout.addTab(tabLayout.newTab().setText("*"));
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

        if (programs == null || programs.isEmpty()) {
            mNoProgramText.setVisibility(View.VISIBLE);
            mListView.setVisibility(View.GONE);
        } else {
            mNoProgramText.setVisibility(View.GONE);
            mListView.setVisibility(View.VISIBLE);
        }

        mAdapter = new ProgramsAdapter(mContext, programs);
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Program prog  = (Program) mAdapter.getItem(position);
                edit(prog);
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Add Program
     */
    public void add() {
        new EditProgramDialog(getActivity(), new EditProgramDialog.AddProgramDialogCallback() {
            @Override
            public void onProgramEditDone() {
                // Program added, update list
                List<Program> programs = DatabaseHelper.getInstance(mContext).getPrograms();
                if (programs == null || programs.isEmpty()) {
                    mNoProgramText.setVisibility(View.VISIBLE);
                    mListView.setVisibility(View.GONE);
                } else {
                    mNoProgramText.setVisibility(View.GONE);
                    mListView.setVisibility(View.VISIBLE);
                }
                mAdapter.setData(programs);
            }
        }, null).show();
    }

    /**
     * Edit Program
     */
    public void edit(Program program) {
        new EditProgramDialog(getActivity(), new EditProgramDialog.AddProgramDialogCallback() {
            @Override
            public void onProgramEditDone() {
                // Program added, update list
                List<Program> programs = DatabaseHelper.getInstance(mContext).getPrograms();
                if (programs == null || programs.isEmpty()) {
                    mNoProgramText.setVisibility(View.VISIBLE);
                    mListView.setVisibility(View.GONE);
                } else {
                    mNoProgramText.setVisibility(View.GONE);
                    mListView.setVisibility(View.VISIBLE);
                }
                mAdapter.setData(programs);
            }
        }, program).show();
    }

    public void refreshPrograms() {
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }
}
