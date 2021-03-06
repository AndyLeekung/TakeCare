package com.google.firebase.example.takecare.adapter;

import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.example.takecare.R;
import com.google.firebase.example.takecare.TaskListFragment;
import com.google.firebase.example.takecare.TaskListFragment.OnTaskSelectedListener;
import com.google.firebase.example.takecare.dummy.DummyContent.DummyItem;
import com.google.firebase.example.takecare.model.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Task} and makes a call to the
 * specified {@link OnTaskSelectedListener}.
 */

public class TaskAdapter extends FirestoreAdapter<TaskAdapter.ViewHolder> {

    private final static String TAG = "TaskAdapter";

    private final OnTaskSelectedListener mListener;

    public TaskAdapter(Query query, OnTaskSelectedListener listener) {
        super(query);
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_task_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
       holder.bind(getSnapshot(position), mListener);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.task_checkbox)
        CheckBox mCheckbox;

        @BindView(R.id.content_text)
        TextView mTaskView;

        @BindView(R.id.deadline_text)
        TextView mDeadlineView;

        @BindView(R.id.btn_delete_task)
        ImageButton mBtnDelete;

        Task mTask;
        String mTaskId;
        DocumentSnapshot mSnapshot;

        OnTaskSelectedListener mListener;

        private SimpleDateFormat mDateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm a", Locale.US);


        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(final DocumentSnapshot snapshot,
                         final TaskListFragment.OnTaskSelectedListener listener) {
            final Task task = snapshot.toObject(Task.class);
            mTaskId = snapshot.getId();
            mTask = task;
            mSnapshot = snapshot;

            mTaskView.setText(task.getText());
            if (task.getDeadline() != null) {
                mDeadlineView.setVisibility(View.VISIBLE);
                mDeadlineView.setText(mDateFormat.format(task.getDeadline().toDate()));
            } else {
                mDeadlineView.setVisibility(View.INVISIBLE);
            }

            mListener = listener;
            mCheckbox.setChecked(task.isComplete());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onTaskClicked(task);
                    }
                }
            });
        }

        @OnClick(R.id.content_text)
        void onTaskClicked() {
            if (mListener != null) {
                mListener.onTaskClicked(mTask);
            }
        }

        @OnCheckedChanged(R.id.task_checkbox)
        void onCheckboxChanged(CheckBox checkbox, boolean checked) {
            Log.d(TAG, "Checkbox: " + checked);
            if (checked) {
                mTaskView.setPaintFlags(mTaskView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                mTaskView.setPaintFlags(mTaskView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            }

            if (mListener != null) {
                mListener.onTaskCheckBoxChange(mTask, checked);
            }
        }

        @OnClick(R.id.btn_delete_task)
        void onDeleteClicked() {
            if (mListener != null) {
                mListener.onTaskDeleteClicked(mTask);
            }
            notifyDataSetChanged();
        }
    }
}
