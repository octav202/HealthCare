package com.simona.healthcare.event;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.simona.healthcare.R;
import com.simona.healthcare.utils.DatabaseHelper;

import java.util.List;

public class EventsAdapter extends BaseAdapter {

    private final static String TAG = "HEALTH_EventsAdapter";

    private List<Event> mEvents;
    private Context mContext;

    public EventsAdapter(Context c, List<Event> events) {
        mContext = c;
        mEvents = events;
    }

    public int getCount() {
        return mEvents.size();
    }

    public Object getItem(int position) {
        return mEvents.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = convertView;
        FileHolder holder = new FileHolder();
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.event_item, null);
            holder.nameTextView = v.findViewById(R.id.eventNameText);
            holder.descriptionTextView = v.findViewById(R.id.eventDescriptionText);
            holder.intervalTextView = v.findViewById(R.id.eventIntervalText);
            holder.activeCheckBox = v.findViewById(R.id.active);
            v.setTag(holder);
        } else {
            holder = (FileHolder) v.getTag();
        }

        final Event event = mEvents.get(position);
        holder.nameTextView.setText(event.getName());
        holder.descriptionTextView.setText(event.getDescription());
        holder.intervalTextView.setText("Frequency - " + String.valueOf(event.getInterval()) + " min.");
        holder.activeCheckBox.setChecked(event.isActive() ? true : false);

        holder.activeCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                event.setActive(isChecked);
                DatabaseHelper.getInstance(mContext).updateEvent(event);
            }
        });

        return v;
    }

    public void setData(List<Event> events) {
        mEvents.clear();
        mEvents.addAll(events);
        notifyDataSetChanged();
    }

    public class FileHolder {
        TextView nameTextView;
        TextView descriptionTextView;
        TextView intervalTextView;
        CheckBox activeCheckBox;
    }

}
