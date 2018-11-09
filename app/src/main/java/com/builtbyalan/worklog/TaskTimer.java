package com.builtbyalan.worklog;

import android.os.SystemClock;
import android.util.Base64;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

// Maintains timer state and provides method that determines time elapsed
public class TaskTimer {
    private String mTaskName;
    private long mStartTimestamp;
    private long mStopTimestamp;
    private boolean mIsRunning;

    TaskTimer() {
        long now = SystemClock.elapsedRealtime();

        mTaskName = "";
        mStartTimestamp = now;
        mStopTimestamp  = now;
        mIsRunning = false;
    }

    TaskTimer(String encodedCookie) {
        String decodedJSONString = new String(Base64.decode(encodedCookie, Base64.DEFAULT));

        try {
            JSONObject params = new JSONObject(decodedJSONString);
            mTaskName = params.getString("taskName");
            mStartTimestamp = Long.parseLong(params.getString("startTimestamp"));
            mStopTimestamp = Long.parseLong(params.getString("stopTimestamp"));
            mIsRunning = false;
        } catch (Exception exception) {
            throw new RuntimeException("Invalid encoded cookie used to initialize Task timer");
        }
    }

    public TaskTimer start() {
        mStartTimestamp = SystemClock.elapsedRealtime();
        mIsRunning = true;
        return this;
    }

    public TaskTimer resume() {
        mIsRunning = true;

        return this;
    }

    public TaskTimer stop() {
        mStopTimestamp = SystemClock.elapsedRealtime();
        mIsRunning = false;
        return this;
    }

    public TaskTimer reset() {
        mStopTimestamp = 0;
        mStartTimestamp = 0;
        mTaskName = "";
        mIsRunning = false;

        return this;
    }

    public String getTaskName() {
        return mTaskName;
    }

    public void setTaskName(String name) {
        this.mTaskName = name;
    }

    // give blob of data back that can be used in constructor
    public String encodedCookie() {
        try {
            JSONObject json = new JSONObject();
            json.put("taskName", mTaskName);
            json.put("startTimestamp", String.valueOf(mStartTimestamp));
            json.put("stopTimestamp", String.valueOf(mStopTimestamp));

            return Base64.encodeToString(json.toString().getBytes(), Base64.DEFAULT);

        } catch (JSONException exception) {
            return null;
        }
    }

    public long getTimeElapsed() {
        long relativeToTimestamp;

        if (mIsRunning) {
            relativeToTimestamp = SystemClock.elapsedRealtime();
        } else {
            relativeToTimestamp = mStopTimestamp;
        }

        return relativeToTimestamp - mStartTimestamp;
    }

    public boolean isRunning() {
        return mIsRunning;
    }

    public boolean isIdle() {
        return !mIsRunning;
    }
}
