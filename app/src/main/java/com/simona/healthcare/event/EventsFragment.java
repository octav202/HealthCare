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
    private EditEventDialog mDialog;

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

        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }

        mDialog = new EditEventDialog(getActivity(), new EditEventDialog.AddEventCallback() {
            @Override
            public void onEditEventDone() {
                mAdapter.setData(DatabaseHelper.getInstance(mContext).getEvents());
            }
        }, null);
        mDialog.show();

    }

    /**
     * Update Event.
     * @param event
     */
    private void edit(final Event event) {

        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }

        mDialog = new EditEventDialog(getActivity(), new EditEventDialog.AddEventCallback() {
            @Override
            public void onEditEventDone() {
                mAdapter.setData(DatabaseHelper.getInstance(mContext).getEvents());
            }
        }, event);
        mDialog.show();
    }

}
