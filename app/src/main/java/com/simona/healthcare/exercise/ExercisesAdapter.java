package com.simona.healthcare.exercise;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.simona.healthcare.R;

import java.util.List;

public class ExercisesAdapter extends BaseAdapter {

    private final static String TAG = "HEALTH_CategoryAdapter";

    private List<Exercise> mExercises;
    private Context mContext;

    public ExercisesAdapter(Context c, List<Exercise> exercises) {
        mContext = c;
        mExercises = exercises;
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
            v = inflater.inflate(R.layout.exercise_item, null);
            holder.nameTextView = v.findViewById(R.id.nameTextView);
            holder.setsTextView = v.findViewById(R.id.setsText);
            holder.durationTextView = v.findViewById(R.id.durationText);
            holder.descriptionTextView = v.findViewById(R.id.descriptionTextView);
            holder.imageView = v.findViewById(R.id.imageView);
            v.setTag(holder);
        } else {
            holder = (FileHolder) v.getTag();
        }
        Exercise exercise = mExercises.get(position);
        holder.nameTextView.setText(exercise.getName());
        holder.setsTextView.setText(exercise.getSets() + " sets,  " + exercise.getRepsPerSet() + " reps.");
        holder.durationTextView.setText(exercise.getSetDuration() + " sec. per set" +
                ", " + exercise.getBreak() + " sec. break");
        holder.descriptionTextView.setText(exercise.getDescription());
        //holder.imageView.setImageDrawable(exercise.getImagePath());


        return v;
    }

    public void onItemSelected(int position) {
        Log.d(TAG, "onItemSelected() " + position);

        Exercise selected = mExercises.get(position);

        Toast.makeText(mContext, "Selected " + selected, Toast.LENGTH_SHORT).show();
    }

    public class FileHolder {
        TextView nameTextView;
        TextView setsTextView;
        TextView durationTextView;
        TextView descriptionTextView;
        ImageView imageView;
    }

}
