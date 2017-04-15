package com.vsevolod.swipe.addphoto.model.query;

import com.google.gson.annotations.SerializedName;

/**
 * Created by vsevolod on 15.04.17.
 */

public class CommitModel {
    @SerializedName("token")
    final String token;

    @SerializedName("url")
    final String url;

    @SerializedName("id")
    final String id;

    public CommitModel(String token, String url, String id) {
        this.token = token;
        this.url = url;
        this.id = id;
    }
}
