package com.vsevolod.swipe.addphoto.model.answer;

/**
 * Created by vsevolod on 08.04.17.
 */

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FlowsTreeModel {

    @SerializedName("columns")
    @Expose
    private List<String> columns = null;
    @SerializedName("list")
    @Expose
    private List<List<String>> list = null;
    @SerializedName("modified")
    @Expose
    private String modified;
    @SerializedName("status")
    @Expose
    private String status;

    public List<String> getColumns() {
        return columns;
    }

    public void setColumns(List<String> columns) {
        this.columns = columns;
    }

    public List<List<String>> getList() {
        return list;
    }

    public void setList(List<List<String>> list) {
        this.list = list;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}