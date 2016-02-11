package com.rahobbs.todo;

import android.support.v4.app.Fragment;

/**
 * Created by rachael on 2/2/16.
 * Activity that hosts the to-do list fragment
 */
public class TodoListActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new TodoListFragment();
    }

}
