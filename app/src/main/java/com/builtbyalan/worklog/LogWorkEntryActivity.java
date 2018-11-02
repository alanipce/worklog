package com.builtbyalan.worklog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Date;

public class LogWorkEntryActivity extends AppCompatActivity {
    public static final String TAG = LogWorkEntryActivity.class.getSimpleName();

    public static final String EXTRA_PROJECT_FIREBASE_KEY = "worklog.logworkentry.intent.extra.projectfirebasekey";
    public static final String EXTRA_TASK_NAME = "worklog.logworkentry.intent.extra.taskname";
    public static final String EXTRA_START_TIME = "worklog.logworkentry.intent.extra.starttime";
    public static final String EXTRA_END_TIME = "worklog.logworkentry.intent.extra.endtime";

    public static final int DEFAULT_WORKSESSION_LENGTH_HOURS = 2;

    private DatabaseReference mWorkEntriesRef = FirebaseDatabase.getInstance().getReference().child("workentries");
    private String mFirebaseProjectKey;

    private DateFormatter mDateFormatter = new DateFormatter();
    private Calendar mStartTime;
    private Calendar mEndTime;

    private EditText mTaskEditText;
    private EditText mNotesEditText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_log_work_entry);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mFirebaseProjectKey = getIntent().getStringExtra(EXTRA_PROJECT_FIREBASE_KEY);

        // initalize datasources
        mStartTime = Calendar.getInstance();

        if (getIntent().hasExtra(EXTRA_START_TIME)) {
            Date startTime = (Date) getIntent().getSerializableExtra(EXTRA_START_TIME);
            mStartTime.setTime(startTime);
        } else {
            adjustStartTimeBasedOnDefaultTimeWorked(mStartTime);
        }

        mEndTime = Calendar.getInstance();

        if (getIntent().hasExtra(EXTRA_END_TIME)) {
            Date endTime = (Date) getIntent().getSerializableExtra(EXTRA_END_TIME);
            mEndTime.setTime(endTime);
        }

        // Obtain view handles
        Button updateStartTimeButton = findViewById(R.id.button_log_work_entry_change_starttime);
        TextView startTimeValueTextView = findViewById(R.id.text_log_work_entry_starttime_value);
        Button updateEndTimeButton = findViewById(R.id.button_log_work_entry_change_endtime);
        TextView endTimeValueTextView = findViewById(R.id.text_log_work_entry_endtime_value);
        mTaskEditText = findViewById(R.id.edittext_log_work_entry_task);
        mNotesEditText = findViewById(R.id.edittext_log_work_entry_notes);


        // update views
        registerDateTimeWidget(updateStartTimeButton, startTimeValueTextView, mStartTime);
        registerDateTimeWidget(updateEndTimeButton, endTimeValueTextView, mEndTime);

        if (getIntent().hasExtra(EXTRA_TASK_NAME)) {
            mTaskEditText.setText(getIntent().getStringExtra(EXTRA_TASK_NAME));
        }
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
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.menuitem_log_work_entry_create:
                createWorkEntry();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void createWorkEntry() {
        Log.d(TAG, "Creating work entry...");
        String task = mTaskEditText.getText().toString();
        String notes = mNotesEditText.getText().toString();
        Date startDate = mStartTime.getTime();
        Date endDate = mEndTime.getTime();

        WorkEntry entry = new WorkEntry(
                task,
                startDate,
                endDate,
                notes
        );

        entry.setProjectIdentifier(mFirebaseProjectKey);

        DatabaseReference mNewWorkEntryRef = mWorkEntriesRef.push();
        mNewWorkEntryRef.setValue(entry);

        finish();
    }

    private void adjustStartTimeBasedOnDefaultTimeWorked(Calendar startDateTime) {
        startDateTime.add(Calendar.HOUR, -DEFAULT_WORKSESSION_LENGTH_HOURS);
    }

    private void registerDateTimeWidget(Button button, final TextView textView, final Calendar dateState) {
        textView.setText(formatDateTime(dateState));

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateTimePickerDialogFragment dateTimeDialog = DateTimePickerDialogFragment.withDefaultDate(dateState);
                dateTimeDialog.setOnDateTimeSetListener(new DateTimePickerDialogFragment.OnDateTimeSetListener() {
                    @Override
                    public void onDateTimeSet(DateTimePickerDialogFragment dialogFragment, int year, int monthOfYear, int dayOfMonth, int hourOfDay, int minute) {
                        dateState.set(Calendar.YEAR, year);
                        dateState.set(Calendar.MONTH, monthOfYear);
                        dateState.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        dateState.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        dateState.set(Calendar.MINUTE, minute);

                        textView.setText(formatDateTime(dateState));
                    }
                });

                dateTimeDialog.show(getFragmentManager(), "datetimedialog");
            }
        });
    }

    private String formatDateTime(Calendar calendarDate) {
        if (DateUtils.isToday(calendarDate.getTimeInMillis())) {
            String formattedTime = mDateFormatter.formatTime(calendarDate.getTime());

            return "Today " + formattedTime;
        } else {
            return mDateFormatter.formatDateTime(calendarDate.getTime());
        }

    }
}
