package com.simona.healthcare.playing;

import android.content.Context;

import com.simona.healthcare.R;
import com.simona.healthcare.exercise.Exercise;
import com.simona.healthcare.program.Program;

import java.util.ArrayList;

public class Operation {

    // Operation type - tts, rep, set etc.
    int type;
    // Operation duration - in seconds
    int duration;
    // Operation details - exercise name, rep number etc
    String info;
    // Exercise
    Exercise exercise;

    // Operation Types
    public static final int TYPE_TTS_PROGRAM = 100;
    public static final int TYPE_TTS_EXERCISE = 101;
    public static final int TYPE_TTS_EXERCISE_SETS_AND_REPS = 102;
    public static final int TYPE_TTS_SET = 103;
    public static final int TYPE_TTS_STOP = 104;
    public static final int TYPE_TTS_START = 105;
    public static final int TYPE_TTS_PROGRAM_OVER = 106;
    public static final int TYPE_TTS_REP = 107;
    public static final int TYPE_BREAK_UNIT = 108;

    // Operation Durations
    public static final int ONE_SEC = 1000;
    public static final int TWO_SEC = 2000;
    public static final int THREE_SEC = 3000;
    public static final int FOUR_SEC = 4000;

    public Operation(int type, int duration, String info, Exercise e) {
        this.type = type;
        this.duration = duration;
        this.info = info;
        this.exercise = e;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public Exercise getExercise() {
        return exercise;
    }

    public void setExercise(Exercise exercise) {
        this.exercise = exercise;
    }

    @Override
    public String toString() {
        return "Operation{" + "type=" + type + ", duration=" + duration + ", info='" + info + '\'' + '}';
    }

    /**
     * Construct list of operations for a program.
     * Similar to a playlist.
     * @param program
     * @return
     */
    public static ArrayList<Operation> programToOperations(Context context, Program program) {
        ArrayList<Operation> list = new ArrayList<>();

        // Program Name
        list.add(new Operation(TYPE_TTS_PROGRAM, THREE_SEC, program.getName(), null));

        // Exercises
        for (Exercise e : program.getExercises()) {

            // Exercise Name
            list.add(new Operation(TYPE_TTS_EXERCISE, TWO_SEC, e.getName(), e));

            // Exercise Sets and Reps
            String exerciseInfo = String.format(context.getResources().getString(R.string.exercise_tts),
                    e.getSets(), e.getRepsPerSet());
            list.add(new Operation(TYPE_TTS_EXERCISE_SETS_AND_REPS, THREE_SEC, exerciseInfo, e));

            // Sets
            for (int set = 1; set <= e.getSets(); set++) {

                // Set Number
                list.add(new Operation(TYPE_TTS_SET, TWO_SEC, String.valueOf(set), e));

                // Start
                list.add(new Operation(TYPE_TTS_START, TWO_SEC, context.getResources().getString(R.string.start_tts), e));

                // Reps
                for (int rep = 1; rep <= e.getRepsPerSet(); rep++) {
                    // Rep Number
                    list.add(new Operation(TYPE_TTS_REP, ONE_SEC, String.valueOf(rep), e));
                }

                // Break
                for (int br = 1; br >= e.getBreak(); br++) {
                    list.add(new Operation(TYPE_BREAK_UNIT, ONE_SEC, String.valueOf(br), e));
                }

                // Stop
                list.add(new Operation(TYPE_TTS_STOP, TWO_SEC, context.getResources().getString(R.string.stop_tts), e));
            }
        }

        // Program Over.
        list.add(new Operation(TYPE_TTS_PROGRAM_OVER, ONE_SEC, context.getResources().getString(R.string.end_program), null));

        return list;
    }

}
