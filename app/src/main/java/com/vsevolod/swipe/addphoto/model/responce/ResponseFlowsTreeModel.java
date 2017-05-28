package com.vsevolod.swipe.addphoto.model.responce;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by vsevolod on 09.04.17.
 */

public class ResponseFlowsTreeModel {

    @SerializedName("columns")
    @Expose
    private List<String> columns;

    @SerializedName("list")
    @Expose
    private List<List<String>> list;

    @SerializedName("modified")
    @Expose
    private String modified;

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("error")
    private String error;

    @SerializedName("notify")
    private String notify;

    @SerializedName("log")
    private String log;

    @SerializedName("result")
    private String result;

    public String getResult() {
        return result;
    }

    public String getLog() {
        return log;
    }

    public List<String> getColumns() {
        return columns;
    }

    public List<List<String>> getList() {
        return list;
    }

    public String getModified() {
        return modified;
    }

    public String getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getNotify() {
        return notify;
    }
}
