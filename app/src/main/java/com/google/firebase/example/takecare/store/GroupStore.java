package com.google.firebase.example.takecare.store;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.tasks.Task;
import com.google.firebase.example.takecare.model.Group;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

public class GroupStore {

    public static Task<Void> addGroup(final Group group) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        CollectionReference groupColRef = firestore.collection("groups");

        final DocumentReference groupRef = groupColRef.document();

        return firestore.runTransaction(new Transaction.Function<Void>() {
            @Nullable
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                transaction.set(groupRef, group);
                return null;
            }
        });
    }

    public static Task<Void> editGroup(final Group group, final String groupId) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        CollectionReference groupColRef = firestore.collection("groups");


        final DocumentReference groupRef = groupColRef.document(groupId);

        return firestore.runTransaction(new Transaction.Function<Void>() {
            @Nullable
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                transaction.set(groupRef, group);
                return null;
            }
        });
    }

    public static Task<Void> deleteGroup(final String groupId) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        CollectionReference groupColRef = firestore.collection("groups");


        final DocumentReference groupRef = groupColRef.document(groupId);

        return groupRef.delete();
    }
}
