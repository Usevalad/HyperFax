package com.unnamed.b.atv.sample;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by vsevolod on 05.04.17.
 */

public class Model {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("prefix")
    @Expose
    private String prefix;
    @SerializedName("parent")
    @Expose
    private String parent;

    public Model(String id, String name, String prefix, String parent) {
        this.id = id;
        this.name = name;
        this.prefix = prefix;
        this.parent = parent;
    }

    public Model() {
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

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }
}
