package com.rahobbs.todo.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.rahobbs.todo.R;
import com.rahobbs.todo.helpers.Feedback;
import com.rahobbs.todo.helpers.SharableList;
import com.rahobbs.todo.helpers.TodoItem;
import com.rahobbs.todo.helpers.TodoLab;
import com.rahobbs.todo.interfaces.SimpleItemTouchHelperCallback;

import java.util.Collections;
import java.util.List;

/**
 * Created by rachael on 5/11/16.
 */
public class ArchivedListFragment extends TodoListFragment {

    int listSize;
    RecyclerView mTodoRecyclerView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context_menu = R.menu.archive_context_menu;
        fragType = "archived_list_fragment";
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.archived_list_fragment, container, false);
        mTodoRecyclerView = (RecyclerView) view.findViewById(R.id.todo_recycler_view);
        mTodoRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
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
        List<TodoItem> todoItems = todoLab.getArchivedItems();
        listSize = todoItems.size();
        Collections.sort(todoItems, new TodoItem.PositionComparator());
        TodoListAdapter adapter = new TodoListAdapter(todoItems);
        recyclerView.setAdapter(adapter);

        ItemTouchHelper.Callback callback =
                new SimpleItemTouchHelperCallback(adapter);
        touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(mTodoRecyclerView);
    }

    public void updateUI() {
        TodoLab todoLab = TodoLab.get(getActivity());
        List<TodoItem> todoItems = todoLab.getArchivedItems();
        Collections.sort(todoItems, new TodoItem.PositionComparator());

        TodoListAdapter recyclerAdapter = (TodoListAdapter) mTodoRecyclerView.getAdapter();
        if (recyclerAdapter == null) {
            mTodoRecyclerView.setAdapter(new TodoListAdapter(todoItems));
        } else {
            recyclerAdapter.setTodoItems(todoItems);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.fragment_archive_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.share_list:
                List<TodoItem> list = TodoLab.get(getActivity()).getUnarchivedItems();
                SharableList sl = new SharableList();
                startActivity(Intent.createChooser(sl.makeSharable(list), "Send list..."));
                return true;
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

}
