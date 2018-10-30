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
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ProjectSummaryActivity extends AppCompatActivity {
    public static final String TAG = ProjectSummaryActivity.class.getSimpleName();

    public static final String EXTRA_PROJECT_DATA = "worklog.projectsummary.intent.extra.projectdata";
    public static final String EXTRA_PROJECT_FIREBASE_KEY = "worklog.projectsummary.intent.extra.projectfirebasekey";

    private List<WorkEntry> mWorkEntries;
    private Project mCurrentProject;
    private String mFirebaseProjectKey;

    private RecyclerView mRecyclerView;


    private DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    private Query mWorkEntriesQuery;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_project_summary);

        mCurrentProject = getIntent().getParcelableExtra(EXTRA_PROJECT_DATA);
        mFirebaseProjectKey = getIntent().getStringExtra(EXTRA_PROJECT_FIREBASE_KEY);

        mWorkEntries = new ArrayList<>();
        mWorkEntriesQuery = mRootRef.child("workentries").orderByChild("projectIdentifier").equalTo(mFirebaseProjectKey);

        getSupportActionBar().setTitle(mCurrentProject.getTitle());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRecyclerView = findViewById(R.id.recyclerview_work_entry_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(mWorkEntryAdapter);
//        findViewById(R.id.button_project_summary_log_entry_button).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent logWorkEntryIntent = new Intent(ProjectSummaryActivity.this, LogWorkEntryActivity.class);
//                logWorkEntryIntent.putExtra(LogWorkEntryActivity.EXTRA_PROJECT_FIREBASE_KEY, mFirebaseProjectKey);
//                startActivity(logWorkEntryIntent);
//            }
//        });
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

    private ValueEventListener mOnChangeWorkEntriesEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            mWorkEntries.clear();

            for (DataSnapshot workEntrySnapshot: dataSnapshot.getChildren()) {
                WorkEntry work = workEntrySnapshot.getValue(WorkEntry.class);

                mWorkEntries.add(work);
            }

            mWorkEntryAdapter.notifyDataSetChanged();

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            Log.d(TAG, "Failed to read work entries.", databaseError.toException());
        }
    };


    private RecyclerView.Adapter mWorkEntryAdapter = new RecyclerView.Adapter() {
        class WorkEntryViewHolder extends RecyclerView.ViewHolder {
            TextView mTitleTextView;

            WorkEntryViewHolder(View itemView) {
                super(itemView);
                mTitleTextView = (TextView) itemView;
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = getLayoutInflater().inflate(R.layout.item_work_entry, parent, false);
            return new WorkEntryViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            WorkEntry entry = mWorkEntries.get(position);

            WorkEntryViewHolder vh = (WorkEntryViewHolder) holder;
            vh.mTitleTextView.setText(entry.getTask());
        }

        @Override
        public int getItemCount() {
            return mWorkEntries.size();
        }
    };
}
