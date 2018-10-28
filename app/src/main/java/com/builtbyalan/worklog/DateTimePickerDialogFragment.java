package com.builtbyalan.worklog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TimePicker;

import java.util.Calendar;

public class DateTimePickerDialogFragment extends DialogFragment {
    private static final String DATE_ARGUMENT_KEY = "worklog.fragment.datetimepicker.arguments.date";

    private Calendar dateComponents;
    private OnDateTimeSetListener mListener;

    public static DateTimePickerDialogFragment withDefaultDate(Calendar date) {
        DateTimePickerDialogFragment fragment = new DateTimePickerDialogFragment();

        Bundle bundle = new Bundle();
        bundle.putSerializable(DATE_ARGUMENT_KEY, (Calendar) date.clone());

        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // initialize datasource
        Bundle argumentsBundle = getArguments();
        if (argumentsBundle != null && argumentsBundle.containsKey(DATE_ARGUMENT_KEY)) {
            dateComponents = (Calendar) argumentsBundle.getSerializable(DATE_ARGUMENT_KEY);
        } else {
            dateComponents = Calendar.getInstance();
        }

        // obtain handles on content views
        View contentView = getActivity().getLayoutInflater().inflate(R.layout.dialog_datetime_picker_content, null);
        TabLayout tabLayout = contentView.findViewById(R.id.layout_datetime_picker_dialog_tabs_container);
        final ViewGroup timeContainerViewGroup = contentView.findViewById(R.id.layout_datetime_picker_dialog_time_container);
        final ViewGroup dateContainerViewGroup = contentView.findViewById(R.id.layout_datetime_picker_dialog_date_container);
        DatePicker datePicker = dateContainerViewGroup.findViewById(R.id.datepicker_datetime_picker_dialog_selector);
        TimePicker timePicker = timeContainerViewGroup.findViewById(R.id.timepicker_datetime_picker_dialog_selector);

        final TabLayout.Tab timeTab = tabLayout.newTab();
        timeTab.setText("Time");
        tabLayout.addTab(timeTab, true);

        final TabLayout.Tab dateTab = tabLayout.newTab();
        dateTab.setText("Date");
        tabLayout.addTab(dateTab);

        timeContainerViewGroup.bringToFront();

        // register listeners
        datePicker.init(
                dateComponents.get(Calendar.YEAR),
                dateComponents.get(Calendar.MONTH),
                dateComponents.get(Calendar.DAY_OF_MONTH),
                new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        dateComponents.set(Calendar.YEAR, year);
                        dateComponents.set(Calendar.MONTH, monthOfYear);
                        dateComponents.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    }
                });

        timePicker.setCurrentHour(dateComponents.get(Calendar.HOUR_OF_DAY));
        timePicker.setCurrentMinute(dateComponents.get(Calendar.MINUTE));

        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                dateComponents.set(Calendar.HOUR_OF_DAY, hourOfDay);
                dateComponents.set(Calendar.MINUTE, minute);
            }
        });

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab == timeTab) {
                    timeContainerViewGroup.bringToFront();
                } else if (tab == dateTab) {
                    dateContainerViewGroup.bringToFront();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        return builder
                .setView(contentView)
                .setPositiveButton("SET", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        notifyDateTimeSetListener();
                    }
                })
                .setNegativeButton("CANCEL", null)
                .create();
    }

    private void notifyDateTimeSetListener() {
        if (mListener != null) {
            mListener.onDateTimeSet(
                    this,
                    dateComponents.get(Calendar.YEAR),
                    dateComponents.get(Calendar.MONTH),
                    dateComponents.get(Calendar.DAY_OF_MONTH),
                    dateComponents.get(Calendar.HOUR_OF_DAY),
                    dateComponents.get(Calendar.MINUTE)
            );
        }
    }


    public void setOnDateTimeSetListener(OnDateTimeSetListener listener) {
        mListener = listener;
    }

    public interface OnDateTimeSetListener {
        public void onDateTimeSet(DateTimePickerDialogFragment dialogFragment, int year, int monthOfYear, int dayOfMonth, int hourOfDay, int minute);
    }
}
