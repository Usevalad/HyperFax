package com.vsevolod.swipe.addphoto.model.responce;

/**
 * Created by vsevolod on 20.04.17.
 */


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ListResponse {

    @SerializedName("columns")
    @Expose
    private List<String> columns = null;
    @SerializedName("list")
    @Expose
    private List<List<String>> list = null;
    @SerializedName("status")
    @Expose
    private String status;

    public ListResponse() {
    }

    public ListResponse(List<String> columns, List<List<String>> list, String status) {
        super();
        this.columns = columns;
        this.list = list;
        this.status = status;
    }

    public List<List<String>> getList() {
        return list;
    }

    public String getStatus() {
        return status;
    }

    public int getListSize() {
        return list.size();
    }

    public List<String> getIds() {
        List<String> ids = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            String uid = list.get(i).get(0);
            ids.add(uid);
            String stateCode = list.get(i).get(1);
        }
        return ids;
    }

    public List<String> stateCodes() {
        List<String> states = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            String uid = list.get(i).get(0);
            String stateCode = list.get(i).get(1);
            states.add(stateCode);
        }
        return states;
    }
}