package com.unnamed.b.atv.sample;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by vsevolod on 05.04.17.
 */

public class ModelList {

    @SerializedName("data")
    private List<Model> models;


    public ModelList(List<Model> models) {
        this.models = models;
    }

    public List<Model> getModels() {
        return models;
    }

    public void setModels(List<Model> models) {
        this.models = models;
    }
}
