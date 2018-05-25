package com.simona.healthcare.recipe;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.simona.healthcare.R;
import com.simona.healthcare.utils.DatabaseHelper;

public class EditRecipeDialog extends Dialog {

    private Context mContext;
    private AddRecipeCallback mCallback;
    private ImageView mImageView;
    private EditText mNameText;
    private EditText mTimeText;
    private EditText mDescriptionText;
    private Button mCancelBtn;
    private Button mOkBtn;
    private Button mDeleteButton;
    private String mImagePath;


    public EditRecipeDialog(final Context context, AddRecipeCallback callback, final Recipe recipeToEdit) {
        super(context, R.style.Theme_AppCompat_Light_Dialog);
        this.setContentView(R.layout.edit_recipe_dialog);

        mContext = context;
        mCallback = callback;
        mImageView = findViewById(R.id.imageView);
        mNameText = findViewById(R.id.nameTextView);
        mTimeText = findViewById(R.id.timeTextView);
        mDescriptionText = findViewById(R.id.descriptionTextView);

        mCancelBtn = findViewById(R.id.cancelButton);
        mOkBtn = findViewById(R.id.okButton);
        mDeleteButton = findViewById(R.id.deleteButton);


        mOkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = mNameText.getText().toString();
                String time = mTimeText.getText().toString();
                String description = mDescriptionText.getText().toString();

                if (name == null || name.isEmpty()) {
                    Toast.makeText(context, "Please enter a program name..", Toast.LENGTH_SHORT)
                            .show();
                    return;
                }

                // Edit Recipe
                if (recipeToEdit != null) {
                    // This is edit mode - delete program and add it again with new values
                    if (DatabaseHelper.getInstance(mContext).deleteRecipe(recipeToEdit)) {
                        // Refresh List
                        mCallback.onRecipeEditDone();
                    } else {
                        Toast.makeText(mContext, "Update Program Failed!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                Recipe recipe = new Recipe();
                if (recipeToEdit !=null) {
                    // Edit Program - keep the same id
                    recipe.setId(recipeToEdit.getId());
                }
                recipe.setName(name);
                recipe.setTime(time);
                recipe.setDescription(description);
                if (mImagePath!=null) {
                    recipe.setImagePath(mImagePath);
                }

                if (DatabaseHelper.getInstance(context).addRecipe(recipe)) {
                    mCallback.onRecipeEditDone();
                } else {
                    Toast.makeText(context, "Add Recipe Failed!", Toast.LENGTH_SHORT).show();
                }

                dismiss();
            }
        });

        mCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Delete Program from database
                if (DatabaseHelper.getInstance(mContext).deleteRecipe(recipeToEdit)) {
                    // Refresh List
                    mCallback.onRecipeEditDone();
                } else {
                    Toast.makeText(mContext, "Delete Recipe Failed!", Toast.LENGTH_SHORT).show();
                }

                dismiss();
            }
        });

        // Edit Mode - populate fields
        if (recipeToEdit != null) {
            mNameText.setText(recipeToEdit.getName());
            mDescriptionText.setText(recipeToEdit.getDescription());
            mTimeText.setText(recipeToEdit.getTime());
            if (recipeToEdit.getImagePath() != null) {
                // TODO set image;
            }
        } else {
            mDeleteButton.setVisibility(View.INVISIBLE);
        }
    }
    public interface AddRecipeCallback {
        void onRecipeEditDone();
    }
}
