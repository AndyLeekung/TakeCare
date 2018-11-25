package com.google.firebase.example.takecare;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.example.takecare.model.Group;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.google.firebase.example.takecare.store.GroupStore.addGroup;

public class GroupListViewFragment extends Fragment {

    private static final String TAG = "GroupListViewFragment";

    @BindView(R.id.fab_add_group)
    FloatingActionButton mFabAddGroup;

    private FirebaseFirestore mFirestore;
    private CollectionReference mGroupColRef;

    public GroupListViewFragment() {}

    public static GroupListViewFragment newInstance() {
        GroupListViewFragment fragment = new GroupListViewFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group_list_view, container, false);

        ButterKnife.bind(this, view);
        // Firestore
        mFirestore = FirebaseFirestore.getInstance();

        // Get reference to the restaurant
        mGroupColRef = mFirestore.collection("groups");

        return view;
    }

    @OnClick(R.id.fab_add_group)
    public void onAddGroupClick() {
        Log.d(TAG, "fab add group click");
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            Group newGroup = new Group(currentUser);
            addGroup(newGroup)
                    .addOnSuccessListener(getActivity(), new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "Group added");
                        }
                    })
                    .addOnFailureListener(getActivity(), new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Add group failed", e);
                        }
                    });
        }
    }

//    @Override
//    public void onGroupSelected(DocumentSnapshot group) {
//        // TODO go to group activity
//        String groupId = group.getId();
//        Log.d(TAG, "Group ID: " + groupId + " clicked");
//        Intent intent = new Intent(getContext(), GroupDetailActivity.class);
//        intent.putExtra(GroupDetailActivity.GROUP_ID_KEY, groupId);
//
//        startActivity(intent);
//    }
}
