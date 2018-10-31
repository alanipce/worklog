package com.builtbyalan.worklog;

import android.os.Parcel;
import android.os.Parcelable;

public class Project implements Parcelable {
    private String title;
    private String description;
    private String identifier;

    public Project() {
        this("", "");
    }

    public Project(String title, String description) {
        this.title = title;
        this.description = description;
        this.identifier = "";
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public Project(Parcel in) {
        title = in.readString();
        description = in.readString();
        identifier = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(identifier);
    }

    public static final Parcelable.Creator<Project> CREATOR = new Parcelable.Creator<Project>() {
        @Override
        public Project createFromParcel(Parcel source) {
            return new Project(source);
        }

        @Override
        public Project[] newArray(int size) {
            return new Project[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }
}
