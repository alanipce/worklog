package com.builtbyalan.worklog;

import android.content.Context;
import android.content.SharedPreferences;

public class TimerTracker {
    private static TimerTracker mInstance;
    public static final String PREFERENCES_FILENAME = "worklog.timertracker.sharedpreferences";

    private Context mContext;

    public TimerTracker(Context context) {
        mContext = context;
    }

    public void removeTimer(String projectIdentifier) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(projectIdentifier, null);
        editor.apply();
    }

    public void trackTimer(TaskTimer timer, String projectIdentifier) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        String encodedCookie = timer.encodedCookie();
        editor.putString(projectIdentifier, encodedCookie);
        editor.apply();
    }

    public TaskTimer getTimer(String projectIdentifier) {
        SharedPreferences preferences = getSharedPreferences();
        String encodedCookie = preferences.getString(projectIdentifier, null);

        if (encodedCookie == null) {
            return null;
        }

        return new TaskTimer(encodedCookie);
    }

    private SharedPreferences getSharedPreferences() {
        return mContext.getSharedPreferences(PREFERENCES_FILENAME, Context.MODE_PRIVATE);
    }

    // convenience method for using shared instance across app
    public static TimerTracker getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new TimerTracker(context);
        }

        return mInstance;
    }
}
