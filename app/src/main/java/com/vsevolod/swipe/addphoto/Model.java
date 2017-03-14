package com.vsevolod.swipe.addphoto;

import io.realm.RealmObject;

/**
 * Created by vsevolod on 13.03.17.
 */

public class Model extends RealmObject{
    String date;
    String path;
    int deliveryCode = 0;
    int stateCode = 0;
    byte[] photo;

    //maybe realm need to add empty constructor instead of this one
    public Model(String date, String path, byte[] photo) {
        this.date = date;
        this.path = path;
        this.photo = photo;
    }

    public Model() {
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getDeliveryCode() {
        return deliveryCode;
    }

    public void setDeliveryCode(int deliveryCode) {
        this.deliveryCode = deliveryCode;
    }

    public int getStateCode() {
        return stateCode;
    }

    public void setStateCode(int stateCode) {
        this.stateCode = stateCode;
    }

    public byte[] getPhoto() {
        return photo;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }
}
