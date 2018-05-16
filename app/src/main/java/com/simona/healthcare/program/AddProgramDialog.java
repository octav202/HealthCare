package com.simona.healthcare.program;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.simona.healthcare.R;
import com.simona.healthcare.exercise.Exercise;
import com.simona.healthcare.utils.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

import static com.simona.healthcare.utils.Constants.TAG;

public class AddProgramDialog extends Dialog {

    private Context mContext;
    private AddProgramDialogCallback mCallback;
    private EditText mNameText;
    private ListView mListView;
    private ProgramExercisesAdapter mAdapter;
    private Button mCancelBtn;
    private Button mOkBtn;

    // Days
    private Button mMonBtn;
    private Button mTueBtn;
    private Button mWedButton;
    private Button mThuButton;
    private Button mFriButton;
    private Button mSatButton;
    private Button mSunButton;
    private List<Button> mDays;

    public AddProgramDialog(final Context context, AddProgramDialogCallback callback) {
        super(context, R.style.Theme_AppCompat_Light_Dialog);
        this.setContentView(R.layout.add_program_dialog);

        mContext = context;
        mCallback = callback;
        mNameText = findViewById(R.id.programNameText);
        mListView = findViewById(R.id.addExerciseList);
        mCancelBtn = findViewById(R.id.cancelButton);
        mOkBtn = findViewById(R.id.okButton);
        mMonBtn = findViewById(R.id.monday);
        mTueBtn = findViewById(R.id.tuesday);
        mWedButton = findViewById(R.id.wednesday);
        mThuButton = findViewById(R.id.thursday);
        mFriButton = findViewById(R.id.friday);
        mSatButton = findViewById(R.id.saturday);
        mSunButton = findViewById(R.id.sunday);
        initButtonListeners();

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

                // Program Days
                List<Integer> days = new ArrayList<>();
                for (Button b : mDays) {
                    if (b.isSelected()) {
                        days.add(Integer.parseInt(b.getTag().toString()));
                    }
                }

                if (days.size() == 0) {
                    Toast.makeText(context, "Please select at least one day..", Toast.LENGTH_SHORT)
                            .show();
                    return;
                }

                Program prog = new Program();
                prog.setName(name);
                prog.setExercises(mAdapter.getSelectedItems());
                prog.setDays(days);
                if (DatabaseHelper.getInstance(context).addProgram(prog)) {
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

    private void initButtonListeners() {

        mDays = new ArrayList<>();
        mDays.add(mMonBtn);
        mDays.add(mTueBtn);
        mDays.add(mWedButton);
        mDays.add(mThuButton);
        mDays.add(mFriButton);
        mDays.add(mSatButton);
        mDays.add(mSunButton);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button b = (Button) v;
                b.setSelected(!b.isSelected());
                if (b.isSelected()) {
                    b.setBackgroundColor(mContext.getResources().getColor(R.color.colorAccent));
                } else {
                    b.setBackgroundColor(Color.GRAY);
                }
            }
        };

        for (Button b:mDays) {
            b.setOnClickListener(listener);
        }

    }

    public interface AddProgramDialogCallback {
        void onProgramAdded();
    }
}
