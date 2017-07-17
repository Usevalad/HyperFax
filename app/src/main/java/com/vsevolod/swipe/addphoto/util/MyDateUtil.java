package com.vsevolod.swipe.addphoto.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by vsevolod on 14.07.17.
 */

public final class MyDateUtil {
    private static TimeZone timeZone = TimeZone.getTimeZone("UTC");
    private static SimpleDateFormat viewDateFormat = new SimpleDateFormat("HH:mm:ss dd.MM.yyyy E");
    private static SimpleDateFormat searchDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static String getViewDate() {
        viewDateFormat.setTimeZone(timeZone);
        return viewDateFormat.format(new Date());
    }

    public static String getSearchDate() {
        searchDateFormat.setTimeZone(timeZone);
        return searchDateFormat.format(new Date());
    }
}