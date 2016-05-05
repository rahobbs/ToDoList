package com.rahobbs.todo.helpers;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.rahobbs.todo.R;
import com.rahobbs.todo.fragments.TodoListFragment;
import com.rahobbs.todo.interfaces.ItemTouchHelperAdapter;

import java.util.Collections;
import java.util.List;

public class TodoListAdapter extends RecyclerView.Adapter<TodoHolder> implements ItemTouchHelperAdapter {
    private List<TodoItem> mTodoItems;
    Context mContext;
    View mView;
    Activity mActivity;
    TodoListFragment mFragment;

    public TodoListAdapter(List<TodoItem> todoItems, Context context, View v, Activity activity, TodoListFragment fragment) {
        mTodoItems = todoItems;
        this.mContext = context;
        this.mView = v;
        this.mActivity = activity;
        this.mFragment = fragment;
    }

    @Override
    public TodoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_todo, parent, false);
        return new TodoHolder(view, parent.getContext(), mActivity, mFragment);
    }

    @Override
    public void onBindViewHolder(TodoHolder holder, int position) {
        final TodoHolder mHolder = holder;
        TodoItem todoItem = mTodoItems.get(position);
        mHolder.bindTodo(todoItem);
        mHolder.updateCompleted();

        mHolder.mHandle.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                    mFragment.touchHelper.startDrag(mHolder);
                }
                return false;
            }
        });

    }

    @Override
    public int getItemCount() {
        return mTodoItems.size();
    }

    public void setTodoItems(List<TodoItem> todoItems) {
        mTodoItems = todoItems;
        notifyDataSetChanged();
    }

    @Override
    public void onItemDismiss(int position) {

        TodoItem item = mTodoItems.get(position);
        final TodoItem copyItem = item;
        TodoLab.get(mActivity).deleteTodoItem(item.getID());
        mFragment.updateUI();

        Snackbar.make(mView, "Item Deleted", Snackbar.LENGTH_LONG).setAction("Undo", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TodoLab.get(mActivity).addTodoItem(copyItem);
                copyItem.setPosition(copyItem.getPosition());
                mFragment.updateUI();
            }
        }).show();
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(mTodoItems, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(mTodoItems, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);

        for (TodoItem i : mTodoItems) {
            i.setPosition(mTodoItems.indexOf(i));
            TodoLab.get(mActivity).updateItem(i);
        }
        return true;
    }
}