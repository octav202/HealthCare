package com.simona.healthcare.program;

import com.simona.healthcare.exercise.Exercise;

import java.util.ArrayList;
import java.util.List;

public class Program {

    private int mId;
    private String mName;
    private List<Exercise> mExercises;
    private List<Integer> mDays;

    public Program(int mId, String mName, List<Exercise> exercises, List<Integer> days) {
        this.mId = mId;
        this.mName = mName;
        this.mExercises = exercises;
        this.mDays = days;
    }

    public Program() {
        this.mId = 0;
        this.mName = "";
        mExercises = new ArrayList<>();
        mDays = new ArrayList<>();
    }

    public int getId() {
        return mId;
    }

    public void setId(int mId) {
        this.mId = mId;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    @Override
    public String toString() {
        return "Program{" +
                "mId=" + mId +
                ", mName='" + mName + '\'' +
                '}';
    }

    public List<Exercise> getExercises() {
        return mExercises;
    }

    public void setExercises(List<Exercise> mExercises) {
        this.mExercises = mExercises;
    }

    public List<Integer> getDays() {
        return mDays;
    }

    public void setDays(List<Integer> mDays) {
        this.mDays = mDays;
    }
}
