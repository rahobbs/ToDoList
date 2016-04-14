package com.rahobbs.todo.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.rahobbs.todo.database.TodoSchema.TodoTable;

/**
 * Helper class to manage database creation and version management
 */
public class TodoBaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 4;
    private static final String DATABASE_NAME = "todoBase.db";

    public TodoBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TodoTable.NAME + "(" +
                TodoTable.Cols.UUID + ", " +
                TodoTable.Cols.TITLE + ", " +
                TodoTable.Cols.DATE + ", " +
                TodoTable.Cols.COMPLETED + "," +
                TodoTable.Cols.DETAILS + "," +
                TodoTable.Cols.PARENTS + "," +
                TodoTable.Cols.CHILDREN + "," +
                TodoTable.Cols.POSITION + ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String addDetails = "ALTER TABLE " + TodoTable.NAME + " ADD COLUMN DETAILS TEXT";
        String addParents = "ALTER TABLE " + TodoTable.NAME + " ADD COLUMN PARENTS TEXT";
        String addChildren = "ALTER TABLE " + TodoTable.NAME + " ADD COLUMN CHILDREN TEXT";
        String addPosition = "ALTER TABLE " + TodoTable.NAME + " ADD COLUMN POSITION TEXT";

        if (oldVersion == 1 && newVersion == 2) {
            db.execSQL(addDetails);
            db.execSQL(addParents);
            db.execSQL(addChildren);
            Log.v("DB Update", "Updated from Schema " + oldVersion + " to " + newVersion);
        }
        if (oldVersion < 4 && newVersion == 4) {
            db.execSQL(addPosition);
            Log.v("DB Update", "Updated from Schema " + oldVersion + " to " + newVersion);
        }
    }

}
