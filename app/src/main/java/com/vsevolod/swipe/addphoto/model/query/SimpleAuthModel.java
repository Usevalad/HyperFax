package com.vsevolod.swipe.addphoto.model.query;

import com.google.gson.annotations.SerializedName;

/**
 * Created by vsevolod on 08.04.17.
 */

public class SimpleAuthModel {
    @SerializedName("phone")
    final String phone;

    public SimpleAuthModel(String phone) {
        this.phone = phone;
    }
}
