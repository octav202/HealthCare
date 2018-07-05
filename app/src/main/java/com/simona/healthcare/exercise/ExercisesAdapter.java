package com.simona.healthcare.exercise;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.simona.healthcare.R;

import java.io.IOException;
import java.util.List;

public class ExercisesAdapter extends BaseAdapter {

    private final static String TAG = "HEALTH_ExercisesAdapter";

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
        return mExercises.get(position);
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
        final Exercise exercise = mExercises.get(position);
        holder.nameTextView.setText(exercise.getName());
        holder.setsTextView.setText(exercise.getSets() + " seturi,  " + exercise.getRepsPerSet() + " repetari");
        holder.durationTextView.setText("Pauza - " + exercise.getBreak() + " sec.");
        holder.descriptionTextView.setText(exercise.getDescription());

        // Exercise image
        if (exercise.getImagePath() != null) {
            Uri uri = Uri.parse(exercise.getImagePath());
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(mContext.getContentResolver().openInputStream(uri));
                holder.imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return v;
    }

    public void setData(List<Exercise> exercises) {
        mExercises.clear();
        mExercises.addAll(exercises);
        notifyDataSetChanged();
    }

    public class FileHolder {
        TextView nameTextView;
        TextView setsTextView;
        TextView durationTextView;
        TextView descriptionTextView;
        ImageView imageView;
    }
}
