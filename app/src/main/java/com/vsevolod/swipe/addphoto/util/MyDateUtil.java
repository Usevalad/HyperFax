package com.vsevolod.swipe.addphoto.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by vsevolod on 14.07.17.
 */

public final class MyDateUtil {
    private static SimpleDateFormat viewDateFormat = new SimpleDateFormat("HH:mm  dd.MM.yyyy EEEE");
    private static SimpleDateFormat searchDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static String getViewDate() {
        return viewDateFormat.format(new Date());
    }

    public static String getSearchDate() {
        searchDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return searchDateFormat.format(new Date());
    }
}