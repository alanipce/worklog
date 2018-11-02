package com.builtbyalan.worklog;

import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import java.lang.ref.WeakReference;

// binds the timer to textview and updates it at a set refresh rate
// requires explicit instruction to continue refreshing
public class RefreshingTimerDisplay {
    private Timer mTimer;
    private TextView mDisplayTextView;
    private long mRefreshInterval; // in milliseconds
    private Handler mHandler;
    private DateFormatter mDateFormatter;

    private static final int MSG = 1;

    RefreshingTimerDisplay() {
        this(null, null);
    }

    RefreshingTimerDisplay(Timer timer, TextView displayTextView) {
        mTimer = timer;
        mDisplayTextView = displayTextView;
        mHandler = new RefreshHandler(this);
        mDateFormatter = new DateFormatter();
        mRefreshInterval = 1000;
    }

    public void beginUpdating(Timer timer, TextView displayTextView) {
        mTimer = timer;
        mDisplayTextView = displayTextView;

        beginUpdating();
    }

    public void beginUpdating() {
        if (mTimer == null || mDisplayTextView == null) {
            return;
        }

        mHandler.sendMessage(mHandler.obtainMessage(MSG));
    }


    private void updateDisplay() {
        String formattedElapsedTime = mDateFormatter.formatElapsedTime(mTimer.getTimeElapsed(), true);

        mDisplayTextView.setText(formattedElapsedTime);
    }

    private static class RefreshHandler extends Handler {
        private WeakReference<RefreshingTimerDisplay> mTimerDisplayReference;

        RefreshHandler(RefreshingTimerDisplay timerDisplay) {
            mTimerDisplayReference = new WeakReference<>(timerDisplay);
        }

        @Override
        public void handleMessage(Message msg) {
            RefreshingTimerDisplay timerDisplay = mTimerDisplayReference.get();

            if (timerDisplay == null) {
                return;
            }

            timerDisplay.updateDisplay();

            sendMessageDelayed(obtainMessage(MSG), timerDisplay.mRefreshInterval);
        }
    }
}
