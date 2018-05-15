package com.simona.healthcare.exercise;

public class Exercise {
    private int mId;
    private String mName;
    private String mDescription;

    // Number of sets
    private int mSets;

    //Number of repetitions per set - ex. 3 sets of 20 reps
    private int mRepsPerSet;

    // Set duration - ex. 1 set - 30 seconds
    private int mSetDuration;

    // Break between sets - ex. 30sec between sets
    private int mBreak;

    public Exercise() {
        this.mId = 0;
        this.mName = "";
        this.mDescription = "";
        this.mSets = 0;
        this.mRepsPerSet = 0;
        this.mSetDuration = 0;
        this.mBreak = 0;
    }

    public Exercise(int mId, String mName, String mDescription, int mSets, int mRepsPerSet, int mSetDuration, int mBreak) {
        this.mId = mId;
        this.mName = mName;
        this.mDescription = mDescription;
        this.mSets = mSets;
        this.mRepsPerSet = mRepsPerSet;
        this.mSetDuration = mSetDuration;
        this.mBreak = mBreak;
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

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public int getSets() {
        return mSets;
    }

    public void setmSets(int mSets) {
        this.mSets = mSets;
    }

    public int getRepsPerSet() {
        return mRepsPerSet;
    }

    public void setRepsPerSet(int mRepsPerSet) {
        this.mRepsPerSet = mRepsPerSet;
    }

    public int getSetDuration() {
        return mSetDuration;
    }

    public void setSetDuration(int mSetDuration) {
        this.mSetDuration = mSetDuration;
    }

    public int getBreak() {
        return mBreak;
    }

    public void setBreak(int mBreak) {
        this.mBreak = mBreak;
    }

    @Override
    public String toString() {
        return "Exercise{" +
                "mId=" + mId +
                ", mName='" + mName + '\'' +
                ", mDescription='" + mDescription + '\'' +
                ", mSets=" + mSets +
                ", mRepsPerSet=" + mRepsPerSet +
                ", mSetDuration=" + mSetDuration +
                ", mBreak=" + mBreak +
                '}';
    }
}
