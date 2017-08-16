package com.vsevolod.swipe.addphoto.model.realm;

import com.vsevolod.swipe.addphoto.R;
import com.vsevolod.swipe.addphoto.constant.DataState;
import com.vsevolod.swipe.addphoto.util.MyDateUtil;

import java.util.Date;
import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by vsevolod on 09.04.17.
 */
public class DataModel extends RealmObject {
    @PrimaryKey
    private String uid;
    private String prefix;
    private String viewArticle;
    private String searchArticle;
    private String serverPhotoURL;
    private String storagePhotoURL;
    private String viewDescription;
    private String searchDescription;
    private String viewComment;
    private String searchComment;
    private String viewDate;
    private String searchDate;
    private Date date;
    private double latitude;
    private double longitude;
    private String stateCode;
    private String prefixID;
    private boolean isSynced = false;

    public DataModel(String prefix, String viewArticle, String viewDescription,
                     String storagePhotoURL, double latitude, double longitude,
                     String prefixID) {
        this.uid = UUID.randomUUID().toString();
        this.viewDescription = viewDescription;
        this.searchDescription = viewDescription.toLowerCase();
        this.latitude = latitude;
        this.longitude = longitude;
        this.searchDate = MyDateUtil.getSearchDate();
        this.prefix = prefix;
        this.serverPhotoURL = null;
        this.storagePhotoURL = storagePhotoURL;
        this.viewArticle = viewArticle;
        this.searchArticle = viewArticle.toLowerCase();
        this.prefixID = prefixID;
        this.date = new Date();
        this.viewDate = MyDateUtil.getViewDate();
        this.stateCode = DataState.NEED_SYNC;
    }

    public DataModel() {
        //realm needs to add empty constructor
    }

    public String getSearchDate() {
        return searchDate;
    }

    public String getStateCode() {
        return stateCode;
    }

    public void setStateCode(String stateCode) {
        this.stateCode = stateCode;
    }

    public String getViewDescription() {
        return viewDescription;
    }

    public String getViewComment() {
        return viewComment;
    }

    public void setViewComment(String viewComment) {
        this.viewComment = viewComment;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getViewArticle() {
        return viewArticle;
    }

    public String getUid() {
        return uid;
    }

    public String getPrefixID() {
        return prefixID;
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

    public String getServerPhotoURL() {
        return serverPhotoURL;
    }

    public void setServerPhotoURL(String serverPhotoURL) {
        this.serverPhotoURL = serverPhotoURL;
    }

    public String getStoragePhotoURL() {
        return storagePhotoURL;
    }

    public int getStateIconImage() {
        switch (stateCode) {
            case DataState.CREATED:
                return R.drawable.ic_checked;
            case DataState.PARAM:
                return R.drawable.ic_bomb;
            case DataState.ACCEPTED:
                return R.drawable.ic_all_checked;
            case DataState.REVIEW:
                return R.drawable.ic_glasses;
            case DataState.DECLINED:
                return R.drawable.ic_canceled;
            case DataState.NEED_SYNC:
                return R.drawable.ic_time;
            default:
                return R.drawable.ic_bomb; //if something went wrong you'll see the bomb
        }
    }
}