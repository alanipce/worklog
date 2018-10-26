package com.builtbyalan.worklog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

public class LogWorkActivity extends AppCompatActivity {
    public static final String EXTRA_PROJECT_DATA = "worklog.intent.extra.projectdata";
    public static final String EXTRA_PROJECT_KEY = "worklog.intent.extra.projectkey";

    private Project mCurrentProject;
    private String mFirebaseProjectKey;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_log_work);

        mCurrentProject = getIntent().getParcelableExtra(EXTRA_PROJECT_DATA);
        mFirebaseProjectKey = getIntent().getStringExtra(EXTRA_PROJECT_KEY);

        getSupportActionBar().setTitle(mCurrentProject.getTitle());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
}
