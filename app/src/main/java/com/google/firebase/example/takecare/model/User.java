package com.google.firebase.example.takecare.model;

import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String name;
    private String email;
    private String token;

    public User() {}

    public User(FirebaseUser user) {
        this.name = user.getDisplayName();
        this.email = user.getEmail();
    }

    public User(FirebaseUser user, String token) {
        this(user);
        this.token = token;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
