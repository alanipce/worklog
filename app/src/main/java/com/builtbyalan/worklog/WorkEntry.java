package com.builtbyalan.worklog;

import java.util.Calendar;
import java.util.Date;

public class WorkEntry {
    private String task;
    private Date startDate;
    private Date endDate;
    private String notes;
    private String projectIdentifier;

    public WorkEntry() {
        this("", Calendar.getInstance().getTime(), Calendar.getInstance().getTime(), "");
    }

    public WorkEntry(String task, Date startDate, Date endDate, String notes) {
        this.task = task;
        this.startDate = startDate;
        this.endDate = endDate;
        this.notes = notes;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getProjectIdentifier() {
        return projectIdentifier;
    }

    public void setProjectIdentifier(String projectIdentifier) {
        this.projectIdentifier = projectIdentifier;
    }
}
