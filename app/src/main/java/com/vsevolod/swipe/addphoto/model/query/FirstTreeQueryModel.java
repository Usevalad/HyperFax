package com.vsevolod.swipe.addphoto.model.query;

import com.google.gson.annotations.SerializedName;

/**
 * Created by vsevolod on 28.05.17.
 */

public class FirstTreeQueryModel {
//    @SerializedName("version")
//    private final double version;
//
//    @SerializedName("release")
//    private final String release;

    @SerializedName("token")
    private final String token;

    public FirstTreeQueryModel(String token) {
//        this.version = MyApplication.getVersionCode();
//        this.release = MyApplication.getBuildDate();
        this.token = token;
    }
}

