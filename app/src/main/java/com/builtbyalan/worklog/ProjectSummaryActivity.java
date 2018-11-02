package com.builtbyalan.worklog;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
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
    private Timer mProjectTimer;

    private RecyclerView mRecyclerView;
    private SectionedRecyclerViewAdapter mSectionedAdapter;
    private Section mProjectTimerSection;


    private DateManager mDateManager = new DateManager();
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

        mRecyclerView = findViewById(R.id.recyclerview_work_entry_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(mSectionedAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();

        mWorkEntriesQuery.addValueEventListener(mOnChangeWorkEntriesEventListener);
    }

    @Override
    protected void onStop() {
        super.onStop();

        mWorkEntriesQuery.removeEventListener(mOnChangeWorkEntriesEventListener);
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
        if (mProjectTimer == null) {
            mProjectTimer = new Timer();
        }

        mProjectTimer.start();
        mSectionedAdapter.notifyItemChangedInSection(mProjectTimerSection, 0);

        Log.d(TAG, "Starting new timer...");
    }

    private void stopTimer() {
        mProjectTimer.stop();

        Log.d(TAG, "Stopping timer with final time elapsed of: " + mProjectTimer.getTimeElapsed());
        // send data to the correct method
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
        private RefreshingTimerDisplay mRefreshingTimerDisplay;

        ProjectTimerSection() {
            super(SectionParameters.builder()
                    .itemResourceId(R.layout.item_project_timer)
                    .build());

            mRefreshingTimerDisplay = new RefreshingTimerDisplay();
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

            if (mProjectTimer == null || mProjectTimer.isIdle()) {
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
                mRefreshingTimerDisplay.beginUpdating(mProjectTimer, vh.timerDisplayTextView);

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
            vh.hoursTextView.setText(mDateManager.formatElapsedTime(entry.getStartDate(), entry.getEndDate()));
            vh.datesTextView.setText(mDateManager.formatDateRange(entry.getStartDate(), entry.getEndDate()));
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
        EditText taskTitleTextView;
        TextView timerDisplayTextView;
        Button startTimerButton;
        Button stopTimerButton;

        ProjectTimerViewHolder(View itemView) {
            super(itemView);

            titleTextView = itemView.findViewById(R.id.text_project_timer_title);
            taskTitleTextView = itemView.findViewById(R.id.edittext_project_timer_task);
            timerDisplayTextView = itemView.findViewById(R.id.text_project_timer_display);
            startTimerButton = itemView.findViewById(R.id.button_project_timer_start);
            stopTimerButton = itemView.findViewById(R.id.button_project_timer_stop);
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
