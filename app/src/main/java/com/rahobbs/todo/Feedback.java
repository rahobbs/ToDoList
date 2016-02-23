package com.rahobbs.todo;
import android.content.Intent;

import java.util.Random;

/**
 * Created by rachael on 2/22/16.
 * Helper class for Send Feedback button
 */
public class Feedback {


    public Intent sendFeedback(Intent i) {
        int rand = new Random().nextInt(301);
        String versionCode = String.valueOf(BuildConfig.VERSION_CODE);
        int ticketCode = rand + 100;

        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL, new String[]{"feedback@rahobbs.com"});
        i.putExtra(Intent.EXTRA_SUBJECT, "(Case " + versionCode + ticketCode + ") The Round File Customer Feedback");

        return i;

    }
}