package com.simona.healthcare.program;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.simona.healthcare.R;
import com.simona.healthcare.exercise.Exercise;

import java.util.ArrayList;
import java.util.List;

public class ProgramExercisesAdapter extends BaseAdapter {

    private final static String TAG = "HEALTH_CategoryAdapter";

    private List<Exercise> mExercises;
    private Context mContext;
    private List<Exercise> mSelectedExercises;

    public ProgramExercisesAdapter(Context c, List<Exercise> exercises) {
        mContext = c;
        mExercises = exercises;
        mSelectedExercises = new ArrayList<Exercise>();
    }

    public int getCount() {
        return mExercises.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = convertView;
        FileHolder holder = new FileHolder();
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.exercise_item_mini, null);
            holder.nameTextView = v.findViewById(R.id.nameTextView);
            holder.setsTextView = v.findViewById(R.id.setsText);
            holder.durationTextView = v.findViewById(R.id.durationText);
            holder.descriptionTextView = v.findViewById(R.id.descriptionTextView);
            holder.imageView = v.findViewById(R.id.imageView);
            holder.checkBox = v.findViewById(R.id.selectedCheckbox);
            v.setTag(holder);
        } else {
            holder = (FileHolder) v.getTag();
        }

        final Exercise exercise = mExercises.get(position);
        holder.nameTextView.setText(exercise.getName());
        holder.setsTextView.setText(exercise.getSets() + " sets,  " + exercise.getRepsPerSet() + " reps.");
        holder.durationTextView.setText("Break : " + exercise.getBreak() + " sec.");
        holder.descriptionTextView.setText(exercise.getDescription());
        //holder.imageView.setImageDrawable(exercise.getImagePath());

        // For Edit Mode - Set selected exercises checked
        for (Exercise e : mSelectedExercises) {
            if (exercise.getId() == e.getId()) {
                holder.checkBox.setChecked(true);
                break;
            }
        }

        // Add exercise to Program
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mSelectedExercises.add(exercise);
                } else {
                    for (Exercise e : mSelectedExercises) {
                        if (e.getId() == exercise.getId()) {
                            mSelectedExercises.remove(e);
                        }
                    }
                }
            }
        });

        return v;
    }

    public List<Exercise> getSelectedItems() {
        return mSelectedExercises;
    }

    public void setData(List<Exercise> exercises) {
        mExercises.clear();
        mExercises.addAll(exercises);
        notifyDataSetChanged();
    }

    /**
     * Set list of selected exercises
     * @param list
     */
    public void setSelectedItems(List<Exercise> list) {
        mSelectedExercises.clear();
        mSelectedExercises.addAll(list);
        notifyDataSetChanged();
    }

    public class FileHolder {
        TextView nameTextView;
        TextView setsTextView;
        TextView durationTextView;
        TextView descriptionTextView;
        ImageView imageView;
        CheckBox checkBox;
    }

}
