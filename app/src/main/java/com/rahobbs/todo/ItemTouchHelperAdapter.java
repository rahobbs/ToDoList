package com.rahobbs.todo;


/**
 * Created by rachael on 3/4/16.
 */
public interface ItemTouchHelperAdapter {

    boolean onItemMove(int fromPosition, int toPosition);

    void onItemDismiss(int position);
}

