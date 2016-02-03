package com.rahobbs.todo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

        mAdapter = new TodoAdapter(todoItems);
        mTodoRecycleView.setAdapter(mAdapter);
    }

    private class TodoHolder extends RecyclerView.ViewHolder {
        public TextView mTitleTextView;

        public TodoHolder(View itemView) {
            super(itemView);

            mTitleTextView = (TextView) itemView;
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
            View view = layoutInflater.inflate(android.R.layout.simple_list_item_1, parent, false);

            return new TodoHolder(view);
        }

        @Override
        public void onBindViewHolder(TodoHolder holder, int position) {
            TodoItem todoItem = mTodoItems.get(position);
            holder.mTitleTextView.setText(todoItem.getTitle());
        }

        @Override
        public int getItemCount() {
            return mTodoItems.size();
        }
    }


}
