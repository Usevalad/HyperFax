package com.vsevolod.swipe.addphoto;

import io.realm.RealmObject;

/**
 * Created by vsevolod on 13.03.17.
 */

public class Model extends RealmObject {
    private String date;
    private String path;
    private String photoURI;
    private int deliveryCode = 0;
    private int stateCode = 0;
    private byte[] photo;

    public Model(String date, String path, String photoURI, byte[] photo) {
        this.date = date;
        this.path = path;
        this.photoURI = photoURI;
        this.photo = photo;
    }

    public Model() {
        //realm need to add empty constructor
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

    public String getPhotoURI() {
        return photoURI;
    }

    public void setPhotoURI(String photoURI) {
        this.photoURI = photoURI;
    }

    public int getStateIconImage() {
        switch (stateCode) {
            case 0:
                return R.drawable.ic_time;
            case 1:
                return R.drawable.ic_all_checked;
            case 2:
                return R.drawable.ic_canceled;
            default:
                return R.drawable.ic_bomb; //if something wrong you'll see bomb
        }
    }

    public int getDeliveryIconImage() {
        switch (deliveryCode) {
            case 0:
                return R.drawable.ic_time;
            case 1:
                return R.drawable.ic_checked;
            default:
                return R.drawable.ic_bomb; //if something wrong you'll see bomb
        }
    }
}
