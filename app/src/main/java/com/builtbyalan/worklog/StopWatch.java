package com.builtbyalan.worklog;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import java.lang.ref.WeakReference;

// displays time elapsed from internal timer state to set display
public class StopWatch {
    private TaskTimer mTimer;
    private WeakReference<TextView> mDisplayTextViewReference;
    private long mRefreshInterval; // in milliseconds
    private Handler mHandler;
    private DateFormatter mDateFormatter;

    private static final int MSG = 1;

    StopWatch() {
        this(new TaskTimer(), null);
    }

    StopWatch(TaskTimer timer) {
        this(timer, null);
    }

    StopWatch(TaskTimer timer, TextView displayTextView) {
        mTimer = timer;
        mDisplayTextViewReference = new WeakReference<>(displayTextView);
        mHandler = new RefreshHandler(this);
        mDateFormatter = new DateFormatter();
        mRefreshInterval = 1000;

        mHandler.sendMessage(mHandler.obtainMessage(MSG));
    }

    public void setDisplay(TextView displayTextView) {
        mDisplayTextViewReference = new WeakReference<>(displayTextView);
    }

    private void updateDisplay() {
        TextView displayTextView = mDisplayTextViewReference.get();
        if (displayTextView == null) {
            return;
        }

        String formattedElapsedTime = mDateFormatter.formatElapsedTime(mTimer.getTimeElapsed(), true);
        displayTextView.setText(formattedElapsedTime);
    }

    public StopWatch setTimerName(String name) {
        getTimer().setTaskName(name);

        return this;
    }
    public String getTimerName() {
        return getTimer().getTaskName();
    }

    public long getTimeElapsed() {
        return getTimer().getTimeElapsed();
    }

    public StopWatch startTimer() {
        getTimer().start();
        return this;
    }

    public StopWatch stopTimer() {
        getTimer().stop();

        return this;
    }

    public StopWatch resetTimer() {
        getTimer().reset();

        return this;
    }

    public TaskTimer getTimer() {
        return mTimer;
    }

    public boolean isTimerIdle() {
        return getTimer().isIdle();
    }

    public boolean isTimerRunning() {
        return getTimer().isRunning();
    }

    private static class RefreshHandler extends Handler {
        private WeakReference<StopWatch> mStopWatchReference;

        RefreshHandler(StopWatch stopWatch) {
            mStopWatchReference = new WeakReference<>(stopWatch);
        }

        @Override
        public void handleMessage(Message msg) {
            StopWatch stopWatch = mStopWatchReference.get();

            if (stopWatch == null) {
                return;
            }

            stopWatch.updateDisplay();

            sendMessageDelayed(obtainMessage(MSG), stopWatch.mRefreshInterval);
        }
    }
}
