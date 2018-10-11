package com.google.firebase.example.takecare.model;

import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class Task {

    private FirebaseUser creator;
    private FirebaseUser owner;
    private List<FirebaseUser> subscribers;
    private String text;

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
}
