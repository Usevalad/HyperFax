package com.vsevolod.swipe.addphoto.model.query;

import com.google.gson.annotations.SerializedName;
import com.vsevolod.swipe.addphoto.config.MyApplication;
import com.vsevolod.swipe.addphoto.config.PreferenceHelper;

/**
 * Created by vsevolod on 08.04.17.
 */

public class TreeQueryModel {
    @SerializedName("version")
    private final double version;

    @SerializedName("release")
    private final String release;

    @SerializedName("token")
    private final String token;

    @SerializedName("modified")
    private final String modified;

    public TreeQueryModel(String token) {
        this.version = MyApplication.getVersionCode();
        this.release = MyApplication.getBuildDate();
        this.token = token;
        this.modified = new PreferenceHelper().getModified();
    }
}
