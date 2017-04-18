package com.vsevolod.swipe.addphoto.config;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by vsevolod on 15.04.17.
 */

public class UniqueIDFactory {
    private static final String TAG = "UniqueIDFactory";

    public static String generateId() {
        Log.d(TAG, "generateId:");
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssE");
        String formattedDate = dateFormat.format(calendar.getTime());
        String random = String.valueOf((Math.random() + 1) * 100);//[100;200]

        Log.d(TAG, formattedDate + random);
        return formattedDate + random;
    }
}
