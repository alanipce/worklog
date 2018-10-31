package com.builtbyalan.worklog;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

class ActionHeaderViewHolder extends RecyclerView.ViewHolder {
    TextView mTitleTextView;
    Button mActionButton;

    ActionHeaderViewHolder(View headerView) {
        super(headerView);

        mActionButton = headerView.findViewById(R.id.button_project_header_action);
        mTitleTextView = headerView.findViewById(R.id.text_project_header_title);
    }
}
