package com.rahobbs.todo.activities;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;

import com.rahobbs.todo.fragments.ArchivedListFragment;

/**
 * Activity to host archived list fragment
 */
public class ArchivedListActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        ActionBar bar = getSupportActionBar();
        bar.setTitle("Archived Tasks");
        getWindow().setStatusBarColor(Color.parseColor("#424242"));
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#616161")));
        return new ArchivedListFragment();
    }
}
