package com.builtbyalan.worklog;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class LogWorkActivity extends AppCompatActivity {
    public static final String TAG = LogWorkActivity.class.getSimpleName();

    public static final String EXTRA_PROJECT_DATA = "worklog.intent.extra.projectdata";
    public static final String EXTRA_PROJECT_KEY = "worklog.intent.extra.projectkey";

    private List<WorkEntry> mWorkEntries;
    private Project mCurrentProject;
    private String mFirebaseProjectKey;

    private DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference mWorkEntriesRef = mRootRef.child("workentries");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_log_work);

        mWorkEntries = new ArrayList<>();
        mCurrentProject = getIntent().getParcelableExtra(EXTRA_PROJECT_DATA);
        mFirebaseProjectKey = getIntent().getStringExtra(EXTRA_PROJECT_KEY);

        getSupportActionBar().setTitle(mCurrentProject.getTitle());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onStart() {
        super.onStart();

        mWorkEntriesRef.addValueEventListener(mOnChangeWorkEntriesEventListener);
    }

    @Override
    protected void onStop() {
        super.onStop();

        mWorkEntriesRef.removeEventListener(mOnChangeWorkEntriesEventListener);
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

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            Log.d(TAG, "Failed to read work entries.", databaseError.toException());
        }
    };
}
