package com.vsevolod.swipe.addphoto;

import io.realm.RealmObject;

/**
 * Created by vsevolod on 13.03.17.
 */

public class Model extends RealmObject {
    String date;
    String path;
    String photoURI;
    int deliveryCode = 0;
    int stateCode = 0;
    byte[] photo;

    public Model(String date, String path, byte[] photo) {
        this.date = date;
        this.path = path;
        this.photo = photo;
    }

    public Model(String date, String path, String photoURI, byte[] photo) {
        this.date = date;
        this.path = path;
        this.photoURI = photoURI;
        this.photo = photo;
    }

    public Model() {
        //realm need to add empty constructor instead of this one
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
//        if (deliveryCode == 0) {
//            return 666; // TODO: 24.03.17 if delivery code is not ok - don't set any image
//        } else {
            switch (stateCode) {
                case 0:
                    return R.drawable.ic_time;
                case 1:
                    return R.drawable.ic_all_checked;
                case 2:
                    return R.drawable.ic_cancel;
                default:
                    return R.drawable.ic_bomb; //if something wrong you'll see bomb
//            }
        }
    }

    public int getDeliveryIconImage() {
        switch (deliveryCode) {
            case 0:
                return R.drawable.ic_time;
            case 1:
//                if (stateCode == 1 || stateCode == 2) {
//                    return 666; // TODO: 24.03.17 if stateCode = 1 or 2 - don't set any image
//                } else {
                return R.drawable.ic_checked;
//                }
            default:
                return R.drawable.ic_bomb; //if something wrong you'll see bomb
        }
    }
}
