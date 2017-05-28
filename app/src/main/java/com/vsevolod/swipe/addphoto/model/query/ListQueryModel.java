package com.vsevolod.swipe.addphoto.model.query;

import com.google.gson.annotations.SerializedName;
import com.vsevolod.swipe.addphoto.config.MyApplication;

/**
 * Created by vsevolod on 20.04.17.
 */

public class ListQueryModel {
    @SerializedName("token")
    final String token;

    @SerializedName("id")
    final String[] id;

    @SerializedName("version")
    private final double version;

    @SerializedName("release")
    private final String release;

    public ListQueryModel(String token, String[] id) {
        this.version = MyApplication.getVersionCode();
        this.release = MyApplication.getBuildDate();
        this.token = token;
        this.id = id;
    }
}
