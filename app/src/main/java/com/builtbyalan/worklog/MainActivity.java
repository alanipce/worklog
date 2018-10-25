package com.builtbyalan.worklog;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = MainActivity.class.getSimpleName();

    private List<String> mProjects;
    private ProjectValueEventListener mOnProjectsChangeListener = new ProjectValueEventListener();

    private DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference mProjectsRef = mRootRef.child("projects");

    private RecyclerView mRecyclerView;
    private LayoutManager mLayoutManager;
    private ProjectListAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mProjects = new ArrayList<>();

        mRecyclerView = findViewById(R.id.recyclerview_projects_list);
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mAdapter = new ProjectListAdapter(mProjects);


        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();

        mProjectsRef.addValueEventListener(mOnProjectsChangeListener);
    }

    @Override
    protected void onStop() {
        super.onStop();

        mProjectsRef.removeEventListener(mOnProjectsChangeListener);
    }

    public class ProjectValueEventListener implements ValueEventListener {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            mProjects.clear();

            for (DataSnapshot projectSnapshot: dataSnapshot.getChildren()) {
                String project = projectSnapshot.getValue(String.class);
                mProjects.add(project);
            }

            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            Log.d(TAG, "Failed to read projects.", databaseError.toException());
        }
    }

    public class ProjectListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        static final int VIEW_TYPE_HEADER = 0;
        static final int VIEW_TYPE_PROJECT_ENTRY = 1;

        private List<String> mProjects;

        ProjectListAdapter(List<String> projects) {
            mProjects = projects;
        }

        String getProject(int position) {
            return mProjects.get(position - 1);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            switch (viewType) {
                case VIEW_TYPE_HEADER:
                    View headerView = getLayoutInflater().inflate(R.layout.item_header_projects, parent, false);
                    return new ProjectHeaderViewHolder(headerView);
                case VIEW_TYPE_PROJECT_ENTRY:
                    View entryView = getLayoutInflater().inflate(R.layout.item_entry_projects, parent, false);
                    return new ProjectEntryViewHolder(entryView);
                default:
                    return null;
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            switch (holder.getItemViewType()) {
                case VIEW_TYPE_HEADER:
                    ProjectHeaderViewHolder headerVH = (ProjectHeaderViewHolder) holder;
                    headerVH.mHeaderTextView.setText("PROJECTS");

                    break;
                case VIEW_TYPE_PROJECT_ENTRY:
                    ProjectEntryViewHolder entryVH = (ProjectEntryViewHolder) holder;
                    String projectTitle = getProject(position);

                    entryVH.mTitleTextView.setText(projectTitle);

                    break;
            }
        }

        @Override
        public int getItemCount() {
            return mProjects.size() + 1;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0) {
                return VIEW_TYPE_HEADER;
            } else {
                return VIEW_TYPE_PROJECT_ENTRY;
            }
        }

        class ProjectHeaderViewHolder extends RecyclerView.ViewHolder {
            TextView mHeaderTextView;

            ProjectHeaderViewHolder(View headerView) {
                super(headerView);

                mHeaderTextView = (TextView) headerView;
            }

        }
        class ProjectEntryViewHolder extends RecyclerView.ViewHolder {
            TextView mTitleTextView;

            ProjectEntryViewHolder(View itemView) {
                super(itemView);

                mTitleTextView = (TextView) itemView;
            }
        }
    }
}
