package com.rahobbs.todo.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.rahobbs.todo.TodoItem;
import com.rahobbs.todo.database.TodoSchema.TodoTable;

import java.util.Date;
import java.util.UUID;

/**
 * Created by rachael on 2/9/16.
 * Delegates calls to Cursor object
 */
public class TodoCursorWrapper extends CursorWrapper {
    public TodoCursorWrapper(Cursor cursor){
        super(cursor);
    }

    public TodoItem getTodoItem(){
        String uuidString = getString(getColumnIndex(TodoTable.Cols.UUID));
        String title = getString(getColumnIndex(TodoTable.Cols.TITLE));
        long date = getLong(getColumnIndex(TodoTable.Cols.DATE));
        int isCompleted = getInt(getColumnIndex(TodoTable.Cols.COMPLETED));

        TodoItem todoItem = new TodoItem(UUID.fromString(uuidString));
        todoItem.setTitle(title);
        todoItem.setDate(new Date(date));
        todoItem.setCompleted(isCompleted != 0);

        return todoItem;
    }
}
