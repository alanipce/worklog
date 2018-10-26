package com.builtbyalan.worklog;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
                            implements ProjectListAdapter.ProjectListListener, AddProjectDialogFragment.AddProjectDialogListener {

    public static final String TAG = MainActivity.class.getSimpleName();

    private List<Project> mProjects;
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
        mAdapter.addEventListener(this);

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

    @Override
    public void onRequestNewProject(AddProjectDialogFragment fragment, String projectTitle, String projectDescription) {
        Project newProject = new Project(projectTitle, projectDescription);

        DatabaseReference newProjectReference = mProjectsRef.push();
        newProjectReference.setValue(newProject);
    }

    @Override
    public void onAddProjectClick(ProjectListAdapter adapter) {
        AddProjectDialogFragment fragment = new AddProjectDialogFragment();
        fragment.show(getFragmentManager(), "addproject");
    }

    @Override
    public void onProjectClick(ProjectListAdapter adapter, Project project) {
        Log.d(TAG, "Project " + project.getTitle() + " clicked!");
    }

    public class ProjectValueEventListener implements ValueEventListener {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            mProjects.clear();

            for (DataSnapshot projectSnapshot: dataSnapshot.getChildren()) {
                Project p = projectSnapshot.getValue(Project.class);
                mProjects.add(p);
            }

            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            Log.d(TAG, "Failed to read projects.", databaseError.toException());
        }
    }

}
