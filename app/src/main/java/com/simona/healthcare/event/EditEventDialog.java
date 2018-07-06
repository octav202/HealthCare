package com.simona.healthcare.event;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.simona.healthcare.R;
import com.simona.healthcare.utils.DatabaseHelper;

public class EditEventDialog extends Dialog {

    private Context mContext;
    private AddEventCallback mCallback;

    private EditText mNameText;
    private EditText mDescriptionText;
    private EditText mIntervalText;
    private CheckBox mActiveCheck;

    private Button mCancelBtn;
    private Button mOkBtn;
    private Button mDeleteButton;


    public EditEventDialog(final Context context, AddEventCallback callback, final Event eventToEdit) {
        super(context, R.style.Theme_AppCompat_Light_Dialog);

        // Set transparent background - used for rounded corners shape
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        View v = getWindow().getDecorView();
        v.setBackgroundResource(android.R.color.transparent);

        this.setContentView(R.layout.edit_event_dialog);

        mContext = context;
        mCallback = callback;

        mNameText = findViewById(R.id.nameTextView);
        mIntervalText = findViewById(R.id.intervalTextView);
        mDescriptionText = findViewById(R.id.descriptionTextView);
        mActiveCheck = findViewById(R.id.activeCheckBox);

        mCancelBtn = findViewById(R.id.cancelButton);
        mOkBtn = findViewById(R.id.okButton);
        mDeleteButton = findViewById(R.id.deleteButton);


        mOkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = mNameText.getText().toString();
                String description = mDescriptionText.getText().toString();
                String interval = mIntervalText.getText().toString();
                Boolean active = mActiveCheck.isChecked();

                if (name == null || name.isEmpty()
                        || description == null || description.isEmpty()
                        || interval == null || interval.isEmpty() ) {
                    Toast.makeText(context, "Completati toate campurile, va rog!", Toast.LENGTH_SHORT)
                            .show();
                    return;
                }

                Event event = new Event();
                if (eventToEdit !=null) {
                    // Edit Event - keep the same id
                    event.setId(eventToEdit.getId());
                }
                event.setName(name);
                event.setDescription(description);
                event.setInterval(Integer.parseInt(interval));
                event.setActive(active);

                if (eventToEdit != null) {
                    // Edit Event
                    if (DatabaseHelper.getInstance(context).updateEvent(event)) {
                        mCallback.onEditEventDone();
                    } else {
                        Toast.makeText(context, "Editare reteta esuata!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Add Event
                    if (DatabaseHelper.getInstance(mContext).addEvent(event)) {
                        // Event was added - refresh adapter
                        mCallback.onEditEventDone();
                    } else {
                        Toast.makeText(mContext, "Adaugare eveniment esuata!", Toast.LENGTH_SHORT).show();
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

                // Delete event  from database
                if (DatabaseHelper.getInstance(mContext).deleteEvent(eventToEdit)) {
                    mCallback.onEditEventDone();
                } else {
                    Toast.makeText(mContext, "Stergere eveniment esuata!", Toast.LENGTH_SHORT).show();
                }

                dismiss();
            }
        });

        // Edit Mode - populate fields
        if (eventToEdit != null) {
            mNameText.setText(eventToEdit.getName());
            mDescriptionText.setText(eventToEdit.getDescription());
            mIntervalText.setText(String.valueOf(eventToEdit.getInterval()));
            mActiveCheck.setChecked(eventToEdit.isActive());
        } else {
            mDeleteButton.setVisibility(View.INVISIBLE);
        }
    }

    public interface AddEventCallback {
        void onEditEventDone();
    }
}
