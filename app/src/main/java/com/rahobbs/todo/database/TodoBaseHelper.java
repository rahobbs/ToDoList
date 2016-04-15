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

    private void createCurrentTable(SQLiteDatabase db) {
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
    public void onCreate(SQLiteDatabase db) {
        createCurrentTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String addDetails = "ALTER TABLE " + TodoTable.NAME + " ADD COLUMN DETAILS TEXT";
        String addParents = "ALTER TABLE " + TodoTable.NAME + " ADD COLUMN PARENTS TEXT";
        String addChildren = "ALTER TABLE " + TodoTable.NAME + " ADD COLUMN CHILDREN TEXT";
        String addPosition = "ALTER TABLE " + TodoTable.NAME + " ADD COLUMN POSITION TEXT";

        //create temp. table to hold data
        db.execSQL("create table todoBackup (" +
                TodoTable.Cols.UUID + ", " +
                TodoTable.Cols.TITLE + ", " +
                TodoTable.Cols.DATE + ", " +
                TodoTable.Cols.COMPLETED + "," +
                TodoTable.Cols.DETAILS + "," +
                TodoTable.Cols.PARENTS + "," +
                TodoTable.Cols.CHILDREN + ")"
        );

        //insert data from old table into temp table

        db.execSQL("INSERT INTO todoBackup SELECT " +
                TodoTable.Cols.UUID + ", " +
                TodoTable.Cols.TITLE + ", " +
                TodoTable.Cols.DATE + ", " +
                TodoTable.Cols.COMPLETED + ", " +
                TodoTable.Cols.DETAILS + ", " +
                TodoTable.Cols.PARENTS + ", " +
                TodoTable.Cols.CHILDREN + " from " + TodoTable.NAME
        );

        //drop the old table
        db.execSQL("DROP TABLE " + TodoTable.NAME);

        //recreate the up-to-date table
        createCurrentTable(db);

        //fill it from backup table
        db.execSQL("INSERT INTO " + TodoTable.NAME + " SELECT " +
                TodoTable.Cols.UUID + ", " +
                TodoTable.Cols.TITLE + ", " +
                TodoTable.Cols.DATE + ", " +
                TodoTable.Cols.COMPLETED + "," +
                TodoTable.Cols.DETAILS + ", " +
                TodoTable.Cols.PARENTS + ", " +
                TodoTable.Cols.CHILDREN + ", null from todoBackup"
        );

        //then drop the temporary table
        db.execSQL("DROP TABLE todoBackup");
    }

}
