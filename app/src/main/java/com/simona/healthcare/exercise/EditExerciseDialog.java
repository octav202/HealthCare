package com.simona.healthcare.exercise;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.simona.healthcare.R;
import com.simona.healthcare.recipe.Recipe;
import com.simona.healthcare.utils.DatabaseHelper;

import java.io.IOException;

public class EditExerciseDialog extends Dialog {

    private Context mContext;
    private AddExerciseCallback mCallback;
    private ImageView mImageView;
    private EditText mNameText;
    private EditText mSetsText;
    private EditText mRepsText;
    private EditText mBreakText;
    private EditText mDescriptionText;
    private Button mCancelBtn;
    private Button mOkBtn;
    private Button mDeleteButton;
    private String mImagePath;


    public EditExerciseDialog(final Context context, AddExerciseCallback callback, final Exercise exerciseToEdit) {
        super(context, R.style.Theme_AppCompat_Light_Dialog);
        this.setContentView(R.layout.edit_exercise_dialog);

        mContext = context;
        mCallback = callback;
        mImageView = findViewById(R.id.imageView);
        mNameText = findViewById(R.id.nameTextView);
        mSetsText = findViewById(R.id.setsTextView);
        mRepsText = findViewById(R.id.repsTextView);
        mBreakText = findViewById(R.id.breakTextView);
        mDescriptionText = findViewById(R.id.descriptionTextView);

        mCancelBtn = findViewById(R.id.cancelButton);
        mOkBtn = findViewById(R.id.okButton);
        mDeleteButton = findViewById(R.id.deleteButton);

        mImageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    mCallback.onAddRecipeImage();
                }
                return true;
            }
        });

        mOkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = mNameText.getText().toString().trim();
                String sets = mSetsText.getText().toString().trim();
                String reps = mRepsText.getText().toString().trim();
                String breakDuration = mBreakText.getText().toString().trim();
                String description = mDescriptionText.getText().toString().trim();

                if (name == null || name.isEmpty() || sets == null || sets.isEmpty() || reps == null || reps.isEmpty()
                    || breakDuration == null || breakDuration.isEmpty() || description == null || description.isEmpty()) {
                    Toast.makeText(context, "Completati toate campurile, va rog!", Toast.LENGTH_SHORT)
                            .show();
                    return;
                }

                // Create Exercise object using the collected input
                Exercise ex = new Exercise();
                ex.setName(name);
                ex.setSets(Integer.parseInt(sets));
                ex.setRepsPerSet(Integer.parseInt(reps));
                ex.setBreak(Integer.parseInt(breakDuration));
                ex.setDescription(description);

                if (exerciseToEdit !=null) {
                    // Edit Exercise - keep the same id
                    ex.setId(exerciseToEdit.getId());
                }

                if (mImagePath!=null) {
                    ex.setImagePath(mImagePath);
                }

                if (exerciseToEdit != null) {
                    if (DatabaseHelper.getInstance(context).updateExercise(ex)) {
                        mCallback.onExerciseEditDone();
                    } else {
                        Toast.makeText(context, "Editare exercitiu esuata!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (DatabaseHelper.getInstance(context).addExercise(ex)) {
                        mCallback.onExerciseEditDone();
                    } else {
                        Toast.makeText(context, "Adaugare exercitiu esuata!", Toast.LENGTH_SHORT).show();
                    }
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
                if (DatabaseHelper.getInstance(mContext).deleteExercise(exerciseToEdit)) {
                    // Refresh List
                    mCallback.onExerciseEditDone();
                } else {
                    Toast.makeText(mContext, "Stergere exercitiu esuata!", Toast.LENGTH_SHORT).show();
                }

                dismiss();
            }
        });

        // Edit Mode - populate fields
        if (exerciseToEdit != null) {
            mNameText.setText(exerciseToEdit.getName());
            mSetsText.setText(String.valueOf(exerciseToEdit.getSets()));
            mRepsText.setText(String.valueOf(exerciseToEdit.getRepsPerSet()));
            mBreakText.setText(String.valueOf(exerciseToEdit.getBreak()));
            mDescriptionText.setText(exerciseToEdit.getDescription());
            if (exerciseToEdit.getImagePath() != null) {
                mImagePath = exerciseToEdit.getImagePath();
                Uri imageUri = Uri.parse(exerciseToEdit.getImagePath());
                try {
                    Bitmap bitmap = BitmapFactory.decodeStream(mContext.getContentResolver().openInputStream(imageUri));
                    mImageView.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            mDeleteButton.setVisibility(View.INVISIBLE);
        }
    }

    public void setImageUri(Uri imageUri) {
        if (imageUri != null) {
            mImagePath = imageUri.toString();
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(mContext.getContentResolver().openInputStream(imageUri));
                mImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public interface AddExerciseCallback {
        void onExerciseEditDone();
        void onAddRecipeImage();
    }
}
