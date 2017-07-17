package com.vsevolod.swipe.addphoto.model.realm;

import com.vsevolod.swipe.addphoto.R;
import com.vsevolod.swipe.addphoto.constant.Constants;
import com.vsevolod.swipe.addphoto.util.MyDateUtil;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by vsevolod on 09.04.17.
 */
// TODO: 13.07.17 add date, viewDate, searchDate, latitude, longitude to constructor
public class DataModel extends RealmObject {
    @PrimaryKey
    private String uid;
    private String searchDate;
    private String prefix;
    private String name;
    private String serverPhotoURL;
    private String storagePhotoURL;
    private String description;
    private String comment;
    private String viewDate;
    private double latitude;
    private double longitude;
    private String stateCode = Constants.DATA_MODEL_STATE_NEED_SYNC;
    private byte[] photo;
    private String prefixID;
    private Date date;
    private boolean isSynced = false;

    public DataModel(String prefix, String name, String description,
                     String storagePhotoURL, byte[] photo, double latitude, double longitude,
                     String prefixID) {
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.searchDate = MyDateUtil.getSearchDate();
        this.prefix = prefix;
        this.serverPhotoURL = null;
        this.storagePhotoURL = storagePhotoURL;
        this.photo = photo;
        this.name = name;
        this.prefixID = prefixID;
        this.date = new Date();
        this.viewDate = MyDateUtil.getViewDate();
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getViewDate() {
        return viewDate;
    }

    public void setViewDate(String viewDate) {
        this.viewDate = viewDate;
    }

    public String getServerPhotoURL() {
        return serverPhotoURL;
    }

    public void setServerPhotoURL(String serverPhotoURL) {
        this.serverPhotoURL = serverPhotoURL;
    }

    public String getStoragePhotoURL() {
        return storagePhotoURL;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setStoragePhotoURL(String storagePhotoURL) {
        this.storagePhotoURL = storagePhotoURL;
    }

    public int getStateIconImage() {
        switch (stateCode) {
            case Constants.DATA_MODEL_STATE_CREATED:
                return R.drawable.ic_checked;
            case Constants.DATA_MODEL_STATE_PARAM:
                return R.drawable.ic_bomb;
            case Constants.DATA_MODEL_STATE_ACCEPTED:
                return R.drawable.ic_all_checked;
            case Constants.DATA_MODEL_STATE_REVIEW:
                return R.drawable.ic_glasses;
            case Constants.DATA_MODEL_STATE_DECLINED:
                return R.drawable.ic_canceled;
            case Constants.DATA_MODEL_STATE_NEED_SYNC:
                return R.drawable.ic_time;
            default:
                return R.drawable.ic_bomb; //if something went wrong you'll see the bomb
        }
    }
}