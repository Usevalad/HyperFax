package com.vsevolod.swipe.addphoto.model.query;

import com.google.gson.annotations.SerializedName;

/**
 * Created by vsevolod on 07.04.17.
 */

public class AuthModel {
    @SerializedName("phone")
    final String phone;

    @SerializedName("password")
    final String password;

    public AuthModel(String phone, String password) {
        this.phone = phone;
        this.password = password;
    }
}
