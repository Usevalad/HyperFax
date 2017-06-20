package com.vsevolod.swipe.addphoto.config;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.crash.FirebaseCrash;

import java.io.File;

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
                mResult = uri.getPath();
            } else {
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                mResult = cursor.getString(columnIndex) != null
                        ? cursor.getString(columnIndex)
                        : cursor.getString(cursor.getColumnIndex(projection[0]));
            }
            if (mResult == null) {
                FirebaseCrash.log(TAG + " result = " + mResult);
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

        if (mResult == null) {
            mResult = uri.getPath();
        }

        Log.e(TAG, "getFullPath: " + mResult);
        return mResult;
    }
}