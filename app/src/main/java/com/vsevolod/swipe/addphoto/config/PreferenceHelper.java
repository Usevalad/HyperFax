package com.vsevolod.swipe.addphoto.config;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by vsevolod on 08.04.17.
 */

public class PreferenceHelper {
    private final String TAG = "PreferenceHelper";
    public final String APP_PREFERENCES = "myUser";
    public final String APP_PREFERENCES_TOKEN = "token";
    public final String APP_PREFERENCES_NAME = "user name";
    public final String APP_PREFERENCES_TITLE = "user title";
    public final String APP_PREFERENCES_PHONE = "user phone";
    public final String APP_PREFERENCES_PASSWORD = "user password";
    private SharedPreferences mUserSettings;

    public PreferenceHelper(Context context) {
        Log.d(TAG, "PreferenceHelper");
        this.mUserSettings = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
    }

    public void saveString(String key, String value) {
        Log.d(TAG, "saveString");
        SharedPreferences.Editor editor = mUserSettings.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public String getUserName() {
        return mUserSettings.getString(APP_PREFERENCES_NAME, "not found");
    }

    public String getUserTitlte() {
        return mUserSettings.getString(APP_PREFERENCES_TITLE, "not found");
    }

    public String getToken() {
        return mUserSettings.getString(APP_PREFERENCES_TOKEN, "not found");
    }

    public String getPhone() {
        return mUserSettings.getString(APP_PREFERENCES_PHONE, "not found");
    }

    public String getPassword() {
        return mUserSettings.getString(APP_PREFERENCES_PASSWORD, "not found");
    }
}
