package com.vsevolod.swipe.addphoto.config;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by vsevolod on 08.04.17.
 */

public class PreferenceHelper {
    private static final String TAG = "PreferenceHelper";
    public static final String APP_PREFERENCES = "myUser";
    public static final String APP_PREFERENCES_TOKEN = "token";
    public static final String APP_PREFERENCES_NAME = "user name";
    public static final String APP_PREFERENCES_TITLE = "user title";
    public static final String APP_PREFERENCES_PHONE = "user phone";
    public static final String APP_PREFERENCES_PASSWORD = "user password";
    private static SharedPreferences mUserSettings;
    private Context context;

    public PreferenceHelper() {
        Log.d(TAG, "PreferenceHelper");
        this.context = MyApplication.getAppContext();
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
