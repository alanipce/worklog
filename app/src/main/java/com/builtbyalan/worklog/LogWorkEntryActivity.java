package com.builtbyalan.worklog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.Calendar;

public class LogWorkEntryActivity extends AppCompatActivity {
    public static final String TAG = LogWorkEntryActivity.class.getSimpleName();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_log_work_entry);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_log_work_entry, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuitem_log_work_entry_create:
                createWorkEntry();
                return true;
        }


        return super.onOptionsItemSelected(item);
    }


    private void createWorkEntry() {
        Log.d(TAG, "Creating work entry...");
    }
}
