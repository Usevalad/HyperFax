package com.vsevolod.swipe.addphoto.model.responce;

/**
 * Created by vsevolod on 08.04.17.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AuthResponseModel {
    @SerializedName("active")
    @Expose
    private String active;

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("token")
    @Expose
    private String token;

    @SerializedName("result")
    @Expose
    private String result;

    @SerializedName("error")
    @Expose
    private String error;

    @SerializedName("notify")
    @Expose
    private String notify;

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("title")

    @Expose
    private String title;

    public String getNotify() {
        return notify;
    }

    public String getError() {
        return error;
    }

    public String getResult() {
        return result;
    }

    public String getActive() {
        return active;
    }

    public String getStatus() {
        return status;
    }

    public String getToken() {
        return token;
    }

    public String getName() {
        return name;
    }

    public String getTitle() {
        return title;
    }
}