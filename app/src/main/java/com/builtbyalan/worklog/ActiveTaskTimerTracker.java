package com.builtbyalan.worklog;

import android.content.Context;
import android.content.SharedPreferences;

public class ActiveTaskTimerTracker {
    private static ActiveTaskTimerTracker mInstance;
    public static final String PREFERENCES_FILENAME = "worklog.timertracker.sharedpreferences";

    private Context mContext;

    public ActiveTaskTimerTracker(Context context) {
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
    public static ActiveTaskTimerTracker getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new ActiveTaskTimerTracker(context);
        }

        return mInstance;
    }
}
