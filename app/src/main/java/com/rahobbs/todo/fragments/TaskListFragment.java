package com.rahobbs.todo.fragments;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.rahobbs.todo.R;
import com.rahobbs.todo.activities.ArchivedListActivity;
import com.rahobbs.todo.helpers.Feedback;
import com.rahobbs.todo.helpers.SharableList;
import com.rahobbs.todo.helpers.TodoItem;
import com.rahobbs.todo.helpers.TodoLab;

import java.util.List;

/**
 * Fragment to hold list of active (non-archived) tasks
 */
public class TaskListFragment extends TodoListFragment {

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.fragment_todo_list, menu);
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
            case R.id.menu_item_view_archive:
                Intent intent = new Intent(getContext(), ArchivedListActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
