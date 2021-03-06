package com.simona.healthcare.recipe;

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

import static com.simona.healthcare.utils.Constants.TYPE_RECIPE;

public class RecipeFragment extends Fragment {

    private static final String TAG = "HEALTH_";

    private ListView mListView;
    private RecipesAdapter mAdapter;
    private Context mContext;
    private EditRecipeDialog mDialog;
    private TextView mNoRecipeText;

    public static RecipeFragment newInstance() {
        RecipeFragment fragment = new RecipeFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        mContext = getActivity().getApplicationContext();
        View view = inflater.inflate(R.layout.recipes_fragment, container, false);
        mListView = view.findViewById(R.id.recipesList);
        mNoRecipeText = view.findViewById(R.id.no_recipe_text);

        // Get all recipes rom database
        List<Recipe> recipes = DatabaseHelper.getInstance(mContext).getRecipes();
        if (recipes == null || recipes.isEmpty()) {
            mNoRecipeText.setVisibility(View.VISIBLE);
            mListView.setVisibility(View.GONE);
        } else {
            mNoRecipeText.setVisibility(View.GONE);
            mListView.setVisibility(View.VISIBLE);
        }

        // Load fetched recipes
        mAdapter = new RecipesAdapter(mContext, recipes);
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mAdapter != null) {
                    Recipe r = (Recipe) mAdapter.getItem(position);
                    edit(r);
                }
            }
        });
        return view;
    }

    /**
     * Add Recipe
     */
    public void add() {

        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }

        mDialog = new EditRecipeDialog(getActivity(), new EditRecipeDialog.AddRecipeCallback() {
            @Override
            public void onRecipeEditDone() {
                // Program added, update list
                List<Recipe> recipes = DatabaseHelper.getInstance(mContext).getRecipes();
                if (recipes == null || recipes.isEmpty()) {
                    mNoRecipeText.setVisibility(View.VISIBLE);
                    mListView.setVisibility(View.GONE);
                } else {
                    mNoRecipeText.setVisibility(View.GONE);
                    mListView.setVisibility(View.VISIBLE);
                }
                mAdapter.setData(recipes);
            }

            @Override
            public void onAddRecipeImage() {
                MainActivity activity = (MainActivity) getActivity();
                activity.openGallery(TYPE_RECIPE);
            }
        }, null);
        mDialog.show();
    }

    /**
     * Edit Recipe
     */
    public void edit(Recipe recipe) {

        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }

        mDialog = new EditRecipeDialog(getActivity(), new EditRecipeDialog.AddRecipeCallback() {
            @Override
            public void onRecipeEditDone() {
                List<Recipe> recipes = DatabaseHelper.getInstance(mContext).getRecipes();
                if (recipes == null || recipes.isEmpty()) {
                    mNoRecipeText.setVisibility(View.VISIBLE);
                    mListView.setVisibility(View.GONE);
                } else {
                    mNoRecipeText.setVisibility(View.GONE);
                    mListView.setVisibility(View.VISIBLE);
                }
                mAdapter.setData(recipes);
            }

            @Override
            public void onAddRecipeImage() {
                MainActivity activity = (MainActivity) getActivity();
                activity.openGallery(TYPE_RECIPE);
            }
        }, recipe);
        mDialog.show();
    }


    public void setDialogImageUri(Uri imageUri) {
        if (mDialog != null) {
            mDialog.setImageUri(imageUri);
        }
    }
}
