package com.builtbyalan.worklog;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.support.v7.widget.Toolbar;
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

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;
import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection;

public class MainActivity extends AppCompatActivity implements AddProjectDialogFragment.AddProjectDialogListener {

    public static final String TAG = MainActivity.class.getSimpleName();

    private List<Project> mProjects;

    private DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference mProjectsRef = mRootRef.child("projects");

    private RecyclerView mRecyclerView;
    private LayoutManager mLayoutManager;
    private SectionedRecyclerViewAdapter mSectionedAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mProjects = new ArrayList<>();

        Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab_main_add_project);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddProjectDialog();
            }
        });

        mRecyclerView = findViewById(R.id.recyclerview_projects_list);
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        mSectionedAdapter = new SectionedRecyclerViewAdapter();
        mSectionedAdapter.addSection(new ProjectSection());

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mSectionedAdapter);
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

    @Override
    public void onRequestNewProject(AddProjectDialogFragment fragment, String projectTitle, String projectDescription) {
        Project newProject = new Project(projectTitle, projectDescription);

        DatabaseReference newProjectReference = mProjectsRef.push();
        newProjectReference.setValue(newProject);
    }

    public void showAddProjectDialog() {
        AddProjectDialogFragment fragment = new AddProjectDialogFragment();
        fragment.show(getFragmentManager(), "addproject");
    }

    public void handleProjectClick(Project project) {
        String key = project.getIdentifier();

        Log.d(TAG, "Project " + project.getTitle() + " clicked! with key " + key);

        Intent projectSummaryIntent = new Intent(this, ProjectSummaryActivity.class);
        projectSummaryIntent.putExtra(ProjectSummaryActivity.EXTRA_PROJECT_FIREBASE_KEY, key);
        projectSummaryIntent.putExtra(ProjectSummaryActivity.EXTRA_PROJECT_DATA, project);

        startActivity(projectSummaryIntent);
    }

    private ValueEventListener mOnProjectsChangeListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            mProjects.clear();

            for (DataSnapshot projectSnapshot: dataSnapshot.getChildren()) {
                Project p = projectSnapshot.getValue(Project.class);
                String key = projectSnapshot.getKey();

                p.setIdentifier(key);
                mProjects.add(p);
            }

            mSectionedAdapter.notifyDataSetChanged();
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            Log.d(TAG, "Failed to read projects.", databaseError.toException());
        }
    };

    class ProjectSection extends StatelessSection {
        ProjectSection() {
            super(SectionParameters.builder()
                    .itemResourceId(R.layout.item_project_entry)
                    .build());
        }

        @Override
        public int getContentItemsTotal() {
            return mProjects.size();
        }

        @Override
        public RecyclerView.ViewHolder getItemViewHolder(View view) {
            return new ProjectViewHolder(view);
        }

        @Override
        public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
            ProjectViewHolder vh = (ProjectViewHolder) holder;
            Project project = mProjects.get(position);

            vh.bind(project);
        }
    }

    class ProjectViewHolder extends RecyclerView.ViewHolder {
        TextView mTitleTextView;
        TextView mDescriptionTextView;
        TextView mTimerTextView;
        ViewGroup mContainerView;

        private StopWatch mStopWatch;

        ProjectViewHolder(View itemView) {
            super(itemView);

            mContainerView = itemView.findViewById(R.id.layout_project_entry_container);
            mTitleTextView = mContainerView.findViewById(R.id.text_project_entry_title);
            mTimerTextView = mContainerView.findViewById(R.id.text_project_entry_timer);
            mDescriptionTextView = mContainerView.findViewById(R.id.text_project_entry_description);
        }

        private void bind(final Project project) {
            mTitleTextView.setText(project.getTitle());
            mDescriptionTextView.setText(project.getDescription());
            mContainerView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handleProjectClick(project);
                }
            });


            mStopWatch = TimerUtils.getActiveStopWatch(MainActivity.this, project);

            if (mStopWatch == null) {
                mTimerTextView.setText("");
            } else {
                mStopWatch.setDisplay(mTimerTextView);
            }
        }

    }

}
