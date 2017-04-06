package com.unnamed.b.atv.sample;

import com.google.gson.annotations.SerializedName;

/**
 * Created by vsevolod on 05.04.17.
 */

public class PostModel {
    @SerializedName("token")
    final String token;//= "21605da16ca43db7f8f2ff6ede1129ea";

    public PostModel(String token) {
        this.token = token;
    }


    public String getToken() {
        return token;
    }
}
