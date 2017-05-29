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

    @SerializedName("flow")
    final String flow;

    @SerializedName("id")
    final String id;

    @SerializedName("created")
    final String created;

    @SerializedName("location")
    final String location;

    @SerializedName("comment")
    final String comment;

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

    @Override
    public String toString() {
        return "token = " + token + "\n" +
                "url = " + url + "\n" +
                "id = " + id + "\n" +
                "flow = " + flow + "\n" +
                "comment = " + comment + "\n" +
                "created = " + created + "\n" +
                "location = " + location;
    }
}
