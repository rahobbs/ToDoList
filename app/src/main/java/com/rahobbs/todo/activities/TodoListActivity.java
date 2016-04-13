package com.rahobbs.todo.activities;

import android.support.v4.app.Fragment;

import com.rahobbs.todo.fragments.TodoListFragment;

/**
 * Activity that hosts the to-do list fragment
 */
public class TodoListActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new TodoListFragment();
    }

}
