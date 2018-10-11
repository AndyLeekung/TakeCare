package com.google.firebase.example.takecare.model;

import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class Group {

    private List<FirebaseUser> members;

    private List<Task> tasks;

    public Group() {}

    public List<FirebaseUser> getMembers() {
        return members;
    }

    public void setMembers(List<FirebaseUser> members) {
        this.members = members;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }
}
