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

        projects = new String[] {"CaseAide® Note", "Personal Website"};

        mRecyclerView = findViewById(R.id.recyclerview_projects_list);
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mAdapter = new ProjectListAdapter(projects);


        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

    }

    public class ProjectListAdapter extends RecyclerView.Adapter<ProjectListAdapter.ViewHolder> {
        private String[] mProjects;

        ProjectListAdapter(String[] projects) {
            mProjects = projects;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = getLayoutInflater().inflate(R.layout.item_projects, parent, false);

            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            String project = mProjects[position];

            holder.mTextView.setText(project);
        }

        @Override
        public int getItemCount() {
            return mProjects.length;
        }


        class ViewHolder extends RecyclerView.ViewHolder {
            TextView mTextView;

            ViewHolder(View itemView) {
                super(itemView);

                mTextView = (TextView) itemView;
            }
        }
    }
}
