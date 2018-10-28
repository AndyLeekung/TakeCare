package com.google.firebase.example.takecare.model;

import com.google.firebase.Timestamp;

public class Task {

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
}
