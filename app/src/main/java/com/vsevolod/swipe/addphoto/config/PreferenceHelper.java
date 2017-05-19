package com.vsevolod.swipe.addphoto.config;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by vsevolod on 08.04.17.
 */

public class PreferenceHelper {
    private final String TAG = this.getClass().getSimpleName();
    private SharedPreferences mUserSettings;
    private final String NOT_FOUND = "not found";

    public PreferenceHelper(Context context) {
        Log.d(TAG, "PreferenceHelper");
        String APP_PREFERENCES = "myUser";
        this.mUserSettings = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
    }

    public void saveString(String key, String value) {
        Log.d(TAG, "saveString");
        SharedPreferences.Editor editor = mUserSettings.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public String getUserName() {
        String APP_PREFERENCES_NAME = "user name";
        return mUserSettings.getString(APP_PREFERENCES_NAME, NOT_FOUND);
    }

    public String getUserTitle() {
        String APP_PREFERENCES_TITLE = "user title";
        return mUserSettings.getString(APP_PREFERENCES_TITLE, NOT_FOUND);
    }

    public String getToken() {
        String APP_PREFERENCES_TOKEN = "token";
        return mUserSettings.getString(APP_PREFERENCES_TOKEN, NOT_FOUND);
    }

    public String getPhone() {
        String APP_PREFERENCES_PHONE = "user phone";
        return mUserSettings.getString(APP_PREFERENCES_PHONE, NOT_FOUND);
    }

    public String getPassword() {
        String APP_PREFERENCES_PASSWORD = "user password";
        return mUserSettings.getString(APP_PREFERENCES_PASSWORD, NOT_FOUND);
    }
}
