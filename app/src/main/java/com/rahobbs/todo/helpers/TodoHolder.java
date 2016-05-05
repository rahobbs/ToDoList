package com.rahobbs.todo.helpers;

import android.content.Intent;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.ActionMode;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rahobbs.todo.R;
import com.rahobbs.todo.activities.TodoPagerActivity;
import com.rahobbs.todo.interfaces.ActionModeCallback;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Class to define custom ViewHolder
 */
public class TodoHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView mTitleTextView;
    public TextView mDateLabel;
    public TextView mDateTextView;
    public CheckBox mCompletedCheckBox;
    public RelativeLayout mListItem;
    public ImageView mHandle;
    public int mPosition;
    public ActionMode mActionMode;
    private TodoItem mTodo;
    private List<TodoItem> mSelectedItems = new ArrayList<>();
    private Boolean mMultiSelectMode = false;

    public TodoHolder(final View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);

        mTitleTextView = (TextView) itemView.findViewById(R.id.list_item_todo_title_text_view);
        mDateTextView = (TextView) itemView.findViewById(R.id.list_item_todo_date_text_view);
        mCompletedCheckBox = (CheckBox) itemView.findViewById(R.id.list_item_todo_completed_check_box);
        mDateLabel = (TextView) itemView.findViewById(R.id.due_date_label);
        mListItem = (RelativeLayout) itemView.findViewById(R.id.list_item_todo);
        mHandle = (ImageView) itemView.findViewById(R.id.reorder_handle);
        mPosition = getAdapterPosition();

        mListItem.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View arg0) {
                ActionModeCallback mActionModeCallback = new ActionModeCallback();
                if(!mMultiSelectMode){
                    mSelectedItems.clear();
                }
                mSelectedItems.add(mTodo);
                mMultiSelectMode = true;
                mActionMode = getActivity().startActionMode(mActionModeCallback);
                mListItem.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.done_task));

                return true;
            }
        });
    }

    /*
    * Binds TodoItem data to the custom view holder so that inflated views (TextViews, CheckBoxes, etc,
    * show the data for that TodoItem.
    * */
    public void bindTodo(TodoItem todo) {
        SimpleDateFormat format = new SimpleDateFormat("E dd MMM yyyy", Locale.ENGLISH);
        mTodo = todo;

        if (mTodo.getTitle() != null) {
            String title = mTodo.getTitle();
            if (title.contains("\n")) {
                title = mTodo.getTitle().replaceFirst("\n", ": ");
                title = title.replace("\n", "");
            }
            mTitleTextView.setText(title.trim());
            mListItem.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.default_background_light));
        }

        mDateTextView.setText(format.format(mTodo.getDate()));

        mCompletedCheckBox.setChecked(mTodo.isCompleted());
        mCompletedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mTodo.setCompleted(isChecked);

                if (isChecked) {
                    checkItem();
                } else {
                    unCheckItem();
                }
                TodoLab.get(getActivity()).updateItem(mTodo);
            }
        });
    }

    /*
    * Changes to state of the checkbox depending on whether the task has been completed.
    */
    public void updateCompleted() {
        if (mTodo.isCompleted()) {
            checkItem();
        }
    }

    @Override
    public void onClick(View v) {
        if (!mMultiSelectMode) {
            Intent intent = TodoPagerActivity.newIntent(getActivity(), mTodo.getID());
            startActivityForResult(intent, 1);
        }
        if (mMultiSelectMode) {
            if (mSelectedItems.contains(mTodo)) {
                mSelectedItems.remove(mTodo);
                mListItem.setBackgroundColor(ContextCompat.getColor(getContext(), (R.color.default_background_light)));
                if (mSelectedItems.isEmpty() && mActionMode != null) {
                    mActionMode.finish();
                }
            } else {
                mSelectedItems.add(mTodo);
                mListItem.setBackgroundColor(ContextCompat.getColor(getContext(), (R.color.done_task)));
            }
        }
    }

    private void checkItem() {
        mTitleTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.inactiveText));
        mDateTextView.setPaintFlags(mDateTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        mDateLabel.setPaintFlags(mDateTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        mTitleTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.inactiveText));

        if (!mTodo.isCompleted()) {
            mTodo.setCompleted(true);
        }
    }

    private void unCheckItem() {
        mTitleTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.darkFont));
        mDateTextView.setPaintFlags(mDateTextView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        mDateLabel.setPaintFlags(mDateTextView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));

        if (mTodo.isCompleted()) {
            mTodo.setCompleted(false);
        }
    }
}