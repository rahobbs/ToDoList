package com.rahobbs.todo.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.rahobbs.todo.activities.ArchivedListActivity;
import com.rahobbs.todo.helpers.Feedback;
import com.rahobbs.todo.R;
import com.rahobbs.todo.helpers.SharableList;
import com.rahobbs.todo.interfaces.SimpleItemTouchHelperCallback;
import com.rahobbs.todo.helpers.TodoItem;
import com.rahobbs.todo.helpers.TodoLab;
import com.rahobbs.todo.activities.TodoPagerActivity;
import com.rahobbs.todo.helpers.TodoListAdapter;

import java.util.Collections;
import java.util.List;

/**
 * Fragment that contains the list of to-do items
 */
public abstract class TodoListFragment extends Fragment {

    private RecyclerView mTodoRecyclerView;
    private int listSize;
    public ItemTouchHelper touchHelper;
    public String mType = "todo_list";

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                final TodoItem item = (TodoItem) data.getSerializableExtra("item");
                if (getView() != null) {
                    Snackbar.make(getView(), "Item Deleted", Snackbar.LENGTH_LONG).setAction("Undo", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            TodoLab.get(getActivity()).addTodoItem(item);
                            item.setPosition(item.getPosition());
                            updateUI();
                        }
                    }).show();
                }
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.fragment_todo_list, menu);
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
                todoItem.setPosition(listSize + 1);
                TodoLab.get(getActivity()).addTodoItem(todoItem);
                Intent intent = TodoPagerActivity.newIntent(getActivity(), todoItem.getID());
                startActivity(intent);
            }
        });

        updateUI();
        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.share_list:
                List<TodoItem> list = TodoLab.get(getActivity()).getAllItems();
                SharableList sl = new SharableList();
                startActivity(Intent.createChooser(sl.makeSharable(list), "Send list..."));
                return true;
            case R.id.menu_item_send_feedback:
                Intent i = new Intent(Intent.ACTION_SEND);
                Feedback fb = new Feedback();
                fb.sendFeedback(i);
                startActivity(Intent.createChooser(i, "Send mail..."));
                return true;
            case R.id.menu_item_view_archive:
                Intent intent = ArchivedListActivity.newIntent(getContext());
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.todo_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        TodoLab todoLab = TodoLab.get(getActivity());
        List<TodoItem> todoItems = todoLab.getUnarchivedItems();
        listSize = todoItems.size();
        Collections.sort(todoItems, new TodoItem.PositionComparator());
        TodoListAdapter adapter = new TodoListAdapter(todoItems, getContext(), getView(), getActivity(), this);
        recyclerView.setAdapter(adapter);

        ItemTouchHelper.Callback callback =
                new SimpleItemTouchHelperCallback(adapter);
        touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(mTodoRecyclerView);
    }

    /*
    * Sort tasks by their position as saved in the database and update the UI to reflect the current
    * state of the db.
    */
    public void updateUI() {
        TodoLab todoLab = TodoLab.get(getActivity());
        List<TodoItem> todoItems = todoLab.getUnarchivedItems();
        Collections.sort(todoItems, new TodoItem.PositionComparator());

        TodoListAdapter recyclerAdapter = (TodoListAdapter) mTodoRecyclerView.getAdapter();
        if (recyclerAdapter == null) {
            mTodoRecyclerView.setAdapter(new TodoListAdapter(todoItems, getContext(), getView(), getActivity(), this));
        } else {
            recyclerAdapter.setTodoItems(todoItems);
        }
    }

    public void setFragmentType(String type) {
        mType = type;
    }

    /*
    * Takes a list of TodoItems and deletes those items from the database.
    */
    public void deleteSelected(List<TodoItem> selectedItems) {
        for (TodoItem i : selectedItems) {
            TodoLab.get(getActivity()).deleteTodoItem(i.getID());
        }
    }

    /*
    * Takes a list of TodoItems and sets them as archived.
    */
    public void archiveSelected(List<TodoItem> selectedItems) {
        for (TodoItem i : selectedItems) {
            i.setArchived(true);
            Log.v("Set archived", String.valueOf(i.isArchived()));
            TodoLab.get(getActivity()).updateItem(i);
        }
    }

    /*
    * Takes a list of TodoItems and sets them as *not* archived.
    */
    public void unArchiveSelected(List<TodoItem> selectedItems) {
        for (TodoItem i : selectedItems) {
            i.setArchived(false);
            Log.v("Set archived", String.valueOf(i.isArchived()));
            TodoLab.get(getActivity()).updateItem(i);
        }
    }
}