package com.rahobbs.todo.helpers;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;
import java.util.UUID;

/**
 * Model for a single to-do item
 */
public class TodoItem implements Serializable {
    private String mTitle;
    private String mDetails;
    private Date mDate;
    private UUID mID;
    private boolean completed;
    private String mParents;
    private String mChildren;
    private int mPosition;
    private boolean mArchived;


    public TodoItem() {
        mID = UUID.randomUUID();
        mDate = new Date(0);
    }

    public TodoItem(UUID id) {
        mID = id;
        mDate = new Date(0);
    }

    public int getPosition() {
        return mPosition;
    }

    public void setPosition(int position) {
        mPosition = position;
    }

    public boolean isCompleted() {

        return completed;
    }

    public void setCompleted(boolean completed) {

        this.completed = completed;
    }

    public boolean isArchived() {
        return mArchived;
    }

    public void setArchived(boolean mArchived) {
        this.mArchived = mArchived;
    }

    public Date getDate() {
        if (mDate != null){
        return mDate;
        } else{
            return null;
        }
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

    public void setParents(String parents) {
        mParents = parents;
    }


    public void setChildren(String children) {
        mChildren = children;
    }

    public static class PositionComparator implements Comparator<TodoItem> {
        public int compare(TodoItem item1, TodoItem item2) {
            return item1.getPosition() - item2.getPosition();
        }
    }
}
