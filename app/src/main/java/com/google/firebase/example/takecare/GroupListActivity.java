package com.google.firebase.example.takecare;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.google.firebase.example.takecare.dummy.DummyContent;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GroupListActivity extends AppCompatActivity
        implements GroupListFragment.OnListFragmentInteractionListener {

    private static final String TAG = "GroupListActivity";

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.fab_add_group)
    FloatingActionButton fabAddGroup;

    private GroupListFragment mGroupListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_list);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FragmentManager fragmentManager = getSupportFragmentManager();
        mGroupListFragment = (GroupListFragment) fragmentManager.findFragmentById(R.id.todays_tasks_fragment);
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
    }

    @Override
    public void onListFragmentInteraction(DummyContent.DummyItem item) {
        // TODO
    }
}
