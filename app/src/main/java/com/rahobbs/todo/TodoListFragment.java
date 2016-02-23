package com.rahobbs.todo;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * Created by rachael on 2/2/16.
 * Fragment that contains the list of to-do items
 */
public class TodoListFragment extends Fragment {

    private RecyclerView mTodoRecycleView;
    private TodoAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_todo_list, container, false);

        mTodoRecycleView = (RecyclerView) view.findViewById(R.id.todo_recycler_view);
        mTodoRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));
        FloatingActionButton addFab = (FloatingActionButton) view.findViewById(R.id.add_fab);

        addFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TodoItem todoItem = new TodoItem();
                TodoLab.get(getActivity()).addTodoItem(todoItem);
                Intent intent = TodoPagerActivity.newIntent(getActivity(), todoItem.getID());
                startActivity(intent);
            }
        });

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
            mAdapter.setTodoItems(todoItems);
            mAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_todo_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_send_feedback:
                Intent i = new Intent(Intent.ACTION_SEND);
                Feedback fb = new Feedback();
                fb.sendFeedback(i);
                startActivity(Intent.createChooser(i, "Send mail..."));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class TodoHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView mTitleTextView;
        public TextView mDateLabel;
        public TextView mDateTextView;
        public CheckBox mCompletedCheckBox;
        private TodoItem mTodo;

        public TodoHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            mTitleTextView = (TextView) itemView.findViewById(R.id.list_item_todo_title_text_view);
            mDateTextView = (TextView) itemView.findViewById(R.id.list_item_todo_date_text_view);
            mCompletedCheckBox = (CheckBox) itemView.findViewById(R.id.list_item_todo_completed_check_box);
            mDateLabel = (TextView) itemView.findViewById(R.id.due_date_label);


        }

        @SuppressWarnings("deprecation")
        public void bindTodo(TodoItem todo) {
            SimpleDateFormat format = new SimpleDateFormat("E dd MMM yyyy", Locale.ENGLISH);
            mTodo = todo;

            if(mTodo.getTitle() != null){
            mTitleTextView.setText(mTodo.getTitle().trim());}

            if (mTodo.isCompleted()) {
                mTitleTextView.setTextColor(getResources().getColor(R.color.inactiveText));
                mDateTextView.setPaintFlags(mDateTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                mDateLabel.setPaintFlags(mDateTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            }

            mDateTextView.setText(format.format(mTodo.getDate()));

            if (mTodo.isCompleted()) {
                mTitleTextView.setTextColor(getResources().getColor(R.color.inactiveText));
            }

            mCompletedCheckBox.setChecked(mTodo.isCompleted());
            mCompletedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    boolean boxChecked = mTodo.isCompleted();

                    if (boxChecked) {
                        mTodo.setCompleted(false);
                        mTitleTextView.setTextColor(getResources().getColor(R.color.darkFont));
                        mDateTextView.setPaintFlags(mDateTextView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                        mDateLabel.setPaintFlags(mDateTextView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                    } else {
                        mTodo.setCompleted(true);
                        mTitleTextView.setTextColor(getResources().getColor(R.color.inactiveText));
                        mDateTextView.setPaintFlags(mDateTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        mDateLabel.setPaintFlags(mDateTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    }
                    TodoLab.get(getActivity()).updateItem(mTodo);

                }
            });
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

        public void setTodoItems(List<TodoItem> todoItems) {
            mTodoItems = todoItems;
        }
    }


}
