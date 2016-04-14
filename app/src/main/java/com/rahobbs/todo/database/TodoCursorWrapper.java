package com.rahobbs.todo.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.rahobbs.todo.helpers.TodoItem;
import com.rahobbs.todo.database.TodoSchema.TodoTable;

import java.util.Date;
import java.util.UUID;

/**
 * Delegates calls to Cursor object
 */
public class TodoCursorWrapper extends CursorWrapper {
    public TodoCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public TodoItem getTodoItem() {
        String uuidString = getString(getColumnIndex(TodoTable.Cols.UUID));
        String title = getString(getColumnIndex(TodoTable.Cols.TITLE));
        long date = getLong(getColumnIndex(TodoTable.Cols.DATE));
        int isCompleted = getInt(getColumnIndex(TodoTable.Cols.COMPLETED));
        String details = getString(getColumnIndex(TodoTable.Cols.DETAILS));
        String parents = getString(getColumnIndex(TodoTable.Cols.PARENTS));
        String children = getString(getColumnIndex(TodoTable.Cols.CHILDREN));
        int position = getInt(getColumnIndex(TodoTable.Cols.POSITION));


        TodoItem todoItem = new TodoItem(UUID.fromString(uuidString));
        todoItem.setTitle(title);
        todoItem.setDate(new Date(date));
        todoItem.setCompleted(isCompleted != 0);
        todoItem.setDetails(details);
        todoItem.setParents(parents);
        todoItem.setChildren(children);
        todoItem.setPosition(position);

        return todoItem;
    }
}
