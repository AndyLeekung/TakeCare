package com.google.firebase.example.takecare;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.example.takecare.adapter.UserAdapter;
import com.google.firebase.example.takecare.model.Group;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnUserSelectedListener}
 * interface.
 */
public class UserListFragment extends Fragment
        implements EventListener<DocumentSnapshot> {

    private static final String TAG = "UserListFragment";

    private static final String ARG_GROUP_ID = "group_id";
    private OnUserSelectedListener mListener;

    private FirebaseFirestore mFirestore;
    private UserAdapter mUserAdapter;
    private String mGroupId;
    private DocumentReference mGroupRef;
    private ListenerRegistration mGroupRegistration;

    @BindView(R.id.user_list)
    RecyclerView mUserRecycler;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public UserListFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static UserListFragment newInstance(String groupId) {
        UserListFragment fragment = new UserListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_GROUP_ID, groupId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mGroupId = getArguments().getString(ARG_GROUP_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_list, container, false);
        ButterKnife.bind(this, view);

        mFirestore = FirebaseFirestore.getInstance();

        // TODO query emails
        mGroupRef = mFirestore.collection("groups").document(mGroupId);

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnUserSelectedListener) {
            mListener = (OnUserSelectedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnUserSelectedListener");
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

    /**
     * Listener for the Group document ({@link #mGroupRef}).
     */
    @Override
    public void onEvent(DocumentSnapshot snapshot, FirebaseFirestoreException e) {
        if (e != null) {
            Log.w(TAG, "user:onEvent", e);
            return;
        }

        onGroupLoaded(snapshot.toObject(Group.class));
    }

    private void onGroupLoaded(Group group) {
        List<String> emails = group.getMembers();

        mUserAdapter = new UserAdapter(emails, mListener);
        mUserRecycler.setAdapter(mUserAdapter);
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
    public interface OnUserSelectedListener {
        void onUserSelected(String email);
    }
}
