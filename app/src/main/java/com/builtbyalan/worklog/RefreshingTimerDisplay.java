package com.builtbyalan.worklog;

import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.widget.TextView;

import java.lang.ref.WeakReference;

// binds the timer to textview and updates it at a set refresh rate
// requires explicit instruction to continue refreshing
public abstract class RefreshingTimerDisplay {
    private Timer mTimer;
    private TextView mDisplayTextView;
    private long mRefreshInterval; // in milliseconds
    private Handler mHandler;
    private DateManager mDateFormatter;

    private static final int MSG = 1;

    RefreshingTimerDisplay(Timer timer, TextView displayTextView) {
        mTimer = timer;
        mDisplayTextView = displayTextView;
        mHandler = new RefreshHandler(this);
        mDateFormatter = new DateManager();
        mRefreshInterval = 60000; // once per minute
    }

    public void beginUpdating() {
        mHandler.sendMessage(mHandler.obtainMessage(MSG));
    }

    public abstract boolean onUpdate(long timeElapsed);

    private boolean updateDisplay() {
        String formattedElapsedTime = mDateFormatter.formatElapsedTime(mTimer.getTimeElapsed());

        mDisplayTextView.setText(formattedElapsedTime);

        return onUpdate(mTimer.getTimeElapsed());
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

            long lastUpdateStart = SystemClock.elapsedRealtime();
            boolean continueUpdates = timerDisplay.updateDisplay();
            long lastUpdateDuration = SystemClock.elapsedRealtime() - lastUpdateStart;


            if (continueUpdates) {
                long refreshInterval = timerDisplay.mRefreshInterval;
                long delay = refreshInterval - lastUpdateDuration;

                // special case where onTick takes one or more intervals to complete, will result in skipping
                // missed intervals
                while (delay < 0) delay += refreshInterval;

                sendMessageDelayed(obtainMessage(MSG), delay);
            }

        }
    }
}
