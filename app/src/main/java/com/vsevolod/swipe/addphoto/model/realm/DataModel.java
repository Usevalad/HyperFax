package com.vsevolod.swipe.addphoto.model.realm;

import com.vsevolod.swipe.addphoto.R;
import com.vsevolod.swipe.addphoto.config.Constants;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by vsevolod on 09.04.17.
 */

public class DataModel extends RealmObject {
    @PrimaryKey
    private String uid;
    private String searchDate;
    private String prefix;
    private String name;
    private String photoURL;
    private String comment;
    private double latitude;
    private double longitude;
    private String stateCode = Constants.DATA_MODEL_STATE_NEED_SYNC;
    private byte[] photo;
    private String prefixID;
    private Date date;
    private boolean isSynced = false;

    public DataModel(String searchDate, String prefix, String name, String comment,
                     String photoURL, byte[] photo, double latitude, double longitude,
                     String prefixID, Date date) {
        this.comment = comment;
        this.latitude = latitude;
        this.longitude = longitude;
        this.searchDate = searchDate;
        this.prefix = prefix;
        this.photoURL = photoURL;
        this.photo = photo;
        this.name = name;
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

    public String getPhotoURL() {
        return photoURL;
    }

    public void setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
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

    public boolean isSynced() {
        return isSynced;
    }

    public void setSynced(boolean synced) {
        isSynced = synced;
    }


    public int getStateIconImage() {
        switch (stateCode) {
            case Constants.DATA_MODEL_STATE_CREATED:
                return R.drawable.ic_checked;
            case Constants.DATA_MODEL_STATE_ACCEPTED:
                return R.drawable.ic_all_checked;
            case Constants.DATA_MODEL_STATE_REVIEW:
                return R.drawable.ic_bomb;
            case Constants.DATA_MODEL_STATE_DECLINED:
                return R.drawable.ic_canceled;
            case Constants.DATA_MODEL_STATE_NEED_SYNC:
                return R.drawable.ic_time;
            case Constants.ACTION_SELECT_PICTURE:
                return R.drawable.leak_canary_icon; // TODO: 19.05.17 если при ответе на отправку фото приходит статус
            // PARAM, то поставить такую иконку
            default:
                return R.drawable.ic_bomb; //if something went wrong you'll see the bomb
        }
    }
}