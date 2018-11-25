package com.google.firebase.example.takecare;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserTasksFragment extends Fragment {
    private static final String ARG_EMAIL = "email_arg";
    private String mEmail;

    @BindView(R.id.task_container)
    FrameLayout mFragmentContainer;

    public UserTasksFragment() {
        // Required empty public constructor
    }

    public static UserTasksFragment newInstance(String email) {
        UserTasksFragment fragment = new UserTasksFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EMAIL, email);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mEmail = getArguments().getString(ARG_EMAIL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_tasks, container, false);
        ButterKnife.bind(this, view);

        TaskListFragment fragment = TaskListFragment
                .newInstance(TaskListFragment.TaskListType.UserTasks, mEmail);

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(mFragmentContainer.getId(),
                fragment).commit();
        return view;
    }
}
