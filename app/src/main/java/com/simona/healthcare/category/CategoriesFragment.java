package com.simona.healthcare.category;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.simona.healthcare.MainActivity;
import com.simona.healthcare.R;
import com.simona.healthcare.program.ProgramsFragment;

import java.util.ArrayList;
import java.util.List;

public class CategoriesFragment extends Fragment {

    private static final String TAG = "HEALTH_";

    private GridView mGridView;
    private Context mContext;

    private final int GRID_NUM_COLUMNS = 2;
    private final int GRID_VERTICAL_SPACE = 20;
    private final int GRID_HORIZONTAL_SPACE = 20;

    public static CategoriesFragment newInstance(int selectedId) {
        CategoriesFragment fragment = new CategoriesFragment();
        Bundle args = new Bundle();
        args.putInt("selectedId", selectedId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView()");

        mContext = getActivity().getApplicationContext();
        View view = inflater.inflate(R.layout.categories_fragment, container, false);
        mGridView = view.findViewById(R.id.categoryGrid);

        // Aici o sa fie lista ta de categorii, pe care la un moment dat o sa o iei din baza de date
        List<Category> categories = new ArrayList<Category>();
        categories.add(new Category(1, "Chest", ""));
        categories.add(new Category(2, "Back", ""));
        categories.add(new Category(3, "Arms", ""));
        categories.add(new Category(4, "Core", ""));
        categories.add(new Category(5, "Legs", ""));

        final CategoryAdapter adapter = new CategoryAdapter(mContext, categories);
        mGridView.setAdapter(adapter);

        mGridView.setNumColumns(GRID_NUM_COLUMNS);
        mGridView.setVerticalSpacing(GRID_VERTICAL_SPACE);
        mGridView.setHorizontalSpacing(GRID_HORIZONTAL_SPACE);

        // Grid Item listener
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (adapter != null) {
                   Category selected =  (Category) adapter.getItem(position);

                   // Create an instance of Programs
                   Fragment programs = ProgramsFragment.newInstance(selected.getId());
                   MainActivity activity = (MainActivity) getActivity();
                   //activity.loadFragment(programs);
                }
            }
        });
        return view;
    }
}
