package com.google.firebase.example.takecare;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.example.takecare.model.Group;
import com.google.firebase.example.takecare.model.Task;
import com.google.firebase.example.takecare.model.User;
import com.google.firebase.example.takecare.store.TaskStore;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CreateTaskActivity extends AppCompatActivity
        implements EventListener<DocumentSnapshot> {

    private static final String TAG = "CreateTaskActivity";

    @BindView(R.id.fab_create_task)
    FloatingActionButton mFabCreateTask;

    @BindView(R.id.edit_name)
    EditText mEditName;

    @BindView(R.id.assignee_spinner)
    Spinner mAssigneeSpinner;

    @BindView(R.id.edit_date)
    EditText mEditDeadline;

    private DatePickerDialog mDatePicker;
    private TimePickerDialog mTimePicker;
    private SimpleDateFormat mDateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm a", Locale.US);
    private Date mDate;

    public static final String GROUP_PARCEL_KEY = "create_task_group_parcel";
    public static final String GROUP_ID_KEY = "group_id_key";
    public static final String TASK_KEY = "task_key";
    private FirebaseFirestore mFirestore;
    private FirebaseAuth mFirebaseAuth;
    private Group mGroup;
    private String mGroupId;
    private Task mTask;

    private DocumentReference mGroupRef;
    private ListenerRegistration mGroupRegistration;

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
        mFirebaseAuth = FirebaseAuth.getInstance();

        // Get Group ID from extras
        mGroupId = getIntent().getExtras().getString(GROUP_ID_KEY);
        mTask = getIntent().getExtras().getParcelable(TASK_KEY);

        mEditDeadline.setInputType(InputType.TYPE_NULL);

        mDate = new Date();

        if (mTask != null) {
            this.setTitle(R.string.title_activity_edit_task);
            toolbar.setTitle(R.string.title_activity_edit_task);
            mEditName.setText(mTask.getText());
            if (mTask.getDeadline() != null) {
                mEditDeadline.setText(mDateFormat.format(mTask.getDeadline().toDate()));
            }
            mGroupId = mTask.getGroupId();
        }

        if (mGroupId != null) {
            mGroupRef = mFirestore.collection("groups").document(mGroupId);
        }

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

        if (mGroupRef != null) {
            mGroupRegistration = mGroupRef.addSnapshotListener(this);
        }
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
    public void onEvent(DocumentSnapshot snapshot, FirebaseFirestoreException e) {
        if (e != null) {
            Log.w(TAG, "restaurant:onEvent", e);
            return;
        }

        onGroupLoaded(snapshot.toObject(Group.class));
    }

    private void onGroupLoaded(Group group) {
        mGroup = group;
        ArrayAdapter<String> adp = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item,
                mGroup.getMembers());
        adp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mAssigneeSpinner.setAdapter(adp);
    }

    @OnClick(R.id.edit_date)
    public void onEditDate() {
        final Calendar cldr = Calendar.getInstance();

        int day = cldr.get(Calendar.DAY_OF_MONTH);
        int month = cldr.get(Calendar.MONTH);
        int year = cldr.get(Calendar.YEAR);
//        Date curDate = cldr.getTime();
        mDatePicker = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                mDate = new Date(year - 1900, month, day);
                // have to show year - 1900 because Date class is relative to 1900
                mEditDeadline.setText(mDateFormat.format(mDate));
                onEditTime();
            }
        }, year, month, day);
        mDatePicker.show();
    }

//    @OnClick(R.id.edit_time)
    public void onEditTime() {
        Date curTime = Calendar.getInstance().getTime();

        mTimePicker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                mDate.setHours(hour);
                mDate.setMinutes(minute);
                mEditDeadline.setText(mDateFormat.format(mDate));

            }
        }, curTime.getHours(), curTime.getMinutes(), false);
        mTimePicker.show();
    }

    @OnClick(R.id.fab_create_task)
    public void onCreateTask(final View view) {
        String name = mEditName.getText().toString();
        String date = mEditDeadline.getText().toString();
        String owner = mAssigneeSpinner.getSelectedItem().toString();
        if (name.equals("") || owner.equals("")) {
            Snackbar.make(view, "Error: please enter valid input", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        } else {
            Log.d("CreateTaskActivity", "Creating new task");
            User curUser = new User(mFirebaseAuth.getCurrentUser());
            boolean shouldEditTask = true;
            if (mTask == null) {
                mTask = new Task();
                shouldEditTask = false;
            }
            mTask.setText(name);
            mTask.setCreator(curUser.getEmail());
            mTask.setOwner(owner);
            Timestamp deadline = new Timestamp(mDate);
            mTask.setDeadline(deadline);

            if (shouldEditTask) {
                TaskStore.editTask(mTask).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Task saved");
                        NavUtils.navigateUpFromSameTask(CreateTaskActivity.this);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Task failed to save");
                        Snackbar.make(view, "Error saving task to server", Snackbar.LENGTH_LONG)
                                .setAction("Error", null).show();
                    }
                });
            } else {
                TaskStore.saveTask(mTask, owner, mGroupId).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Task saved");
                        NavUtils.navigateUpFromSameTask(CreateTaskActivity.this);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Task failed to save");
                        Snackbar.make(view, "Error saving task to server", Snackbar.LENGTH_LONG)
                                .setAction("Error", null).show();
                    }
                });
            }

        }
    }
}
