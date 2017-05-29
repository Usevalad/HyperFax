package com.vsevolod.swipe.addphoto.model.query;

import android.util.Log;

import com.google.gson.annotations.SerializedName;
import com.vsevolod.swipe.addphoto.config.MyApplication;

/**
 * Created by vsevolod on 07.04.17.
 */

public class AuthModel {
    @SerializedName("appVersion")
    private final double appVersion;

    @SerializedName("appRelease")
    private final String appRelease;

    @SerializedName("phone")
    final String phone;

    @SerializedName("password")
    final String password;

    @SerializedName("androidModel")
    final String androidModel;

    @SerializedName("androidVersion")
    final int androidVersion;

    public AuthModel(String phone, String password) {
        this.appVersion = MyApplication.getVersionCode();
        this.appRelease = MyApplication.getBuildDate();
        this.phone = phone;
        this.password = password;
        this.androidModel = MyApplication.getAndroidModel();
        this.androidVersion = MyApplication.getAndroidVersion();
        Log.e("AuthModel", toString());
    }

    @Override
    public String toString() {
        return "appVersion: " + appVersion + "\n" +
                "appRelease: " + appRelease + "\n" +
                "phone: " + phone + "\n" +
                "password: " + password + "\n" +
                "androidModel: " + androidModel + "\n" +
                "androidVersion: " + androidVersion;
    }
}
