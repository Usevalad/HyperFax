package com.vsevolod.swipe.addphoto.model.realm;

import io.realm.RealmObject;

/**
 * Created by vsevolod on 08.04.17.
 */

public class FlowsTreeModel extends RealmObject {
    private String id;
    private String name;
    private String prefix;
    private String parentId;

    public FlowsTreeModel(String id, String name, String prefix, String parentId) {
        this.id = id;
        this.name = name;
        this.prefix = prefix;
        this.parentId = parentId;
    }

    public FlowsTreeModel() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
        return name + " @" + prefix;
    }
}
