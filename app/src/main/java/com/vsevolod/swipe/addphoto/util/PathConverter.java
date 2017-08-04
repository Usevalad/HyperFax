package com.vsevolod.swipe.addphoto.util;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.crash.FirebaseCrash;

import org.w3c.dom.Text;

/**
 * Created by vsevolod on 21.04.17.
 * getting full path instead of short android absolute path
 * needed for decoding image to byte array
 */

public final class PathConverter {
    private final String TAG = this.getClass().getSimpleName();
    private Context mContext;

    public PathConverter(Context context) {
        Log.e(TAG, "PathConverter: constructor");
        this.mContext = context;
    }

    public String getFullPath(Uri uri, int photoResource) {
        Log.e(TAG, "getFullPath");
        return getFullPathElse(uri);
    }

    private String getFullPathElse(@NonNull Uri uri) {
        Log.e(TAG, "getFullPathElse");
        Cursor cursor = null;
        String result = null;
        try {
            String[] projection = {MediaStore.Images.Media.DATA};
            cursor = mContext.getContentResolver().query(uri, projection, null, null, null);
            if (cursor == null) {
                result = uri.getPath();
            } else {
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                result = cursor.getString(columnIndex) != null
                        ? cursor.getString(columnIndex)
                        : cursor.getString(cursor.getColumnIndex(projection[0]));
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        mContext = null;

        if (TextUtils.isEmpty(result)) {
            result = getFulPathLollipop(uri);
        }

        Log.e(TAG, "getFullPath: " + result);
        return result;
    }

    private String getFulPathLollipop(Uri uri) {
        Log.e(TAG, "getFulPathLollipop");
        String wholeID = DocumentsContract.getDocumentId(uri);
        // Split at colon, use second item in the array
        String id = wholeID.split(":")[1];
        String[] column = {MediaStore.Images.Media.DATA};
        // where id is equal to
        String sel = MediaStore.Images.Media._ID + "=?";
        String result = null;
        Cursor cursor = mContext.getContentResolver().
                query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        column, sel, new String[]{id}, null);

        if (cursor != null) {
            int columnIndex = cursor.getColumnIndex(column[0]);
            if (cursor.moveToFirst()) {
                result = cursor.getString(columnIndex);
            }
            cursor.close();
        }
        Log.e(TAG, "getFulPathLollipop: path " + result);
        return result;
    }
}