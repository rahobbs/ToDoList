package com.rahobbs.todo;

import java.util.Date;
import java.util.UUID;

/**
 * Created by rachael on 2/1/16.
 * Model for a single to-do item
 */
public class TodoItem {
    private String mTitle;
    private String mDetails;
    private Date mDate;
    private UUID mID;
    private boolean completed;
    private String mParents;
    private String mChildren;


    public TodoItem() {
        mID = UUID.randomUUID();
        mDate = new Date();
    }

    public TodoItem(UUID id) {
        mID = id;
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

    public String getDetails() {
        return mDetails;
    }

    public void setDetails(String details) {
        mDetails = details;
    }

    public String getParents() {
        return mParents;
    }

    public void setParents(String parents) {
        mParents = parents;
    }

    public String getChildren() {
        return mChildren;
    }

    public void setChildren(String children) {
        mChildren = children;
    }
}
