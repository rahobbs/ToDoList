package com.rahobbs.todo;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import java.util.UUID;

/**
 * Created by rachael on 2/1/16.
 * Activity that hosts a to-do item's detail view fragment
 */

public class TodoActivity extends SingleFragmentActivity {

    private static final String EXTRA_TODO_ID = "com.rahobbs.todo.todo_id";

    public static Intent newIntent(Context packageContext, UUID todoId){
        Intent intent = new Intent(packageContext, TodoActivity.class);
        intent.putExtra(EXTRA_TODO_ID, todoId);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        UUID todoId = (UUID) getIntent().getSerializableExtra(EXTRA_TODO_ID);
        return TodoFragment.newInstance(todoId);
    }
}
