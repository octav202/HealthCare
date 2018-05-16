package com.simona.healthcare.program;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.simona.healthcare.R;
import com.simona.healthcare.exercise.Exercise;
import com.simona.healthcare.utils.DatabaseHelper;

import java.util.List;

public class AddProgramDialog extends Dialog {

    private AddProgramDialogCallback mCallback;
    private EditText mNameText;
    private ListView mListView;
    private ProgramExercisesAdapter mAdapter;
    private Button mCancelBtn;
    private Button mOkBtn;

    public AddProgramDialog(final Context context, AddProgramDialogCallback callback) {
        super(context, R.style.Theme_AppCompat_Light_Dialog);
        this.setContentView(R.layout.add_dialog);

        setTitle(R.string.add_program);

        mCallback = callback;
        mNameText = findViewById(R.id.programNameText);
        mListView = findViewById(R.id.addExerciseList);
        mCancelBtn = findViewById(R.id.cancelButton);
        mOkBtn = findViewById(R.id.okButton);

        // Get all exercises from database
        List<Exercise> mExercises = DatabaseHelper.getInstance(context).getExercises();
        mAdapter = new ProgramExercisesAdapter(context, mExercises);
        mListView.setAdapter(mAdapter);

        mOkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Program Name
                String name = mNameText.getText().toString();
                if (name == null || name.isEmpty()) {
                    Toast.makeText(context, "Please enter a program name..", Toast.LENGTH_SHORT)
                            .show();
                    return;
                }

                // Program Exercises
                if (mAdapter.getSelectedItems().size() == 0){
                    Toast.makeText(context, "Please select some exercises..", Toast.LENGTH_SHORT)
                            .show();
                    return;
                }


                Program prog = new Program();
                prog.setName(name);
                if (DatabaseHelper.getInstance(context).addProgram(prog, mAdapter.getSelectedItems())) {
                    mCallback.onProgramAdded();
                } else {
                    Toast.makeText(context, "Add Exercise Failed!", Toast.LENGTH_SHORT).show();
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
    }

    public interface AddProgramDialogCallback {
        void onProgramAdded();
    }
}
