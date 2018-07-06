package com.simona.healthcare.event;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.simona.healthcare.R;
import com.simona.healthcare.utils.DatabaseHelper;

import java.util.List;

public class EventsFragment extends Fragment {

    private static final String TAG = "HEALTH_";

    private ListView mListView;
    private EventsAdapter mAdapter;
    private Context mContext;
    private EditEventDialog mDialog;
    private TextView mNoEventText;

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
        mNoEventText = view.findViewById(R.id.no_event_text);

        // Get all events from database
        List<Event> events = DatabaseHelper.getInstance(mContext).getEvents();

        if (events == null || events.isEmpty()) {
            mNoEventText.setVisibility(View.VISIBLE);
            mListView.setVisibility(View.GONE);
        } else {
            mNoEventText.setVisibility(View.GONE);
            mListView.setVisibility(View.VISIBLE);
        }

        // Load fetched events
        mAdapter = new EventsAdapter(mContext, events);
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

                List<Event> events = DatabaseHelper.getInstance(mContext).getEvents();

                mAdapter.setData(events);

                if (events == null || events.isEmpty()) {
                    mNoEventText.setVisibility(View.VISIBLE);
                    mListView.setVisibility(View.GONE);
                } else {
                    mNoEventText.setVisibility(View.GONE);
                    mListView.setVisibility(View.VISIBLE);
                }
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
                List<Event> events = DatabaseHelper.getInstance(mContext).getEvents();

                mAdapter.setData(events);

                if (events == null || events.isEmpty()) {
                    mNoEventText.setVisibility(View.VISIBLE);
                    mListView.setVisibility(View.GONE);
                } else {
                    mNoEventText.setVisibility(View.GONE);
                    mListView.setVisibility(View.VISIBLE);
                }
            }
        }, event);
        mDialog.show();
    }

}
