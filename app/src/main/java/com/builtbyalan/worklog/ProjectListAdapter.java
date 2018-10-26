package com.builtbyalan.worklog;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class ProjectListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    static final int VIEW_TYPE_HEADER = 0;
    static final int VIEW_TYPE_PROJECT_ENTRY = 1;

    private List<Project> mProjects;
    private ProjectListListener mListener;

    ProjectListAdapter(List<Project> projects) {
        mProjects = projects;
    }

    public void addEventListener(ProjectListListener listener) {
        mListener = listener;
    }

    private Project getProject(int position) {
        return mProjects.get(position - 1);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_HEADER:
                View headerView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_header_projects, parent, false);
                return new ProjectHeaderViewHolder(headerView);
            case VIEW_TYPE_PROJECT_ENTRY:
                View entryView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_entry_projects, parent, false);
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
                headerVH.mTitleTextView.setText("Projects");
                headerVH.mActionButton.setText("Add Project");
                headerVH.mActionButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListener.onAddProjectClick(ProjectListAdapter.this);
                    }
                });

                break;
            case VIEW_TYPE_PROJECT_ENTRY:
                ProjectEntryViewHolder entryVH = (ProjectEntryViewHolder) holder;
                final Project project = getProject(position);

                entryVH.mTitleTextView.setText(project.getTitle());
                entryVH.mDescriptionTextView.setText(project.getDescription());
                entryVH.mContainerView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListener.onProjectClick(ProjectListAdapter.this, project);
                    }
                });

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

    public interface ProjectListListener {
        public void onAddProjectClick(ProjectListAdapter adapter);
        public void onProjectClick(ProjectListAdapter adapter, Project project);
    }

    class ProjectHeaderViewHolder extends RecyclerView.ViewHolder {
        TextView mTitleTextView;
        Button mActionButton;

        ProjectHeaderViewHolder(View headerView) {
            super(headerView);

            mActionButton = headerView.findViewById(R.id.button_project_header_action);
            mTitleTextView = headerView.findViewById(R.id.text_project_header_title);
        }

    }
    class ProjectEntryViewHolder extends RecyclerView.ViewHolder {
        TextView mTitleTextView;
        TextView mDescriptionTextView;
        ViewGroup mContainerView;

        ProjectEntryViewHolder(View itemView) {
            super(itemView);

            mContainerView = itemView.findViewById(R.id.layout_project_entry_container);
            mTitleTextView = mContainerView.findViewById(R.id.text_project_entry_title);
            mDescriptionTextView = mContainerView.findViewById(R.id.text_project_entry_description);
        }
    }
}
