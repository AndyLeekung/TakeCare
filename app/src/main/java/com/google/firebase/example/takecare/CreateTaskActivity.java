package com.google.firebase.example.takecare;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.example.takecare.model.Group;
import com.google.firebase.example.takecare.model.Task;
import com.google.firebase.example.takecare.model.User;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.type.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CreateTaskActivity extends AppCompatActivity {

    @BindView(R.id.fab_create_task)
    FloatingActionButton mFabCreateTask;

    @BindView(R.id.edit_name)
    EditText mEditName;

    @BindView(R.id.assignee_spinner)
    Spinner mAssigneeSpinner;

    @BindView(R.id.edit_date)
    EditText mEditDate;

    @BindView(R.id.edit_time)
    EditText mEditTime;

    public static final String GROUP_PARCEL_KEY = "create_task_group_parcel";
    public static final String USER_PARCEL_KEY = "create_task_user_parcel";
    private FirebaseFirestore mFirestore;
    private FirebaseAuth mFirebaseAuth;
    private Group mGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_create_task);
        //fab.setOnClickListener(new View.OnClickListener() {
        //   @Override
        //    public void onClick(View view) {
        //        //Snackbar.make(view, "Create New Task", Snackbar.LENGTH_LONG)
        //        //        .setAction("Action", null).show();
        //    }
        //});
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mFirestore = FirebaseFirestore.getInstance();

        // Get Group ID from extras
        mGroup = (Group) getIntent().getParcelableExtra(GROUP_PARCEL_KEY);
        if (mGroup == null) {
            throw new IllegalArgumentException("Must pass extra " + GROUP_PARCEL_KEY);
        }

        ArrayAdapter<String> adp = new ArrayAdapter<String>(this,
            android.R.layout.simple_spinner_item,
            mGroup.getMembers());
        adp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mAssigneeSpinner.setAdapter(adp);
    }

    @OnClick(R.id.fab_create_task)
    public void onCreateTask(View view) {
        String name = mEditName.getText().toString();
        String date = mEditDate.getText().toString();
        String time = mEditTime.getText().toString();
        String owner = mAssigneeSpinner.getSelectedItem().toString();
        if (name.equals("") || date.equals("") || time.equals("") || owner.equals("")) {
            Snackbar.make(view, "Error: please enter valid input", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        } else {
            Log.d("CreateTaskActivity", "Creating new task");
            Task task = new Task();
            task.setText(name);
            task.setCreator(new User(mFirebaseAuth.getCurrentUser()));
            task.setDueDate(null);
            task.setTimeDue(new Time(time));
            //User fbOwner = new User(mFirestore.collection("users").document(owner));
            //task.setOwner();
        }
    }
}
