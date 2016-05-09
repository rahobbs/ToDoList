package com.rahobbs.todo.helpers;

import android.content.Intent;

import java.util.List;


/**
 * Helper class to build an intent for sharing a list
 */
public class SharableList {

    public SharableList() {

    }

    public Intent makeSharable(List<TodoItem> itemsList) {
        Intent listIntent = new Intent(Intent.ACTION_SEND);
        String textToSend = "";
        String bulletChar;

        for (TodoItem i : itemsList) {
            if (i.isCompleted()) {
                bulletChar = "\u2713";
            } else {
                bulletChar = "\u2022";
            }
            textToSend = textToSend + bulletChar + " " + i.getTitle() + "\n";
        }
        textToSend = textToSend.trim();


        listIntent.setType("text/plain");
        listIntent.putExtra(Intent.EXTRA_TEXT, textToSend);

        return listIntent;
    }
}
