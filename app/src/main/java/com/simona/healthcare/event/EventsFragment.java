package com.simona.healthcare.event;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.simona.healthcare.R;
import com.simona.healthcare.utils.DatabaseHelper;

import java.util.List;

public class EventsFragment extends Fragment {

    private static final String TAG = "HEALTH_";

    private ListView mListView;
    private EventsAdapter mAdapter;
    private Context mContext;

    public static EventsFragment newInstance() {
        EventsFragment fragment = new EventsFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView()");

        mContext = getActivity().getApplicationContext();
        View view = inflater.inflate(R.layout.events_fragment, container, false);
        mListView = view.findViewById(R.id.eventsList);

        // Get all events from database
        List<Event> mExercises = DatabaseHelper.getInstance(mContext).getEvents();

        // Load fetched events
        mAdapter = new EventsAdapter(mContext, mExercises);
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mAdapter != null) {
                    Event event = (Event) mAdapter.getItem(position);
                    edit(event);
                }
            }
        });
        return view;
    }

    /**
     * Add Event
     */
    public void add() {
        final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        LinearLayout layout= new LinearLayout(getActivity().getApplicationContext());
        layout.setOrientation(LinearLayout.VERTICAL);

        // Name
        final EditText nameInput = new EditText(getActivity());
        nameInput.setHint(R.string.name);

        // Description
        final EditText descriptionInput = new EditText(getActivity());
        descriptionInput.setHint(R.string.description);

        // Interval
        final EditText intervalInput = new EditText(getActivity());
        intervalInput.setHint(R.string.interval);
        intervalInput.setInputType(InputType.TYPE_CLASS_NUMBER);

        // Active
        final CheckBox activeCheckbox = new CheckBox(getActivity());
        activeCheckbox.setText(R.string.active);

        layout.addView(nameInput);
        layout.addView(descriptionInput);
        layout.addView(intervalInput);
        layout.addView(activeCheckbox);
        alert.setView(layout);

        alert.setTitle(R.string.add_event);

        // Ok Button
        alert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                // Collect input
                String name = nameInput.getText().toString().trim();
                String description = descriptionInput.getText().toString().trim();
                Integer interval = Integer.parseInt(intervalInput.getText().toString().trim());
                Boolean active = activeCheckbox.isChecked();

                // Create Event object using the collected input
                Event event = new Event();
                event.setName(name);
                event.setDescription(description);
                event.setInterval(interval);
                event.setActive(active);

                // Add event to database
                if (DatabaseHelper.getInstance(mContext).addEvent(event)) {
                    // Event was added - refresh adapter
                    mAdapter.setData(DatabaseHelper.getInstance(mContext).getEvents());
                } else {
                    Toast.makeText(mContext, "Add Event Failed!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Cancel Button
        alert.setNegativeButton(R.string.cancel,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                });
        alert.create().show();
    }

    /**
     * Update Event.
     * @param event
     */
    private void edit(final Event event) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        LinearLayout layout= new LinearLayout(getActivity().getApplicationContext());
        layout.setOrientation(LinearLayout.VERTICAL);

        // Name
        final EditText nameInput = new EditText(getActivity());
        nameInput.setText(event.getName());

        // Description
        final EditText descriptionInput = new EditText(getActivity());
        descriptionInput.setText(event.getDescription());

        // Interval
        final EditText intervalInput = new EditText(getActivity());
        intervalInput.setText(String.valueOf(event.getInterval()));
        intervalInput.setInputType(InputType.TYPE_CLASS_NUMBER);

        // Active
        final CheckBox activeCheckbox = new CheckBox(getActivity());
        activeCheckbox.setText(R.string.active);
        activeCheckbox.setChecked(event.isActive() ? true : false);

        layout.addView(nameInput);
        layout.addView(descriptionInput);
        layout.addView(intervalInput);
        layout.addView(activeCheckbox);
        alert.setView(layout);

        alert.setTitle(R.string.add_event);


        // Ok Button
        alert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                // Collect input
                String name = nameInput.getText().toString().trim();
                String description = descriptionInput.getText().toString().trim();
                Integer interval = Integer.parseInt(intervalInput.getText().toString().trim());
                Boolean active = activeCheckbox.isChecked();

                // Create Event object using the collected input
                Event e = new Event();
                e.setId(e.getId());
                e.setName(name);
                e.setDescription(description);
                e.setInterval(interval);
                e.setActive(active);

                // Add exercise to database
                if (DatabaseHelper.getInstance(mContext).updateEvent(e)) {
                    // Exercise was added - refresh adapter
                    mAdapter.setData(DatabaseHelper.getInstance(mContext).getEvents());
                } else {
                    Toast.makeText(mContext, "Update Event Failed!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Delete Button
        alert.setNegativeButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Delete event  from database
                if (DatabaseHelper.getInstance(mContext).deleteEvent(event)) {
                    // Exercise was deleted - refresh adapter
                    mAdapter.setData(DatabaseHelper.getInstance(mContext).getEvents());
                } else {
                    Toast.makeText(mContext, "Delete event Failed!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Cancel Button
        alert.setNeutralButton(R.string.cancel,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                });

        alert.create().show();
    }

}
