package com.vsevolod.swipe.addphoto.model.responce;

/**
 * Created by vsevolod on 20.04.17.
 */


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

// TODO: 20.06.17 magic numbers
public class ListResponse {

    private List<String> ids = new ArrayList<>();
    private List<String> stateCodes = new ArrayList<>();
    private List<String> comments = new ArrayList<>();

    @SerializedName("columns")
    @Expose
    private List<String> columns = null;

    @SerializedName("list")
    @Expose
    private List<List<String>> list = null;

    @SerializedName("status")
    @Expose
    private String status;

    public ListResponse(List<String> columns, List<List<String>> list, String status) {
        super();
        this.columns = columns;
        this.status = status;
        this.list = list;
    }

    public ListResponse() {}
    /*
            оба конструктора не используются. Статус ответа проверяется в первую очередь,
            поэтому тут я инициализирую списки
     */
    public String getStatus() {
        if (ids.size() < 1)
            initLists(list);
        return status;
    }

    public List<List<String>> getList() {
        return list;
    }

    public int getListSize() {
        return list.size();
    }

    public List<String> getIds() {
        return ids;
    }

    public List<String> getComments() {
        return comments;
    }

    public List<String> getStateCodes() {
        return stateCodes;
    }

    public List<String> getColumns() {
        return columns;
    }

    private void initLists(List<List<String>> list) {
        if (list != null) {
            String uid, stateCode, comment;

            for (int i = 0; i < list.size(); i++) {
                uid = list.get(i).get(0); //post uid
                stateCode = list.get(i).get(1);// post state code
                comment = list.get(i).get(2); // post comment
                this.ids.add(uid);
                this.stateCodes.add(stateCode);
                this.comments.add(comment);
            }
        }
    }
}