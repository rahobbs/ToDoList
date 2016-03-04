package com.rahobbs.todo;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Created by rachael on 2/2/16.
 * Fragment that contains the list of to-do items
 */
public class TodoListFragment extends Fragment{

    private RecyclerView mTodoRecyclerView;
    public List<TodoItem> selectedItems = new ArrayList<>();
    public Boolean multiSelectMode = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_todo_list, container, false);

        mTodoRecyclerView = (RecyclerView) view.findViewById(R.id.todo_recycler_view);
        mTodoRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

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

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.todo_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        TodoLab todoLab = TodoLab.get(getActivity());
        List<TodoItem> todoItems = todoLab.getItems();
        TodoAdapter adapter = new TodoAdapter(todoItems);
        recyclerView.setAdapter(adapter);

        ItemTouchHelper.Callback callback =
                new SimpleItemTouchHelperCallback(adapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(mTodoRecyclerView);
    }

    private void updateUI() {
        TodoLab todoLab = TodoLab.get(getActivity());
        List<TodoItem> todoItems = todoLab.getItems();

        TodoAdapter recyclerAdapter = (TodoAdapter) mTodoRecyclerView.getAdapter();
        if (recyclerAdapter == null) {
            mTodoRecyclerView.setAdapter(new TodoAdapter(todoItems));
        } else {
            recyclerAdapter.setTodoItems(todoItems);
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

        if (multiSelectMode) {
            inflater.inflate(R.menu.fragment_todo_list_action, menu);
        }
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
            case R.id.multi_delete:
                Log.v("Items to delete: ", selectedItems.toString());
                deleteSelected(selectedItems);
                updateUI();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void deleteSelected(List<TodoItem> selectedItems) {
        for (TodoItem i : selectedItems) {
            TodoLab.get(getActivity()).deleteTodoItem(i.getID());
        }
    }

    private class TodoHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView mTitleTextView;
        public TextView mDateLabel;
        public TextView mDateTextView;
        public CheckBox mCompletedCheckBox;
        private TodoItem mTodo;
        public RelativeLayout mListItem;
        public ImageView mHandle;

        public TodoHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            mTitleTextView = (TextView) itemView.findViewById(R.id.list_item_todo_title_text_view);
            mDateTextView = (TextView) itemView.findViewById(R.id.list_item_todo_date_text_view);
            mCompletedCheckBox = (CheckBox) itemView.findViewById(R.id.list_item_todo_completed_check_box);
            mDateLabel = (TextView) itemView.findViewById(R.id.due_date_label);
            mListItem = (RelativeLayout) itemView.findViewById(R.id.list_item_todo);
            mHandle = (ImageView) itemView.findViewById(R.id.reorder_handle);

            mListItem.setOnLongClickListener(new View.OnLongClickListener() {
                public boolean onLongClick(View arg0) {
                    selectedItems.add(mTodo);
                    multiSelectMode = true;
                    mListItem.setBackgroundColor(getResources().getColor(R.color.done_task));

                    return true;    // <- set to true
                }
            });
        }

        public void bindTodo(TodoItem todo) {
            SimpleDateFormat format = new SimpleDateFormat("E dd MMM yyyy", Locale.ENGLISH);
            mTodo = todo;

            if (mTodo.getTitle() != null) {
                mTitleTextView.setText(mTodo.getTitle().trim());
                mListItem.setBackgroundColor(getResources().getColor(R.color.default_background_light));
            }

            mDateTextView.setText(format.format(mTodo.getDate()));

            mCompletedCheckBox.setChecked(mTodo.isCompleted());
            mCompletedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    mTodo.setCompleted(isChecked);

                    if (isChecked) {
                        mTitleTextView.setTextColor(getResources().getColor(R.color.inactiveText));
                        mDateTextView.setPaintFlags(mDateTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        mDateLabel.setPaintFlags(mDateTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    } else {
                        mTitleTextView.setTextColor(getResources().getColor(R.color.darkFont));
                        mDateTextView.setPaintFlags(mDateTextView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                        mDateLabel.setPaintFlags(mDateTextView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                    }
                    TodoLab.get(getActivity()).updateItem(mTodo);
                }
            });
        }

        public void updateCompleted() {
            if (mTodo.isCompleted()) {
                mTitleTextView.setTextColor(getResources().getColor(R.color.inactiveText));
                mDateTextView.setPaintFlags(mDateTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                mDateLabel.setPaintFlags(mDateTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                mTitleTextView.setTextColor(getResources().getColor(R.color.inactiveText));
            }
        }

        @Override
        public void onClick(View v) {
            if (!multiSelectMode) {
                Intent intent = TodoPagerActivity.newIntent(getActivity(), mTodo.getID());
                startActivity(intent);
            }
            if (multiSelectMode) {
                if (selectedItems.contains(mTodo)) {
                    selectedItems.remove(mTodo);
                    mListItem.setBackgroundColor(getResources().getColor(R.color.default_background_light));
                    if (selectedItems.isEmpty()) {
                        multiSelectMode = false;
                    }
                } else {
                    selectedItems.add(mTodo);
                    mListItem.setBackgroundColor(getResources().getColor(R.color.done_task));
                }
            }
        }
    }

    private class TodoAdapter extends RecyclerView.Adapter<TodoHolder> implements ItemTouchHelperAdapter {
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
            holder.updateCompleted();
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
            /*
            Swipe dismiss currently disabled.
             */
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
            return true;
        }
    }
}
