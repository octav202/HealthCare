package com.simona.healthcare.category;

public class Category {

    private int mId;
    private String mName;
    private String mImagePath;

    public Category() {
        this.mId = 0;
        this.mName = "";
        this.mImagePath = "";
    }

    public Category(int mId, String mName, String mImagePath) {
        this.mId = mId;
        this.mName = mName;
        this.mImagePath = mImagePath;
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

    public String getImagePath() {
        return mImagePath;
    }

    public void setImagePath(String mImagePath) {
        this.mImagePath = mImagePath;
    }

    @Override
    public String toString() {
        return "Category{" +
                "mId=" + mId +
                ", mName='" + mName + '\'' +
                ", mImagePath='" + mImagePath + '\'' +
                '}';
    }
}
