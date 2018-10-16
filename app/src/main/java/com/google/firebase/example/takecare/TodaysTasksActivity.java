package com.google.firebase.example.takecare;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.example.takecare.dummy.DummyContent;
import com.google.firebase.example.takecare.model.Task;
import com.google.firebase.example.takecare.model.User;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TodaysTasksActivity extends AppCompatActivity
        implements TaskListFragment.OnTaskSelectedListener {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.todays_tasks_container)
    FrameLayout mTodaysTaskLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todays_tasks);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String curUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.add(mTodaysTaskLayout.getId(), TaskListFragment.newInstance(
                TaskListFragment.TaskListType.UserTasks, curUserEmail));
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
    public void onTaskClicked(Task item) {
        // TODO
    }
}
