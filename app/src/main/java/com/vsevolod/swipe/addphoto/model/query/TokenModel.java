package com.vsevolod.swipe.addphoto.model.query;

import com.google.gson.annotations.SerializedName;

/**
 * Created by vsevolod on 08.04.17.
 */

public class TokenModel {
    @SerializedName("token")
    final String token;//= "21605da16ca43db7f8f2ff6ede1129ea";

    public TokenModel(String token) {
        this.token = token;
    }
}
