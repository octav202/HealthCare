package com.simona.healthcare.category;

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

public class CategoryAdapter extends BaseAdapter {

    private final static String TAG = "HEALTH_CategoryAdapter";

    private List<Category> mCategories;
    private Context mContext;

    public CategoryAdapter(Context c, List<Category> categories) {
        mContext = c;
        mCategories = categories;
    }

    public int getCount() {
        return mCategories.size();
    }

    public Object getItem(int position) {
        return mCategories.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = convertView;
        FileHolder holder = new FileHolder();
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.category_item, null);
            holder.nameTextView = v.findViewById(R.id.nameTextView);
            holder.imageView = v.findViewById(R.id.imageView);
            v.setTag(holder);
        } else {
            holder = (FileHolder) v.getTag();
        }

        Category category = mCategories.get(position);
        holder.nameTextView.setText(category.getName());
        //holder.imageView.setImageDrawable(category.getImagePath());

        return v;
    }

    public void onItemSelected(int position) {
        Log.d(TAG, "onItemSelected() " + position);

        Category selectedCategory = mCategories.get(position);

        Toast.makeText(mContext, "Selected " + selectedCategory, Toast.LENGTH_SHORT).show();
    }

    public class FileHolder {
        TextView nameTextView;
        ImageView imageView;
    }

}
