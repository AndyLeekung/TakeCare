package com.google.firebase.example.takecare.store;

import com.google.android.gms.tasks.Task;
import com.google.firebase.example.takecare.model.User;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

public class TaskStore {

    public static Task<Void> saveTask(com.google.firebase.example.takecare.model.Task task,
                                      String email, String groupId) {
        // TODO subscribers

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        DocumentReference groupRef = firestore.collection("groups").document(groupId);
        DocumentReference userRef = firestore.collection("users").document(email);
        WriteBatch batch = firestore.batch();


        // Add task to group
        // Use the same ID for both the groups
        DocumentReference groupTaskRef = groupRef.collection("tasks").document();
        task.setGroupId(groupId);
        task.setTaskId(groupTaskRef.getId());

        batch.set(groupTaskRef, task);
        batch.set(userRef.collection("tasks").document(groupTaskRef.getId()), task);

        return batch.commit();
    }

    public static Task<Void> editTask(com.google.firebase.example.takecare.model.Task task,
                                      String email) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        WriteBatch batch = firestore.batch();

        String groupId = task.getGroupId();
        String taskId = task.getTaskId();


        DocumentReference userRef = firestore.collection("users")
                .document(email).collection("tasks").document(taskId);

        DocumentReference groupRef = firestore.collection("groups")
                .document(groupId).collection("tasks").document(taskId);

        batch.set(userRef, task);
        batch.set(groupRef, task);

        return batch.commit();
    }

    public static Task<Void> deleteTask(com.google.firebase.example.takecare.model.Task task,
                                      String email) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        WriteBatch batch = firestore.batch();

        String groupId = task.getGroupId();
        String taskId = task.getTaskId();

        DocumentReference userRef = firestore.collection("users")
                .document(email).collection("tasks").document(taskId);

        DocumentReference groupRef = firestore.collection("groups")
                .document(groupId).collection("tasks").document(taskId);

        batch.delete(userRef);
        batch.delete(groupRef);

        return batch.commit();
    }


}
