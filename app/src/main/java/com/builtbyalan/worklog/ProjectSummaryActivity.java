package com.builtbyalan.worklog;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.Section;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;
import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection;

public class ProjectSummaryActivity extends AppCompatActivity {
    public static final String TAG = ProjectSummaryActivity.class.getSimpleName();

    public static final String EXTRA_PROJECT_DATA = "worklog.projectsummary.intent.extra.projectdata";
    public static final String EXTRA_PROJECT_FIREBASE_KEY = "worklog.projectsummary.intent.extra.projectfirebasekey";

    private List<WorkEntry> mWorkEntries;
    private Project mCurrentProject;
    private StopWatch mProjectStopWatch;

    private RecyclerView mRecyclerView;
    private ViewGroup mContainerView;
    private SectionedRecyclerViewAdapter mSectionedAdapter;
    private Section mProjectTimerSection;

    private DateFormatter mDateFormatter = new DateFormatter();
    private DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    private Query mWorkEntriesQuery;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_project_summary);

        mCurrentProject = getIntent().getParcelableExtra(EXTRA_PROJECT_DATA);

        mWorkEntries = new ArrayList<>();
        mWorkEntriesQuery = mRootRef.child("workentries").orderByChild("projectIdentifier").equalTo(mCurrentProject.getIdentifier());

        getSupportActionBar().setTitle(mCurrentProject.getTitle());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mSectionedAdapter = new SectionedRecyclerViewAdapter();
        mProjectTimerSection = new ProjectTimerSection();

        mSectionedAdapter.addSection(mProjectTimerSection);
        mSectionedAdapter.addSection(new WorkEntrySection());

        mContainerView = findViewById(R.id.layout_project_summary_container);
        mRecyclerView = mContainerView.findViewById(R.id.recyclerview_work_entry_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(mSectionedAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();

        mWorkEntriesQuery.addValueEventListener(mOnChangeWorkEntriesEventListener);

        StopWatch activeStopWatch = TimerUtils.getActiveStopWatch(this, mCurrentProject);

        if (activeStopWatch == null) {
            mProjectStopWatch = new StopWatch(new TaskTimer());
        } else {
            mProjectStopWatch = activeStopWatch;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        mWorkEntriesQuery.removeEventListener(mOnChangeWorkEntriesEventListener);

        TimerUtils.storeStopWatchIfNecessary(this, mProjectStopWatch, mCurrentProject);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }


        return super.onOptionsItemSelected(item);
    }

    private void startTimer() {
        mProjectStopWatch.startTimer();
        mSectionedAdapter.notifyItemChangedInSection(mProjectTimerSection, 0);

        Log.d(TAG, "Starting new timer...");
    }

    private void stopTimer() {
        mProjectStopWatch.stopTimer();
        Log.d(TAG, "Stopping timer with final time elapsed of: " + mProjectStopWatch.getTimeElapsed());
        logNewWorkEntry(mProjectStopWatch.getTimerName(), mProjectStopWatch.getTimeElapsed());

        TimerUtils.clearStopWatch(this, mCurrentProject);

        // "reset" the project timer
        mProjectStopWatch.resetTimer();
        mContainerView.requestFocus(); // remove focus
        mSectionedAdapter.notifyItemChangedInSection(mProjectTimerSection, 0);
    }

    private void logNewWorkEntry(String taskName, long timeWorked) {
        int secondsWorked = (int) (timeWorked/1000L);

        Calendar now = Calendar.getInstance();
        Date endTime = now.getTime();
        now.add(Calendar.SECOND, -secondsWorked);
        Date startTime = now.getTime();


        Intent logWorkEntryIntent = new Intent(ProjectSummaryActivity.this, LogWorkEntryActivity.class);
        logWorkEntryIntent.putExtra(LogWorkEntryActivity.EXTRA_PROJECT_FIREBASE_KEY, mCurrentProject.getIdentifier());
        logWorkEntryIntent.putExtra(LogWorkEntryActivity.EXTRA_TASK_NAME, taskName);
        logWorkEntryIntent.putExtra(LogWorkEntryActivity.EXTRA_START_TIME, startTime);
        logWorkEntryIntent.putExtra(LogWorkEntryActivity.EXTRA_END_TIME, endTime);
        startActivity(logWorkEntryIntent);
    }

    private void logNewWorkEntry() {
        Intent logWorkEntryIntent = new Intent(ProjectSummaryActivity.this, LogWorkEntryActivity.class);
        logWorkEntryIntent.putExtra(LogWorkEntryActivity.EXTRA_PROJECT_FIREBASE_KEY, mCurrentProject.getIdentifier());
        startActivity(logWorkEntryIntent);
    }

    private ValueEventListener mOnChangeWorkEntriesEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            mWorkEntries.clear();

            for (DataSnapshot workEntrySnapshot: dataSnapshot.getChildren()) {
                WorkEntry work = workEntrySnapshot.getValue(WorkEntry.class);

                mWorkEntries.add(work);
            }

            mSectionedAdapter.notifyDataSetChanged();

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            Log.d(TAG, "Failed to read work entries.", databaseError.toException());
        }
    };

    class ProjectTimerSection extends StatelessSection {
        ProjectTimerSection() {
            super(SectionParameters.builder()
                    .itemResourceId(R.layout.item_project_timer)
                    .build());
        }

        @Override
        public int getContentItemsTotal() {
            return 1;
        }

        @Override
        public RecyclerView.ViewHolder getItemViewHolder(View view) {
            return new ProjectTimerViewHolder(view);
        }

        @Override
        public void onBindItemViewHolder(RecyclerView.ViewHolder holder, final int position) {
            ProjectTimerViewHolder vh = (ProjectTimerViewHolder) holder;

            if (mProjectStopWatch == null) {
                return;
            }

            // initialize the task displayed in timer
            vh.taskTitleEditText.setText(mProjectStopWatch.getTimerName());


            // handle stopped and started states of list item

            if (mProjectStopWatch.isTimerIdle()) {
                vh.timerDisplayTextView.setVisibility(View.INVISIBLE);

                vh.stopTimerButton.setVisibility(View.INVISIBLE);
                vh.stopTimerButton.setOnClickListener(null);

                vh.startTimerButton.setVisibility(View.VISIBLE);
                vh.startTimerButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startTimer();
                    }
                });
            } else {
                mProjectStopWatch.setDisplay(vh.timerDisplayTextView);

                vh.timerDisplayTextView.setVisibility(View.VISIBLE);

                vh.stopTimerButton.setVisibility(View.VISIBLE);
                vh.stopTimerButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        stopTimer();
                    }
                });

                vh.startTimerButton.setVisibility(View.INVISIBLE);
                vh.startTimerButton.setOnClickListener(null);
            }
        }
    }

    class WorkEntrySection extends StatelessSection {

        WorkEntrySection() {
            super(SectionParameters.builder()
                    .itemResourceId(R.layout.item_work_entry)
                    .headerResourceId(R.layout.item_action_header)
                    .build());
        }

        @Override
        public int getContentItemsTotal() {
            return mWorkEntries.size();
        }

        @Override
        public RecyclerView.ViewHolder getItemViewHolder(View view) {
            return new WorkEntryViewHolder(view);
        }

        @Override
        public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
            WorkEntry entry = mWorkEntries.get(position);

            WorkEntryViewHolder vh = (WorkEntryViewHolder) holder;
            vh.titleTextView.setText(entry.getTask());
            vh.notesTextView.setText(entry.getNotes());
            vh.hoursTextView.setText(mDateFormatter.formatElapsedTime(entry.getStartDate(), entry.getEndDate(), false));
            vh.datesTextView.setText(mDateFormatter.formatDateRange(entry.getStartDate(), entry.getEndDate()));
        }

        @Override
        public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
            return new ActionHeaderViewHolder(view);
        }

        @Override
        public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
            ActionHeaderViewHolder vh = (ActionHeaderViewHolder) holder;
            vh.mTitleTextView.setText("Work Entries");
            vh.mActionButton.setText("Log Entry");
            vh.mActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    logNewWorkEntry();
                }
            });

        }
    }

    private class ProjectTimerViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        EditText taskTitleEditText;
        TextView timerDisplayTextView;
        Button startTimerButton;
        Button stopTimerButton;

        ProjectTimerViewHolder(View itemView) {
            super(itemView);

            titleTextView = itemView.findViewById(R.id.text_project_timer_title);
            taskTitleEditText = itemView.findViewById(R.id.edittext_project_timer_task);
            timerDisplayTextView = itemView.findViewById(R.id.text_project_timer_display);
            startTimerButton = itemView.findViewById(R.id.button_project_timer_start);
            stopTimerButton = itemView.findViewById(R.id.button_project_timer_stop);

            taskTitleEditText.addTextChangedListener(new TaskNameEditTextListener());
        }

        private class TaskNameEditTextListener implements TextWatcher {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mProjectStopWatch.setTimerName(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        }
    }

    private class WorkEntryViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView notesTextView;
        TextView datesTextView;
        TextView hoursTextView;


        WorkEntryViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.text_work_entry_item_title);
            notesTextView = itemView.findViewById(R.id.text_work_entry_item_notes);
            datesTextView = itemView.findViewById(R.id.text_work_entry_item_dates);
            hoursTextView = itemView.findViewById(R.id.text_work_entry_item_hours);
        }
    }
}
