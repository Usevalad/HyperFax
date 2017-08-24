package com.vsevolod.swipe.addphoto.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Student Vsevolod on 24.08.17.
 * usevalad.uladzimiravich@gmail.com
 */

public class Folder {
    private String name;
    private String id;
    private String parentId;
    private String prefix;
    private List<Folder> children = new ArrayList<>();

    public Folder(String name, String id, String parentId, String prefix) {
        this.name = name;
        this.id = id;
        this.parentId = parentId;
        this.prefix = prefix;
    }

    public Folder(String name, String id, String parentId, String prefix, List<Folder> children) {
        this.name = name;
        this.id = id;
        this.parentId = parentId;
        this.prefix = prefix;
        this.children = children;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public List<Folder> getChildren() {
        return children;
    }

    public void addChildren(List<Folder> children) {
        this.children = children;
    }

    public void addChild(Folder child) {
        this.children.add(child);
    }

    public boolean haveChild() {
        return !this.children.isEmpty();
    }

    public Folder getChild(int location) {
        return this.children.get(location);
    }

}
