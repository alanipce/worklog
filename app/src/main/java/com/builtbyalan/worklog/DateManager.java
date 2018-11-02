package com.builtbyalan.worklog;

import java.text.SimpleDateFormat;
import java.util.Date;

// formats dates using the apps predefined date formats
public class DateManager {
    private static final String DAY_COMPARISON_FORMAT = "yyyyMMdd";
    // access using internal accessor method
    private SimpleDateFormat mSimpleFormatter;

    public String formatTime(Date time) {
        return getSimpleFormatter(getTimePattern()).format(time);
    }

    public String formatDate(Date date) {
        return getSimpleFormatter(getDatePattern()).format(date);
    }

    public String formatDateRange(Date start, Date end) {
        if (isSameDay(start, end)) {
            return formatDate(start);
        }

        return formatDate(start) + AppConstants.DATE_RANGE_SEPARATOR + formatDate(end);
    }

    public String formatDateTime(Date date) {
        return getSimpleFormatter(getDateTimePattern()).format(date);
    }

    public String formatElapsedTime(Date start, Date end) {
        return formatElapsedTime(end.getTime() - start.getTime());
    }

    public String formatElapsedTime(long millis) {
        // going to loose time elapsed less than a minute which is ok in this case
        int minutes = (int) (millis / (1000 * 60));
        return formatElapsedTime(minutes);
    }

    public String formatElapsedTime(int elapsedMinutes) {
        int hours = elapsedMinutes / 60;
        int minutes = elapsedMinutes % 60;

        return String.format("%02d:%02d", hours, minutes);
    }

    public boolean isSameDay(Date left, Date right) {
        SimpleDateFormat formatter = getSimpleFormatter(DAY_COMPARISON_FORMAT);

        return formatter.format(left).equals(formatter.format(right));
    }

    private String getTimePattern() {
        return AppConstants.BASIC_TIME_FORMAT;
    }

    private String getDatePattern() {
        return AppConstants.BASIC_DATE_FORMAT;
    }

    private String getDateTimePattern() {
        return AppConstants.BASIC_DATETIME_FORMAT;
    }

    private SimpleDateFormat getSimpleFormatter(String pattern) {
        if (mSimpleFormatter == null) {
            mSimpleFormatter = new SimpleDateFormat(pattern);
        } else {
            mSimpleFormatter.applyPattern(pattern);
        }

        return mSimpleFormatter;
    }
}