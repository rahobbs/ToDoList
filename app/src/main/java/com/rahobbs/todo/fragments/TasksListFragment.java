package com.rahobbs.todo.fragments;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.rahobbs.todo.R;
import com.rahobbs.todo.helpers.TodoItem;
import com.rahobbs.todo.helpers.TodoLab;
import com.rahobbs.todo.helpers.TodoListAdapter;
import com.rahobbs.todo.interfaces.SimpleItemTouchHelperCallback;

import java.util.Collections;
import java.util.List;

/**
 * Fragment to display list of active/unarchived tasks
 */
public class TasksListFragment extends TodoListFragment {

    public ItemTouchHelper touchHelper;
    public String type = "task_list";

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.todo_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        TodoLab todoLab = TodoLab.get(getActivity());
        List<TodoItem> todoItems = todoLab.getUnarchivedItems();
        Collections.sort(todoItems, new TodoItem.PositionComparator());
        TodoListAdapter adapter = new TodoListAdapter(todoItems, getContext(), getView(), getActivity(), this);
        recyclerView.setAdapter(adapter);

        setFragmentType(type);

        ItemTouchHelper.Callback callback =
                new SimpleItemTouchHelperCallback(adapter);
        touchHelper = new ItemTouchHelper(callback);

    }
}
