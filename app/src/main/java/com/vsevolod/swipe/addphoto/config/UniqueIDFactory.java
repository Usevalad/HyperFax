package com.vsevolod.swipe.addphoto.config;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by vsevolod on 15.04.17.
 */

public class UniqueIDFactory {
    public static String generateId() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssE");
        String formattedDate = dateFormat.format(calendar.getTime());
        String random = String.valueOf((Math.random() + 1) * 100);//[100;200]

        return formattedDate + random;
    }
}
