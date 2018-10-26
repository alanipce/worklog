package com.builtbyalan.worklog;

public class WorkEntry {
    private String title;

    public WorkEntry() {
        this("");
    }

    public WorkEntry(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
