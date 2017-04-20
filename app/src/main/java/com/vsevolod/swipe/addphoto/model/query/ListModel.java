package com.vsevolod.swipe.addphoto.model.query;

import com.google.gson.annotations.SerializedName;

/**
 * Created by vsevolod on 20.04.17.
 */

public class ListModel {
    @SerializedName("token")
    final String token;

    @SerializedName("id")
    final String[] id;

    public ListModel(String token, String[] id) {
        this.token = token;
        this.id = id;
    }
}
