package com.vsevolod.swipe.addphoto.model.query;

import com.google.gson.annotations.SerializedName;

/**
 * Created by vsevolod on 15.04.17.
 */

public class CommitModel {
    @SerializedName("token")
    final String token;

    @SerializedName("id")
    final String id;

    @SerializedName("url")
    final String url;

    @SerializedName("flow")
    final String flow;

    @SerializedName("comment")
    final String comment;

    @SerializedName("created")
    final String created;

    @SerializedName("location")
    final String location;

    public CommitModel(String token, String url, String id, String flow,
                       String comment, String created, String location) {
        this.token = token;
        this.url = url;
        this.id = id;
        this.flow = flow;
        this.comment = comment;
        this.created = created;
        this.location = location;
    }
}
