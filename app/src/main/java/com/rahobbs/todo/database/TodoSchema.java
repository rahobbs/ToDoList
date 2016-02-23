package com.rahobbs.todo.database;

/**
 * Created by rachael on 2/9/16.
 * Defines database for to-do items
 */
public class TodoSchema {
    public static final class TodoTable{
        public static final String NAME = "todoItems";

        public static final class Cols{
            public static final String UUID = "uuid";
            public static final String TITLE = "title";
            public static final String DATE = "date";
            public static final String COMPLETED = "completed";
            public static final String DETAILS = "details";
            public static final String PARENTS = "parents";
            public static final String CHILDREN = "children";

        }
    }
}
