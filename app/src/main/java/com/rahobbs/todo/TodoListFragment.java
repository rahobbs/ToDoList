package com.rahobbs.todo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.List;

/**
 * Created by rachael on 2/2/16.
 * Fragment that contains the list of to-do items
 */
public class TodoListFragment extends Fragment {

    private RecyclerView mTodoRecycleView;
    private TodoAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_todo_list, container, false);

        mTodoRecycleView = (RecyclerView) view.findViewById(R.id.todo_recycler_view);
        mTodoRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));

        updateUI();
        return view;

    }

    private void updateUI() {
        TodoLab todoLab = TodoLab.get(getActivity());
        List<TodoItem> todoItems = todoLab.getItems();

        if (mAdapter == null) {
            mAdapter = new TodoAdapter(todoItems);
            mTodoRecycleView.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    private class TodoHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView mTitleTextView;
        public TextView mDateTextView;
        public CheckBox mCompletedCheckBox;
        private TodoItem mTodo;

        public TodoHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            mTitleTextView = (TextView) itemView.findViewById(R.id.list_item_todo_title_text_view);
            mDateTextView = (TextView) itemView.findViewById(R.id.list_item_todo_date_text_view);
            mCompletedCheckBox = (CheckBox) itemView.findViewById(R.id.list_item_todo_completed_check_box);
        }

        public void bindTodo(TodoItem todo) {
            mTodo = todo;
            mTitleTextView.setText(mTodo.getTitle());
            mDateTextView.setText(mTodo.getDate().toString());
            mCompletedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    mTodo.setCompleted(isChecked);
                }
            });
            mCompletedCheckBox.setChecked(mTodo.isCompleted());
        }

        @Override
        public void onClick(View v) {
            Intent intent = TodoPagerActivity.newIntent(getActivity(), mTodo.getID());
            startActivity(intent);
        }
    }

    private class TodoAdapter extends RecyclerView.Adapter<TodoHolder> {
        private List<TodoItem> mTodoItems;

        public TodoAdapter(List<TodoItem> todoItems) {
            mTodoItems = todoItems;
        }

        @Override
        public TodoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.list_item_todo, parent, false);

            return new TodoHolder(view);
        }

        @Override
        public void onBindViewHolder(TodoHolder holder, int position) {
            TodoItem todoItem = mTodoItems.get(position);
            holder.bindTodo(todoItem);
        }

        @Override
        public int getItemCount() {
            return mTodoItems.size();
        }
    }


}
