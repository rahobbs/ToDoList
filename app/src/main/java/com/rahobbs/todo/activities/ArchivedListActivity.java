package com.rahobbs.todo.activities;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;

import com.rahobbs.todo.fragments.ArchivedListFragment;

/**
 * Created by rachael on 5/11/16.
 */
public class ArchivedListActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        ActionBar bar = getSupportActionBar();
        bar.setTitle("Archived Tasks");
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#727272")));
        return new ArchivedListFragment();
    }
}
