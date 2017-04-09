package com.vsevolod.swipe.addphoto.model.responce;

/**
 * Created by vsevolod on 08.04.17.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CheckedInfo {

    @SerializedName("html")
    @Expose
    private String html;
    @SerializedName("status")
    @Expose
    private String status;

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}