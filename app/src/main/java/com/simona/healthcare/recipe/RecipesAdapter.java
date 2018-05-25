package com.simona.healthcare.recipe;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.simona.healthcare.R;
import com.simona.healthcare.exercise.Exercise;
import com.simona.healthcare.utils.DatabaseHelper;

import java.io.IOException;
import java.util.List;

public class RecipesAdapter extends BaseAdapter {

    private final static String TAG = "HEALTH_CategoryAdapter";

    private List<Recipe> mRecipes;
    private Context mContext;
    private ImageCallback mCallback;

    public RecipesAdapter(Context c, List<Recipe> recipes) {
        mContext = c;
        mRecipes = recipes;
    }

    public void setCallback(ImageCallback callback) {
        mCallback = callback;
    }

    public int getCount() {
        return mRecipes.size();
    }

    public Object getItem(int position) {
        return mRecipes.get(position);
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
            holder.timeTextView = v.findViewById(R.id.timeTextView);
            holder.descriptionTextView = v.findViewById(R.id.descriptionTextView);
            holder.imageView = v.findViewById(R.id.imageView);
            v.setTag(holder);
        } else {
            holder = (FileHolder) v.getTag();
        }


        final Recipe recipe = mRecipes.get(position);

        holder.nameTextView.setText(recipe.getName());
        holder.timeTextView.setText(recipe.getTime());
        holder.descriptionTextView.setText(recipe.getDescription());

        // Recipe image
        if (recipe.getImagePath() != null) {
            Uri uri = Uri.parse(recipe.getImagePath());
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

    public void setData(List<Recipe> recipes) {
        mRecipes.clear();
        mRecipes.addAll(recipes);
        notifyDataSetChanged();
    }

    public class FileHolder {
        TextView nameTextView;
        TextView timeTextView;
        TextView descriptionTextView;
        ImageView imageView;
    }

    public interface ImageCallback {
        void onImageRequested(int id);
    }

}
