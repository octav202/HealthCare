package com.simona.healthcare.program;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.simona.healthcare.R;
import com.simona.healthcare.exercise.Exercise;
import com.simona.healthcare.playing.PlayBarFragment;
import com.simona.healthcare.utils.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class ProgramsAdapter extends BaseAdapter {

    private final static String TAG = "HEALTH_CategoryAdapter";

    private List<Program> mPrograms;
    private Context mContext;
    private List<Program> mFilteredPrograms;

    public ProgramsAdapter(Context c, List<Program> programs) {
        mContext = c;
        mPrograms = programs;
        mFilteredPrograms = new ArrayList<>();
        mFilteredPrograms.addAll(programs);
    }

    public int getCount() {
        return mFilteredPrograms.size();
    }

    public Object getItem(int position) {
        return mFilteredPrograms.get(position);
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

        final Program program = mFilteredPrograms.get(position);
        holder.nameTextView.setText(program.getName());

        List<Exercise> exercises = DatabaseHelper.getInstance(mContext).getExercisesForProgramId(program.getId());
        StringBuilder builder = new StringBuilder();
        // Get Exercises for Program
        for (int i = 0; i < exercises.size(); i++) {
            Exercise ex = exercises.get(i);
            builder.append(ex.getName());

            if (i < exercises.size() - 1) {
                builder.append(", ");
            }
        }

        holder.exercisesTextView.setText(builder.toString());

        // Start Program
        holder.playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlayBarFragment.getInstance().setProgram(program);
            }
        });

        return v;
    }

    public void setData(List<Program> programs) {
        mPrograms.clear();
        mPrograms.addAll(programs);
        mFilteredPrograms.clear();
        mFilteredPrograms.addAll(programs);
        notifyDataSetChanged();
    }

    public void filterByDay(Integer day) {
        Log.d(TAG, "filterByDay() : " + day);
        mFilteredPrograms.clear();

        if (day <8) {
            mFilteredPrograms.addAll(DatabaseHelper.getInstance(mContext).getProgramsForDay(day));
        } else {
            mFilteredPrograms.addAll(mPrograms);
        }
        notifyDataSetChanged();
    }

    public class FileHolder {
        TextView nameTextView;
        TextView exercisesTextView;
        Button playButton;
    }

}
