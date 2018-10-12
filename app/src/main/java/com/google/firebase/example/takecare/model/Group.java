package com.google.firebase.example.takecare.model;

import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class Group {

    // Using email for now for simplicity
    private List<String> members;
    private String name;

    public Group() {}

    public Group(FirebaseUser creator) {
        this.name = creator.getDisplayName() + "'s group";
        this.members = new ArrayList<>();
        this.members.add(creator.getEmail());
    }

    public Group(FirebaseUser creator, String name) {
        this.name = name;
        this.members = new ArrayList<>();
        this.members.add(creator.getEmail());
    }

    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
