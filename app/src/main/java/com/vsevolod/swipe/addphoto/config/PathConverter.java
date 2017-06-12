package com.vsevolod.swipe.addphoto.config;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.crash.FirebaseCrash;

/**
 * Created by vsevolod on 21.04.17.
 * getting full path instead of short android absolute path
 * needed for decoding image to byte array
 */

public class PathConverter {
    private final String TAG = this.getClass().getSimpleName();
    private Context mContext;
    private String mResult;

    public PathConverter(Context context) {
        Log.e(TAG, "PathConverter: constructor");
        this.mContext = context;
    }

    public String getFullPath(@NonNull Uri uri) {
        Log.e(TAG, "getFullPath");
        Cursor cursor = null;
        try {
            String[] projection = {MediaStore.Images.Media.DATA};
            cursor = mContext.getContentResolver().query(uri, projection, null, null, null);
            if (cursor == null) {
                return uri.getPath();
            } else {
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                mResult = cursor.getString(columnIndex);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
            FirebaseCrash.log(TAG + " " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        mContext = null;
        FirebaseCrash.log(mResult);
        return mResult;
    }
}
