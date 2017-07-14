package com.vsevolod.swipe.addphoto.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by vsevolod on 14.07.17.
 */

public class MyDateUtil {
    private static TimeZone timeZone = TimeZone.getTimeZone("UTC");

    public static String getViewDate() {
        SimpleDateFormat viewDateFormat = new SimpleDateFormat("HH:mm:ss dd.MM.yyyy E");
        viewDateFormat.setTimeZone(timeZone);
        return viewDateFormat.format(getDate());
    }

    public static String getSearchDate() {
        SimpleDateFormat searchDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        searchDateFormat.setTimeZone(timeZone);
        return searchDateFormat.format(getDate());
    }

    public static Date getDate() {
        return new Date();
    }
}
