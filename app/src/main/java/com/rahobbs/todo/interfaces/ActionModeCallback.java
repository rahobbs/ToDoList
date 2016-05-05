package com.rahobbs.todo.interfaces;

import android.support.design.widget.Snackbar;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.rahobbs.todo.R;
import com.rahobbs.todo.fragments.TodoListFragment;
import com.rahobbs.todo.helpers.TodoItem;
import com.rahobbs.todo.helpers.TodoLab;

import java.util.ArrayList;
import java.util.List;

/**
 * Customizations to ActionMode.Callback to handle the Context Action Menu for selecting and
 * manipulating multiple tasks.
 */
public class ActionModeCallback extends ActionMode.Callback{

    List<TodoItem> mSelectedItems = new ArrayList<>();
    Boolean mMultiSelectMode;

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        // Inflate a menu resource providing context menu items
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
        return true;
    }

    // Called each time the action mode is shown. Always called after onCreateActionMode, but
    // may be called multiple times if the mode is invalidated.
    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false; // Return false if nothing is done
    }

    // Called when the user selects a contextual menu item
    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.multi_delete:
                final List<TodoItem> copySelected = mSelectedItems;
                deleteItems(mSelectedItems);
                TodoListFragment.updateUI();

                Snackbar.make(getView(), "Deleting " + mSelectedItems.size() + " items",
                        Snackbar.LENGTH_LONG).setAction("Undo", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for (TodoItem i : copySelected) {
                            TodoLab.get(getActivity()).addTodoItem(i);
                            i.setPosition(i.getPosition());
                        }
                        mSelectedItems.clear();
                        updateUI();
                    }
                }).show();
                updateUI();
                mode.finish();
                return true;
            case R.id.close_menu:
                updateUI();
                mode.finish();
                mSelectedItems.clear();
                return true;
            default:
                return false;
        }
    }

    // Called when the user exits the action mode
    @Override
    public void onDestroyActionMode(ActionMode mode) {
        mMultiSelectMode = false;
        updateUI();
    }

    private void getSelectedItems(List<TodoItem> items){
        mSelectedItems = items;
    }

    /*
    * Takes a list of TodoItems and deletes those items from the database.
    */
    public void deleteItems(List<TodoItem> selectedItems) {
        for (TodoItem i : selectedItems) {
            TodoLab.get(getActivity()).deleteTodoItem(i.getID());
        }
    }
}
