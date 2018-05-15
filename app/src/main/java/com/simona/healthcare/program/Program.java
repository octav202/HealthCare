package com.simona.healthcare.program;

public class Program {

    private int mId;
    private String mName;

    public Program(int mId, String mName) {
        this.mId = mId;
        this.mName = mName;
    }

    public Program() {
        this.mId = 0;
        this.mName = "";
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
}
