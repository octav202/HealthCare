package com.simona.healthcare.event;

public class Event {

    private int mId;
    private String mName;
    private String mDescription;
    private int mInterval;
    private boolean mActive;

    public Event() {
        this.mId = 0;
        this.mName = "";
        this.mDescription = "";
        this.mInterval = 0;
        this.mActive = false;
    }

    public Event(int mId, String mName, String mDescription, int mInterval, boolean mActive) {
        this.mId = mId;
        this.mName = mName;
        this.mDescription = mDescription;
        this.mInterval = mInterval;
        this.mActive = mActive;
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

    public int getInterval() {
        return mInterval;
    }

    public void setInterval(int mInterval) {
        this.mInterval = mInterval;
    }

    public boolean isActive() {
        return mActive;
    }

    public void setActive(boolean enabled) {
        this.mActive = enabled;
    }

    @Override
    public String toString() {
        return "Event{" +
                "mId=" + mId +
                ", mName='" + mName + '\'' +
                ", mDescription='" + mDescription + '\'' +
                ", mInterval=" + mInterval +
                ", mActive=" + mActive +
                '}';
    }
}
