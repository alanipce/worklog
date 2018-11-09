package com.builtbyalan.worklog;

import android.content.Context;

public class TimerUtils {
    public static StopWatch getActiveStopWatch(Context context, Project project) {
        TaskTimer timer = TimerTracker.getInstance(context).getTimer(project.getIdentifier());

        if (timer == null) {
            return null;
        }

        timer.resume();
        return new StopWatch(timer);
    }

    public static void storeStopWatchIfNecessary(Context context, StopWatch stopWatch, Project project) {
        if (stopWatch.isTimerRunning()) {
            TimerTracker.getInstance(context).trackTimer(stopWatch.getTimer(), project.getIdentifier());
        }
    }

    public static void clearStopWatch(Context context, Project project) {
        TimerTracker.getInstance(context).removeTimer(project.getIdentifier());
    }
}
