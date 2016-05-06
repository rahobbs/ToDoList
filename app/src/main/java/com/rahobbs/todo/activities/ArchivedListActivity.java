package com.rahobbs.todo.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.Fragment;

import com.rahobbs.todo.R;
import com.rahobbs.todo.fragments.ArchivedListFragment;

/**
 * Activity that hosts the to-do list fragment
 */
public class ArchivedListActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.light_grey)));
        getSupportActionBar().setTitle("Archived Tasks");
        getWindow().setStatusBarColor(getResources().getColor(R.color.darkFont));
        return new ArchivedListFragment();
    }


    public static Intent newIntent(Context packageContext) {
        Intent intent = new Intent(packageContext, ArchivedListActivity.class);
        return intent;
    }

}
