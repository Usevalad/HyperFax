package com.vsevolod.swipe.addphoto.model.realm;

import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by vsevolod on 08.04.17.
 */

public class FlowsTreeModel extends RealmObject {
    @PrimaryKey
    private String uid;
    private String id;
    private String viewName;
    private String searchName;
    private String prefix;
    private String parentId;

    public FlowsTreeModel(String id, String viewName, String searchName, String prefix, String parentId) {
        this.id = id;
        this.viewName = viewName;
        this.searchName = searchName;
        this.prefix = prefix;
        this.parentId = parentId;
        this.uid = UUID.randomUUID().toString();
    }

    public FlowsTreeModel() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getViewName() {
        return viewName;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    public String getSearchName() {
        return searchName;
    }

    public void setSearchName(String searchName) {
        this.searchName = searchName;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    @Override
    public String toString() {
        return viewName + " " + prefix;
    }
}
