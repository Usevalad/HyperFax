package com.vsevolod.swipe.addphoto.model.realm;

import com.vsevolod.swipe.addphoto.R;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by vsevolod on 09.04.17.
 */

public class DataModel extends RealmObject {
    @PrimaryKey
    private String uid;
    private String searchDate; // TODO: 11.05.17 maybe i need to left only Date date?
    private String viewDate;
    private String prefix;
    private String name;
    private String photoURI;
    private String serverPhotoURL; //// TODO: 11.05.17 maybe i need to left only one photo link
    /// and change it when i'll get it from server response
    // TODO: 11.05.17 decide it before i'll finish MVP
    private String comment;
    private double latitude;
    private double longitude;
    private String stateCode = "Created"; // FIXME: 11.05.17 hardcode
    private byte[] photo;
    private String prefixID;
    private Date date;

    public DataModel(String searchDate, String viewDate, String prefix, String name, String comment,
                     String photoURI, String serverPhotoURL, byte[] photo, double latitude, double longitude,
                     String prefixID, Date date) {
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
        this.prefixID = prefixID;
        this.date = date;
    }

    public DataModel() {
        //realm needs to add empty constructor
    }

    public String getSearchDate() {
        return searchDate;
    }

    public void setSearchDate(String searchDate) {
        this.searchDate = searchDate;
    }

    public String getStateCode() {
        return stateCode;
    }

    public void setStateCode(String stateCode) {
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

    public String getPrefixID() {
        return prefixID;
    }

    public void setPrefixID(String prefixID) {
        this.prefixID = prefixID;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getStateIconImage() {// FIXME: 11.05.17 hardcode
        switch (stateCode) {
            case "Created":
                return R.drawable.ic_time;
            case "Accepted":
                return R.drawable.ic_all_checked;
            case "Review":
                return R.drawable.ic_bomb;
            case "Declined":
                return R.drawable.ic_canceled;
            default:
                return R.drawable.ic_bomb; //if something went wrong you'll see the bomb
        }
    }
}