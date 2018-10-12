package com.google.firebase.example.takecare.model;

import com.google.firebase.auth.FirebaseUser;

import java.util.Date;
import java.util.List;

public class Task {

    private FirebaseUser creator;
    private FirebaseUser owner;
    private List<FirebaseUser> subscribers;
    private String text;
    private Date deadline;

    public Task() {}

    public FirebaseUser getCreator() {
        return creator;
    }

    public void setCreator(FirebaseUser creator) {
        this.creator = creator;
    }

    public FirebaseUser getOwner() {
        return owner;
    }

    public void setOwner(FirebaseUser owner) {
        this.owner = owner;
    }

    public List<FirebaseUser> getSubscribers() {
        return subscribers;
    }

    public void setSubscribers(List<FirebaseUser> subscribers) {
        this.subscribers = subscribers;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }
}
