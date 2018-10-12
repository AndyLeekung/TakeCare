package com.google.firebase.example.takecare.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.example.takecare.GroupListFragment;
import com.google.firebase.example.takecare.R;
import com.google.firebase.example.takecare.dummy.DummyContent.DummyItem;
import com.google.firebase.example.takecare.model.Group;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link GroupListFragment.OnGroupSelectedListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class GroupAdapter extends FirestoreAdapter<GroupAdapter.ViewHolder> {

    private final GroupListFragment.OnGroupSelectedListener mListener;

    public GroupAdapter(Query query, GroupListFragment.OnGroupSelectedListener listener) {
        super(query);
        mListener = listener;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_group_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.bind(getSnapshot(position), mListener);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.content)
        TextView groupView;


        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(final DocumentSnapshot snapshot,
                         final GroupListFragment.OnGroupSelectedListener listener) {
            Group group = snapshot.toObject(Group.class);
            groupView.setText(group.getName());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onGroupSelected(snapshot);
                    }
                }
            });
        }
    }
}
