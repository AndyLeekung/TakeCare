package com.google.firebase.example.takecare;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.google.firebase.example.takecare.model.Group;
import com.google.firebase.example.takecare.model.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GroupDetailActivity extends AppCompatActivity
        implements EventListener<DocumentSnapshot>,
        UserListFragment.OnUserSelectedListener,
        TaskListFragment.OnTaskSelectedListener {

    private static final String TAG = "GroupDetailActivity";

    public static final String GROUP_ID_KEY = "group_id_key";

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.fab_add_task)
    FloatingActionButton mFabAddTask;

    @BindView(R.id.user_fragment_container)
    FrameLayout userListLayout;

    private FirebaseFirestore mFirestore;
    private DocumentReference mGroupRef;
    private ListenerRegistration mGroupRegistration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_detail);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get Group ID from extras
        String groupId = getIntent().getExtras().getString(GROUP_ID_KEY);
        if (groupId == null) {
            throw new IllegalArgumentException("Must pass extra " + GROUP_ID_KEY);
        }

        // Initialize Firestore
        mFirestore = FirebaseFirestore.getInstance();

        // Get reference to the restaurant
        mGroupRef = mFirestore.collection("groups").document(groupId);

        // put the user list in
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(userListLayout.getId(), UserListFragment.newInstance(groupId));
        ft.commit();
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

    @Override
    public void onStart() {
        super.onStart();

        mGroupRegistration = mGroupRef.addSnapshotListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();

        if (mGroupRegistration != null) {
            mGroupRegistration.remove();
            mGroupRegistration = null;
        }
    }

    @Override
    public void onUserSelected(String email) {
        // TODO do something when user is selected
        Log.d(TAG, email + " selected");
    }

    @Override
    public void onTaskClicked(Task task) {
        // TODO do something when task is clicked
        Log.d(TAG, "task selected");
    }

    /**
     * Listener for the Group document ({@link #mGroupRef}).
     */
    @Override
    public void onEvent(DocumentSnapshot snapshot, FirebaseFirestoreException e) {
        if (e != null) {
            Log.w(TAG, "restaurant:onEvent", e);
            return;
        }

        onGroupLoaded(snapshot.toObject(Group.class));
    }

    private void onGroupLoaded(Group group) {
        mToolbar.setTitle(group.getName());
    }

    @OnClick(R.id.fab_add_task)
    public void onAddTask() {
        // TODO Add task
        Log.d(TAG, "Add task button clicked");
    }

}
