package com.google.firebase.example.takecare;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.example.takecare.model.Group;
import com.google.firebase.example.takecare.model.Task;
import com.google.firebase.example.takecare.store.GroupStore;
import com.google.firebase.example.takecare.store.TaskStore;
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
    public static final int CREATE_TASK_REQUEST_CODE = 8001;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.fab_add_task)
    FloatingActionButton mFabAddTask;

    @BindView(R.id.user_fragment_container)
    FrameLayout mUserListLayout;

    @BindView(R.id.group_tasks_container)
    FrameLayout mGroupTasksLayout;

    private FirebaseFirestore mFirestore;
    private DocumentReference mGroupRef;
    private ListenerRegistration mGroupRegistration;
    private Group mGroupInstance;
    private String mGroupId;

    private EditText mDialongInput;


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
        mGroupId = groupId;

        // Initialize Firestore
        mFirestore = FirebaseFirestore.getInstance();

        // Get reference to the restaurant
        mGroupRef = mFirestore.collection("groups").document(groupId);

        // put the user list in
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(mUserListLayout.getId(), UserListFragment.newInstance(groupId));
        ft.add(mGroupTasksLayout.getId(),
                TaskListFragment.newInstance(TaskListFragment.TaskListType.GroupTasks, groupId));
        ft.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_group, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                //handle the home button onClick event here.
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.menu_add_member:
                Log.d(TAG, "Add member");
                onAddMember();
                return true;
            case R.id.menu_change_group_name:
                onChangeName();
                return true;
            case R.id.delete_group:
                onDeleteGroup();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CREATE_TASK_REQUEST_CODE:

                break;
            default:
        }
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
        Log.d(TAG, "Add task button clicked");
        Intent intent = new Intent(this, CreateTaskActivity.class);
        // need to pass along the group ID for task creation
        intent.putExtra(CreateTaskActivity.GROUP_ID_KEY, mGroupId);
        intent.putExtra(CreateTaskActivity.TASK_KEY, task);
        startActivityForResult(intent, CREATE_TASK_REQUEST_CODE);
    }

    @Override
    public void onTaskCheckBoxChange(com.google.firebase.example.takecare.model.Task task, boolean checked) {
        // TODO
        Log.d(TAG, "Task checkbox: " + checked);
        task.setComplete(checked);
        TaskStore.editTask(task).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "Task edited");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Task failed to edit");
            }
        });
    }

    @Override
    public void onTaskDeleteClicked(com.google.firebase.example.takecare.model.Task task) {
        // TODO
        Log.d(TAG, "Task delete click");
        TaskStore.deleteTask(task).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "Task deleted");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Task failed to delete");
            }
        });
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
        mGroupInstance = group;
        mToolbar.setTitle(group.getName());
    }

    @OnClick(R.id.fab_add_task)
    public void onAddTask() {
        Log.d(TAG, "Add task button clicked");
        Intent intent = new Intent(this, CreateTaskActivity.class);
        // need to pass along the group ID for task creation
        intent.putExtra(CreateTaskActivity.GROUP_ID_KEY, mGroupId);
        startActivityForResult(intent, CREATE_TASK_REQUEST_CODE);
    }

    private void onAddMember() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.add_member);

        // Set up add member input
        mDialongInput = new EditText(this);
        mDialongInput.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
//        mDialongInput.setPadding(16, 0, 16, 0);

        builder.setView(mDialongInput);

        builder.setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String email = mDialongInput.getText().toString();
                mGroupInstance.getMembers().add(email);
                GroupStore.editGroup(mGroupInstance, mGroupId).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Group member added");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Group member failed to add");
                    }
                });
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        builder.show();
    }

    private void onChangeName() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.change_group_name);

        // Set up add member input
        mDialongInput = new EditText(this);
        mDialongInput.setInputType(InputType.TYPE_TEXT_VARIATION_SHORT_MESSAGE);

        builder.setView(mDialongInput);

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String name = mDialongInput.getText().toString();
                mGroupInstance.setName(name);
                GroupStore.editGroup(mGroupInstance, mGroupId).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Group name changed");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Group name failed to change");
                    }
                });
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        builder.show();
    }

    private void onDeleteGroup() {
        // TODO tasks for individuals need to be deleted

        GroupStore.deleteGroup(mGroupId).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "Group deleted");
                NavUtils.navigateUpFromSameTask(GroupDetailActivity.this);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Group failed to delete");
            }
        });
    }

}
