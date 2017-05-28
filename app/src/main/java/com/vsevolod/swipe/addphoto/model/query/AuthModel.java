package com.vsevolod.swipe.addphoto.model.query;

import com.google.gson.annotations.SerializedName;
import com.vsevolod.swipe.addphoto.config.MyApplication;

/**
 * Created by vsevolod on 07.04.17.
 */

public class AuthModel {
    @SerializedName("version")
    private final double version;

    @SerializedName("release")
    private final String release;

    @SerializedName("phone")
    final String phone;

    @SerializedName("password")
    final String password;

    public AuthModel(String phone, String password) {
        this.version = MyApplication.getVersionCode();
        this.release = MyApplication.getBuildDate();
        this.phone = phone;
        this.password = password;
    }
}
