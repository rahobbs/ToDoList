package com.rahobbs.todo;

import java.util.Date;
import java.util.UUID;

/**
 * Created by rachael on 2/1/16.
 */
public class TodoItem {
    private String mTitle;
    private Date mDate;
    private UUID mID;
    private boolean completed;

    public TodoItem(){
        mID = UUID.randomUUID();
        mDate = new Date();
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public UUID getID() {
        return mID;
    }
}
