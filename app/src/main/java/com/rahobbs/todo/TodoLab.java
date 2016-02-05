package com.rahobbs.todo;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by rachael on 2/2/16.
 * Creates an array list of the items to display
 */
public class TodoLab {
    private static TodoLab sTodoLab;
    private List<TodoItem> mTodoItems;

    public static TodoLab get(Context context) {
        if (sTodoLab == null) {
            sTodoLab = new TodoLab(context);
        }
        return sTodoLab;
    }

    private TodoLab(Context context) {
        mTodoItems = new ArrayList<>();
        //Create dummy items
        for (int i = 0; i < 50; i++) {
            TodoItem todo = new TodoItem();
            todo.setTitle("Todo # " + i);
            Log.d("things", "Constructed creating Item " + todo.getID());
            mTodoItems.add(todo);
        }
    }

    public List<TodoItem> getItems() {
        return mTodoItems;
    }

    public TodoItem getItem(UUID id) {
        for (TodoItem i : mTodoItems) {
            if (i.getID().equals(id)) {
                return i;
            }
        }
        return null;
    }
}
