package com.google.firebase.example.takecare;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.example.takecare.adapter.TaskAdapter;
import com.google.firebase.example.takecare.model.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnTaskSelectedListener}
 * interface.
 */
public class TaskListFragment extends Fragment {

    public enum TaskListType {
        GroupTasks, UserTasks
    }

    // TODO: Customize parameter argument names
    private static final String ARG_TASK_TYPE = "task_type";
    private static final String ARG__ID = "id_for_task";
    // TODO: Customize parameters
    private TaskListType mTaskListType;
    private String mDocId;
    private OnTaskSelectedListener mListener;

    private FirebaseFirestore mFirestore;
    private TaskAdapter mTaskAdapter;

    @BindView(R.id.task_list)
    RecyclerView mTaskRecycler;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public TaskListFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static TaskListFragment newInstance(TaskListType type, String docId) {
        TaskListFragment fragment = new TaskListFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_TASK_TYPE, type);
        args.putString(ARG__ID, docId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mTaskListType = (TaskListType) getArguments().getSerializable(ARG_TASK_TYPE);
            mDocId = getArguments().getString(ARG__ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task_list, container, false);
        ButterKnife.bind(this, view);

        // Initialize Firestore
        mFirestore = FirebaseFirestore.getInstance();
        DocumentReference docRef;
        switch (mTaskListType) {
            case UserTasks:
                docRef = mFirestore.collection("users").document(mDocId);
                break;
            case GroupTasks:
                docRef = mFirestore.collection("groups").document(mDocId);
                break;
            default:
                docRef = mFirestore.collection("groups").document(mDocId);
        }

        Query taskQuery = docRef.collection("tasks").orderBy("deadline");

        // Set the adapter
        mTaskAdapter = new TaskAdapter(taskQuery, mListener);
        mTaskRecycler.setAdapter(mTaskAdapter);

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnTaskSelectedListener) {
            mListener = (OnTaskSelectedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnTaskSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onStart() {
        super.onStart();

        mTaskAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();

        mTaskAdapter.stopListening();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnTaskSelectedListener {
        // TODO: Update argument type and name
        void onTaskClicked(Task item);
    }
}
