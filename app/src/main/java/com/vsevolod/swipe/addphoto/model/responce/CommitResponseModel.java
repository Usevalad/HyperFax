package com.vsevolod.swipe.addphoto.model.responce;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by vsevolod on 29.05.17.
 */

public class CommitResponseModel {
    @SerializedName("log")
    @Expose
    private String log;

    @SerializedName("status")
    @Expose
    private String status;

    public CommitResponseModel(String log, String status) {
        super();
        this.log = log;
        this.status = status;
    }

    public String getLog() {
        return log;
    }

    public String getStatus() {
        return status;
    }
}
