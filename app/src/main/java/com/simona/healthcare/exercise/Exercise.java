package com.simona.healthcare.exercise;

public class Exercise {
    private int mId;
    private String mName;
    private int mSets;
    private int mRepsPerSet;
    private int mBreak;
    private String mDescription;
    private String mImagePath;

    public Exercise() {
        this.mId = 0;
        this.mName = "";
        this.mDescription = "";
        this.mSets = 0;
        this.mRepsPerSet = 0;
        this.mBreak = 0;
        this.mImagePath = null;
    }

    public Exercise(int mId, String mName, String mDescription, int mSets, int mRepsPerSet,
                    int mBreak, String imagePath) {
        this.mId = mId;
        this.mName = mName;
        this.mDescription = mDescription;
        this.mSets = mSets;
        this.mRepsPerSet = mRepsPerSet;
        this.mBreak = mBreak;
        this.mImagePath = imagePath;
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

    public void setSets(int mSets) {
        this.mSets = mSets;
    }

    public int getRepsPerSet() {
        return mRepsPerSet;
    }

    public void setRepsPerSet(int mRepsPerSet) {
        this.mRepsPerSet = mRepsPerSet;
    }


    public int getBreak() {
        return mBreak;
    }

    public void setBreak(int mBreak) {
        this.mBreak = mBreak;
    }

    public String getImagePath() {
        return mImagePath;
    }

    public void setImagePath(String mImagePath) {
        this.mImagePath = mImagePath;
    }

    @Override
    public String toString() {
        return "Exercise{" +
                "mId=" + mId +
                ", mName='" + mName + '\'' +
                ", mSets=" + mSets +
                ", mRepsPerSet=" + mRepsPerSet +
                ", mBreak=" + mBreak +
                ", mDescription='" + mDescription + '\'' +
                ", mImagePath='" + mImagePath + '\'' +
                '}';
    }
}
