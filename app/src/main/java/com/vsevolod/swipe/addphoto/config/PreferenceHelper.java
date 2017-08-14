package com.vsevolod.swipe.addphoto.config;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by vsevolod on 08.04.17.
 */

public final class PreferenceHelper {
    private final String TAG = this.getClass().getSimpleName();
    private final String APP_PREFERENCES = "myUser";
    private final String APP_PREFERENCES_NOT_FOUND = "";
    private SharedPreferences mUserSettings;
    public static final String APP_PREFERENCES_MODIFIED = "modified date";
    public static final String APP_PREFERENCES_FLOWS_UPDATE_DATE = "last date of flows tree update";
    public static final String APP_PREFERENCES_ACCOUNT_NAME = "Hyper Fax";

    public PreferenceHelper() {
        Log.d(TAG, "PreferenceHelper");
        this.mUserSettings = MyApplication
                .getContext()
                .getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
    }

    public void saveString(String key, String value) {
        Log.d(TAG, "saveString");
        SharedPreferences.Editor editor = mUserSettings.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public void saveDate(String key, long value) {
        Log.e(TAG, "saveDate");
        SharedPreferences.Editor editor = mUserSettings.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    public String getModified() {
        return mUserSettings.getString(APP_PREFERENCES_MODIFIED, APP_PREFERENCES_NOT_FOUND);
    }

    public String getAccountName() {
        return mUserSettings.getString(APP_PREFERENCES_ACCOUNT_NAME, APP_PREFERENCES_NOT_FOUND);
    }

    public long getLastUpdate() {
        return mUserSettings.getLong(APP_PREFERENCES_FLOWS_UPDATE_DATE, 0);
    }
}
