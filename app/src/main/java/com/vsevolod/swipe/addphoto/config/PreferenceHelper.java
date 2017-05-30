package com.vsevolod.swipe.addphoto.config;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by vsevolod on 08.04.17.
 */

public class PreferenceHelper {
    private final String TAG = this.getClass().getSimpleName();
    public static final String APP_PREFERENCES = "myUser";
    public static final String APP_PREFERENCES_TOKEN = "token";
    public static final String APP_PREFERENCES_NAME = "user name";
    public static final String APP_PREFERENCES_TITLE = "user title";
    public static final String APP_PREFERENCES_PHONE = "user phone";
    public static final String APP_PREFERENCES_PASSWORD = "user password";
    public static final String APP_PREFERENCES_MODIFIED = "modified date";
    public static final String APP_PREFERENCES_NOT_FOUND = "";
    public static final String APP_PREFERENCES_ACCOUNT_NAME = "Hyper Fax";
    public static final String APP_STATE = "application state";
    private SharedPreferences mUserSettings;

    public PreferenceHelper() {
        Log.d(TAG, "PreferenceHelper");
        this.mUserSettings = MyApplication.getAppContext().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
    }

    public void saveString(String key, String value) {
        Log.d(TAG, "saveString");
        SharedPreferences.Editor editor = mUserSettings.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public String getUserName() {
        return mUserSettings.getString(APP_PREFERENCES_NAME, APP_PREFERENCES_NOT_FOUND);
    }

    public String getUserTitlte() {
        return mUserSettings.getString(APP_PREFERENCES_TITLE, APP_PREFERENCES_NOT_FOUND);
    }

    public String getToken() {
        return mUserSettings.getString(APP_PREFERENCES_TOKEN, APP_PREFERENCES_NOT_FOUND);
    }

    public String getPhone() {
        return mUserSettings.getString(APP_PREFERENCES_PHONE, APP_PREFERENCES_NOT_FOUND);
    }

    public String getPassword() {
        return mUserSettings.getString(APP_PREFERENCES_PASSWORD, APP_PREFERENCES_NOT_FOUND);
    }

    public String getModified() {
        return mUserSettings.getString(APP_PREFERENCES_MODIFIED, APP_PREFERENCES_NOT_FOUND);
    }

    public String getAccountName() {
        return mUserSettings.getString(APP_PREFERENCES_ACCOUNT_NAME, APP_PREFERENCES_NOT_FOUND);
    }

    public String getAccountState() {
        return mUserSettings.getString(APP_STATE, APP_PREFERENCES_NOT_FOUND);
    }
}
