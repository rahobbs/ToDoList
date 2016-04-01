package com.rahobbs.todo.helpers;
import android.content.Intent;
import android.util.Log;

import com.rahobbs.todo.BuildConfig;

/**
 * Created by rachael on 2/22/16.
 * Helper class for Send Feedback button
 */
public class Feedback {


    public Intent sendFeedback(Intent i) {
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL, new String[]{"feedback@rahobbs.com"});
        i.putExtra(Intent.EXTRA_SUBJECT, "The Round File Customer Feedback, version " + BuildConfig.VERSION_NAME);
        Log.v("Confirm sendFeeback", "The Round File Customer Feedback, version " + BuildConfig.VERSION_NAME );

        return i;

    }
}