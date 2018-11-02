package com.builtbyalan.worklog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

public class FakeTimerActivity extends AppCompatActivity {
    public static final String TAG = FakeTimerActivity.class.getSimpleName();

    private TextView mDisplayTextView;
    private Timer mTimer;
    private RefreshingTimerDisplay mRefreshingTimerDisplay;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fake_timer);
        mDisplayTextView = findViewById(R.id.text_fake_timer_display);

        mTimer = new Timer();
        mTimer.start();
        mRefreshingTimerDisplay = new FakeTimerRefreshDisplay(mTimer, mDisplayTextView);
        mRefreshingTimerDisplay.beginUpdating();
    }


    public static class FakeTimerRefreshDisplay extends RefreshingTimerDisplay {
        FakeTimerRefreshDisplay(Timer timer, TextView displayTextView) {
            super(timer, displayTextView);
        }

        @Override
        public boolean onUpdate(long timeElapsed) {
            Log.d(TAG, "Refreshed timer display!!! with time elapsed: " + timeElapsed);
            return true; // continue updating indefinately
        }
    }
}
