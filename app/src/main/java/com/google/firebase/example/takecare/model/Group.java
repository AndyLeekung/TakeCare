package com.google.firebase.example.takecare.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class Group implements Parcelable {

    private String name;
    private List<String> members;

    public Group() {}

    public Group(FirebaseUser creator) {
        this.name = creator.getDisplayName() + "'s group";
        this.members = new ArrayList<>();
        this.members.add(creator.getEmail());
    }

    public Group(FirebaseUser creator, String name) {
        this(creator);
        this.name = name;
    }

    private Group(Parcel in) {
        name = in.readString();
        members = in.createStringArrayList();
    }

    public static final Creator<Group> CREATOR = new Creator<Group>() {
        @Override
        public Group createFromParcel(Parcel in) {
            return new Group(in);
        }

        @Override
        public Group[] newArray(int size) {
            return new Group[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeStringList(members);
    }
}
