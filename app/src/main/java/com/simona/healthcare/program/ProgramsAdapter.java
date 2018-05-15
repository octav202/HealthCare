package com.simona.healthcare.program;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.simona.healthcare.R;

import java.util.List;

public class ProgramsAdapter extends BaseAdapter {

    private final static String TAG = "HEALTH_CategoryAdapter";

    private List<Program> mPrograms;
    private Context mContext;

    public ProgramsAdapter(Context c, List<Program> programs) {
        mContext = c;
        mPrograms = programs;
    }

    public int getCount() {
        return mPrograms.size();
    }

    public Object getItem(int position) {
        return mPrograms.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = convertView;
        FileHolder holder = new FileHolder();
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.program_item, null);
            holder.nameTextView = v.findViewById(R.id.nameTextView);
            holder.exercisesTextView = v.findViewById(R.id.exercisesPreviewText);
            holder.playButton = v.findViewById(R.id.playButton);
            v.setTag(holder);
        } else {
            holder = (FileHolder) v.getTag();
        }

        final Program program = mPrograms.get(position);
        holder.nameTextView.setText(program.getName());
        holder.exercisesTextView.setText("Exercises : ex1, ex2, ex3. ex4..");
        holder.playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "Play " + program.getName(), Toast.LENGTH_SHORT);
            }
        });

        return v;
    }

    public class FileHolder {
        TextView nameTextView;
        TextView exercisesTextView;
        Button playButton;
    }

}
