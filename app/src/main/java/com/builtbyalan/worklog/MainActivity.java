package com.builtbyalan.worklog;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private String[] projects;

    private RecyclerView mRecyclerView;
    private LayoutManager mLayoutManager;
    private ProjectListAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        projects = new String[] {"CaseAide® Note", "Personal Website", "Worklog"};

        mRecyclerView = findViewById(R.id.recyclerview_projects_list);
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mAdapter = new ProjectListAdapter(projects);


        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

    }

    public class ProjectListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        static final int VIEW_TYPE_HEADER = 0;
        static final int VIEW_TYPE_PROJECT_ENTRY = 1;

        private String[] mProjects;

        ProjectListAdapter(String[] projects) {
            mProjects = projects;
        }

        String getProject(int position) {
            return mProjects[position - 1];
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
            return mProjects.length + 1;
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
