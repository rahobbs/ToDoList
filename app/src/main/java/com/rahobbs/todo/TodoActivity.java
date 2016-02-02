package com.rahobbs.todo;

import android.support.v4.app.Fragment;
/**
 * Created by rachael on 2/1/16.
 * Activity that hosts a to-do item's detail view fragment
 */

public class TodoActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new TodoFragment();
    }
}
