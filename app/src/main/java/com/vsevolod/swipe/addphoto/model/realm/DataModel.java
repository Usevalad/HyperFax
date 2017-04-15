package com.vsevolod.swipe.addphoto.model.realm;

import com.vsevolod.swipe.addphoto.R;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by vsevolod on 09.04.17.
 */

public class DataModel extends RealmObject {
    @PrimaryKey
    private String uid;
    private String searchDate;
    private String viewDate;
    private String prefix;
    private String name;
    private String photoURI;
    private String serverPhotoURL;
    private String comment;
    private double latitude;
    private double longitude;
    private int stateCode = 0;
    private byte[] photo;

    public DataModel(String searchDate, String viewDate, String prefix, String name, String comment,
                     String photoURI, String serverPhotoURL, byte[] photo, double latitude, double longitude) {
        this.comment = comment;
        this.viewDate = viewDate;
        this.latitude = latitude;
        this.longitude = longitude;
        this.searchDate = searchDate;
        this.prefix = prefix;
        this.photoURI = photoURI;
        this.photo = photo;
        this.name = name;
        this.serverPhotoURL = serverPhotoURL;
    }

    public DataModel() {
        //realm need to add empty constructor
    }

    public String getSearchDate() {
        return searchDate;
    }

    public void setSearchDate(String searchDate) {
        this.searchDate = searchDate;
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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getViewDate() {
        return viewDate;
    }

    public void setViewDate(String viewDate) {
        this.viewDate = viewDate;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getServerPhotoURL() {
        return serverPhotoURL;
    }

    public void setServerPhotoURL(String serverPhotoURL) {
        this.serverPhotoURL = serverPhotoURL;
    }

    public String getUid() {
        return uid;
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
}