package com.google.firebase.example.takecare.store;

import com.google.android.gms.tasks.Task;
import com.google.firebase.example.takecare.model.User;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

public class TaskStore {

    public static Task<Void> saveTask(com.google.firebase.example.takecare.model.Task task,
                                      User user, String groupId) {
        // TODO subscribers

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        DocumentReference groupRef = firestore.collection("groups").document(groupId);
        DocumentReference userRef = firestore.collection("users").document(user.getEmail());
        WriteBatch batch = firestore.batch();

        // Add task to group
        batch.set(groupRef.collection("tasks").document(), task);
        batch.set(userRef.collection("tasks").document(), task);

        return batch.commit();
    }
}
