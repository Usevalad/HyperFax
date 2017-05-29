package com.vsevolod.swipe.addphoto.model.query;

import android.util.Log;

import com.google.gson.annotations.SerializedName;
import com.vsevolod.swipe.addphoto.config.MyApplication;

import java.util.HashMap;

/**
 * Created by vsevolod on 07.04.17.
 */

public class AuthModel {
    @SerializedName("version")
    final String version;

    @SerializedName("release")
    final String release;

    @SerializedName("phone")
    final String phone;

    @SerializedName("password")
    final String password;

    @SerializedName("android")
    final HashMap<String, String> android;

    public AuthModel(String phone, String password) {
        this.phone = phone;
        this.password = password;
        this.android = new HashMap<>();
        this.version = String.valueOf(MyApplication.getAppVersionCode());
        this.release = MyApplication.getBuildDate();
        this.android.put("build", String.valueOf(MyApplication.getBuildVersion()));
        this.android.put("model", MyApplication.getDeviceModel());
        Log.e("AuthModel", toString());
    }

    @Override
    public String toString() {
        return "phone: " + phone + "\n" +
                "password: " + password;
    }
}