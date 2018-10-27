package com.builtbyalan.worklog;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class LogWorkEntryActivity extends AppCompatActivity {
    public static final String TAG = LogWorkEntryActivity.class.getSimpleName();
    public static final int DEFAULT_WORKSESSION_LENGTH_HOURS = 2;

    private Calendar startTime;
    private Calendar endTime;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_log_work_entry);

        Button updateStartTimeButton = findViewById(R.id.button_log_work_entry_change_starttime);
        TextView startTimeValueTextView = findViewById(R.id.text_log_work_entry_starttime_value);
        Button updateEndTimeButton = findViewById(R.id.button_log_work_entry_change_endtime);
        TextView endTimeValueTextView = findViewById(R.id.text_log_work_entry_endtime_value);

        startTime = Calendar.getInstance();
        adjustStartTimeBasedOnDefaultTimeWorked(startTime);

        endTime = Calendar.getInstance();

        registerDateTimeWidget(updateStartTimeButton, startTimeValueTextView, startTime);
        registerDateTimeWidget(updateEndTimeButton, endTimeValueTextView, endTime);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_log_work_entry, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuitem_log_work_entry_create:
                createWorkEntry();
                return true;
        }


        return super.onOptionsItemSelected(item);
    }


    private void createWorkEntry() {
        Log.d(TAG, "Creating work entry...");
    }

    private void adjustStartTimeBasedOnDefaultTimeWorked(Calendar startDateTime) {
        startDateTime.add(Calendar.HOUR, -DEFAULT_WORKSESSION_LENGTH_HOURS);
    }

    private void registerDateTimeWidget(Button button, final TextView textView, final Calendar dateState) {
        textView.setText(formatDateTime(dateState));

        final TimePickerDialog dialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                dateState.set(Calendar.HOUR_OF_DAY, hourOfDay);
                dateState.set(Calendar.MINUTE, minute);

                textView.setText(formatDateTime(dateState));
            }
        }, dateState.get(Calendar.HOUR_OF_DAY), dateState.get(Calendar.MINUTE), false);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });
    }

    private String formatDateTime(Calendar calendarDate) {
        if (DateUtils.isToday(calendarDate.getTimeInMillis())) {
            SimpleDateFormat formatter = new SimpleDateFormat("h:mm a");
            String formattedTime = formatter.format(calendarDate.getTime());

            return "Today " + formattedTime;
        } else {
            SimpleDateFormat formatter = new SimpleDateFormat("MMM d h:mm a");

            return formatter.format(calendarDate.getTime());
        }

    }
}
