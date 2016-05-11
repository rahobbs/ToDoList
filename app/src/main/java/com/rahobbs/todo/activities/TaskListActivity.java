package com.rahobbs.todo.activities;

import android.support.v4.app.Fragment;

import com.rahobbs.todo.fragments.TaskListFragment;
/**
 * Activity that hosts the to-do list fragment
 */
public class TaskListActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new TaskListFragment();
    }

}
