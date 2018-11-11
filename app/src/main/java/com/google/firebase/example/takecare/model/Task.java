package com.google.firebase.example.takecare.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.Timestamp;

public class Task implements Parcelable {

    private String taskId;
    private String creator;
    private String owner;
    private String text;
    private Timestamp deadline;
    private boolean isComplete;
    private String groupId;

    public Task() {}

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Timestamp getDeadline() {
        return deadline;
    }

    public void setDeadline(Timestamp deadline) {
        this.deadline = deadline;
    }

    public boolean isComplete() {
        return isComplete;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public void setComplete(boolean complete) {
        isComplete = complete;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.taskId);
        dest.writeString(this.creator);
        dest.writeString(this.owner);
        dest.writeString(this.text);
        dest.writeParcelable(this.deadline, flags);
        dest.writeByte(this.isComplete ? (byte) 1 : (byte) 0);
        dest.writeString(this.groupId);
    }

    protected Task(Parcel in) {
        this.taskId = in.readString();
        this.creator = in.readString();
        this.owner = in.readString();
        this.text = in.readString();
        this.deadline = in.readParcelable(Timestamp.class.getClassLoader());
        this.isComplete = in.readByte() != 0;
        this.groupId = in.readString();
    }

    public static final Parcelable.Creator<Task> CREATOR = new Parcelable.Creator<Task>() {
        @Override
        public Task createFromParcel(Parcel source) {
            return new Task(source);
        }

        @Override
        public Task[] newArray(int size) {
            return new Task[size];
        }
    };
}
