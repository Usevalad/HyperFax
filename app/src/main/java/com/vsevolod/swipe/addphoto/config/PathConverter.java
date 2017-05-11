package com.vsevolod.swipe.addphoto.config;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;

/**
 * Created by vsevolod on 21.04.17.
 * <p>
 * getting full path instead of short android absolute path
 * needed for decoding image to byte array
 */

public class PathConverter {
    private static final String TAG = "PathConverter";

    public static String getFullPath(@NonNull Uri uri) {
        Log.d(TAG, "getFullPath");
        Cursor cursor = null;
        try {
            String[] projection = {MediaStore.Images.Media.DATA};
            Context context = MyApplication.getAppContext();
            cursor = context.getContentResolver().query(uri, projection, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
    }
}
