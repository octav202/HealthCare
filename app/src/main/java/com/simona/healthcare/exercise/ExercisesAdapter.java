package com.simona.healthcare.exercise;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.simona.healthcare.R;
import com.simona.healthcare.utils.DatabaseHelper;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class ExercisesAdapter extends BaseAdapter {

    private final static String TAG = "HEALTH_CategoryAdapter";

    private List<Exercise> mExercises;
    private Context mContext;
    private ImageCallback mCallback;

    public ExercisesAdapter(Context c, List<Exercise> exercises) {
        mContext = c;
        mExercises = exercises;
    }

    public void setCallback(ImageCallback callback) {
        mCallback = callback;
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
        holder.setsTextView.setText(exercise.getSets() + " sets,  " + exercise.getRepsPerSet() + " reps.");
        holder.durationTextView.setText("Break - " + exercise.getBreak() + " sec.");
        holder.descriptionTextView.setText(exercise.getDescription());

        // Exercise image
        String imageUri = DatabaseHelper.getInstance(mContext).getImageForExercise(exercise.getId());
        if (imageUri != null) {
            Uri uri = Uri.parse(imageUri);
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(mContext.getContentResolver().openInputStream(uri));
                holder.imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        holder.imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (mCallback != null) {
                        mCallback.onImageRequested(exercise.getId());
                    }
                }
                return true;
            }
        });

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

    public interface ImageCallback {
        void onImageRequested(int id);
    }

}
