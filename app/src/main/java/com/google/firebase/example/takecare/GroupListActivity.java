package com.google.firebase.example.takecare;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.example.takecare.dummy.DummyContent;
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

public class GroupListActivity extends AppCompatActivity
        implements GroupListFragment.OnGroupSelectedListener {

    private static final String TAG = "GroupListActivity";

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.fab_add_group)
    FloatingActionButton fabAddGroup;

    private GroupListFragment mGroupListFragment;
    private FirebaseFirestore mFirestore;
    private CollectionReference mGroupColRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_list);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FragmentManager fragmentManager = getSupportFragmentManager();
        mGroupListFragment = (GroupListFragment) fragmentManager.findFragmentById(R.id.todays_tasks_fragment);

        // Firestore
        mFirestore = FirebaseFirestore.getInstance();

        // Get reference to the restaurant
        mGroupColRef = mFirestore.collection("groups");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                //handle the home button onClick event here.
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.fab_add_group)
    public void onAddGroupClick() {
        Log.d(TAG, "fab add group click");
        // TODO add Group
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            Group newGroup = new Group(currentUser);
            addGroup(mGroupColRef, newGroup)
                    .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "Group added");
                        }
                    })
                    .addOnFailureListener(this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Add group failed", e);
                        }
                    });
        }
    }

    private Task<Void> addGroup(final CollectionReference groupColRef, final Group group) {
        // Create reference for new Group, for use inside the transaction
        final DocumentReference groupRef = groupColRef.document();

        return mFirestore.runTransaction(new Transaction.Function<Void>() {
            @Nullable
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                transaction.set(groupRef, group);
                return null;
            }
        });
    }

    @Override
    public void onGroupSelected(DocumentSnapshot group) {
        // TODO go to group activity
        String groupId = group.getId();
        Log.d(TAG, "Group ID: " + groupId + " clicked");
    }
}
