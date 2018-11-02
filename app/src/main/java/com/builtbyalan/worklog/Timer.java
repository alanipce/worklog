package com.builtbyalan.worklog;

import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;

// Maintains timer state and provides method that determines time elapsed
public class Timer {
    private long mStartTimestamp = 0;
    private long mStopTimestamp  = 0;
    private boolean isRunning = false;


    public Timer start() {
        mStartTimestamp = SystemClock.elapsedRealtime();
        isRunning = true;
        return this;
    }

    public Timer resume() {
        isRunning = true;

        return this;
    }

    public Timer stop() {
        mStopTimestamp = SystemClock.elapsedRealtime();
        isRunning = false;
        return this;
    }

    public long getTimeElapsed() {
        long relativeToTimestamp;

        if (isRunning) {
            relativeToTimestamp = SystemClock.elapsedRealtime();
        } else {
            relativeToTimestamp = mStopTimestamp;
        }

        return relativeToTimestamp - mStartTimestamp;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public boolean isIdle() {
        return !isRunning;
    }
}
