package com.rahobbs.todo.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rahobbs.todo.R;
import com.rahobbs.todo.activities.TodoPagerActivity;
import com.rahobbs.todo.fragments.TodoListFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Custom ViewHolder
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
    private Context mContext;
    private Activity mActivity;
    private TodoListFragment mFragment;

    public TodoHolder(final View itemView, Context context, Activity activity, final TodoListFragment fragment) {
        super(itemView);
        itemView.setOnClickListener(this);
        this.mContext = context;
        this.mActivity = activity;
        this.mFragment = fragment;

        mTitleTextView = (TextView) itemView.findViewById(R.id.list_item_todo_title_text_view);
        mDateTextView = (TextView) itemView.findViewById(R.id.list_item_todo_date_text_view);
        mCompletedCheckBox = (CheckBox) itemView.findViewById(R.id.list_item_todo_completed_check_box);
        mDateLabel = (TextView) itemView.findViewById(R.id.due_date_label);
        mListItem = (RelativeLayout) itemView.findViewById(R.id.list_item_todo);
        mHandle = (ImageView) itemView.findViewById(R.id.reorder_handle);
        mPosition = getAdapterPosition();

        mListItem.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View arg0) {
                mFragment.mSelectedItems.add(mTodo);
                mFragment.setmMultiSelectMode(true);
                mActionMode = mActivity.startActionMode(mActionModeCallback);
                mListItem.setBackgroundColor(ContextCompat.getColor(mContext, R.color.dark_grey));
                return true;    // <- set to true
            }
        });
    }

    /*
    * Bind task data to the custom ViewHolder
    */
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
            mListItem.setBackgroundColor(ContextCompat.getColor(mContext, R.color.default_background_light));
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
                TodoLab.get(mActivity).updateItem(mTodo);
            }
        });
    }

    /*
    * Update checkboxes to reflect whether the task has been completed.
    */
    public void updateCompleted() {
        if (mTodo.isCompleted()) {
            checkItem();
        }
    }

    @Override
    public void onClick(View v) {
        if (!mFragment.mMultiSelectMode) {
            Intent intent = TodoPagerActivity.newIntent(mActivity, mTodo.getID());
            mActivity.startActivityForResult(intent, 1);
        } else {
            if (mFragment.mSelectedItems.contains(mTodo)) {
                mFragment.mSelectedItems.remove(mTodo);
                mListItem.setBackgroundColor(ContextCompat.getColor(mContext, (R.color.default_background_light)));
                if (mFragment.mSelectedItems.isEmpty() && mActionMode != null) {
                    mActionMode.finish();
                }
            } else {
                mFragment.mSelectedItems.add(mTodo);
                mListItem.setBackgroundColor(ContextCompat.getColor(mContext, (R.color.dark_grey)));
            }
        }
    }

    /*
    * Update checkbox UI and make sure the item's isCompleted() is set correctly
    */
    private void checkItem() {
        mTitleTextView.setTextColor(ContextCompat.getColor(mContext, R.color.light_grey));
        mDateTextView.setPaintFlags(mDateTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        mDateLabel.setPaintFlags(mDateTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        mTitleTextView.setTextColor(ContextCompat.getColor(mContext, R.color.light_grey));

        if (!mTodo.isCompleted()) {
            mTodo.setCompleted(true);
        }
    }

    /*
    * Update checkbox UI and make sure the item's isCompleted() is set correctly
    */
    private void unCheckItem() {
        mTitleTextView.setTextColor(ContextCompat.getColor(mContext, R.color.darkFont));
        mDateTextView.setPaintFlags(mDateTextView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        mDateLabel.setPaintFlags(mDateTextView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));

        if (mTodo.isCompleted()) {
            mTodo.setCompleted(false);
        }
    }

    /*
    * Custom ActionMode.Callback to create Context Action Menu for selecting and modifying multiple
    * items
    */
    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            if (mFragment.mType.equals("task_list")) {
                inflater.inflate(R.menu.context_menu, menu);
            } else {
                inflater.inflate(R.menu.archive_context_menu, menu);
            }
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.unarchive:
                    mFragment.unArchiveSelected(mFragment.mSelectedItems);
                    mFragment.updateUI();
                    mode.finish();
                    return true;
                case R.id.archive:
                    mFragment.archiveSelected(mFragment.mSelectedItems);
                    mFragment.updateUI();
                    mode.finish();
                    return true;
                case R.id.multi_delete:
                    final List<TodoItem> copySelected = mFragment.mSelectedItems;
                    Log.v("Selected Items: ", mFragment.mSelectedItems.toString());
                    mFragment.deleteSelected(mFragment.mSelectedItems);
                    mFragment.updateUI();

                    Snackbar.make(itemView, "Deleting " + mFragment.mSelectedItems.size() + " items",
                            Snackbar.LENGTH_LONG).setAction("Undo", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            for (TodoItem i : copySelected) {
                                TodoLab.get(mActivity).addTodoItem(i);
                                i.setPosition(i.getPosition());
                            }
                            mFragment.mSelectedItems.clear();
                            mFragment.updateUI();
                        }
                    }).show();
                    mFragment.updateUI();
                    mode.finish();
                    return true;
                case R.id.close_menu:
                    mFragment.updateUI();
                    mode.finish();
                    mFragment.mSelectedItems.clear();
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mFragment.setmMultiSelectMode(false);
            mFragment.updateUI();
        }
    };
}