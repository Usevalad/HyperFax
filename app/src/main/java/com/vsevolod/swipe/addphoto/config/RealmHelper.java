package com.vsevolod.swipe.addphoto.config;

import android.text.TextUtils;
import android.util.Log;

import com.vsevolod.swipe.addphoto.constant.DataState;
import com.vsevolod.swipe.addphoto.model.realm.DataModel;
import com.vsevolod.swipe.addphoto.model.realm.FlowsTreeModel;

import java.util.List;
import java.util.UUID;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by Student Vsevolod on 26.03.17.
 * usevalad.uladzimiravich@gmail.com
 */

public class RealmHelper {
    private final String TAG = this.getClass().getSimpleName();
    private final String DATE = "date";
    private final String SEARCH_ARTICLE = "searchArticle";
    private final String TREE_SEARCH_NAME = "searchName";
    private final String PREFIX = "prefix";
    private final String UID = "uid";
    private final String COMMENT = "searchComment";
    private Realm mRealm;

    public RealmHelper() {
        Log.d(TAG, "Realm constructor");
    }

    public void open() {
        Log.d(TAG, "open");
        Realm.init(MyApplication.getContext());
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder().build();
        Realm.setDefaultConfiguration(realmConfiguration);
        this.mRealm = Realm.getDefaultInstance();
    }

    public void close() {
        Log.d(TAG, "close");
        this.mRealm.close();
    }

    public Realm getRealm() {
        return mRealm;
    }

    public void dropRealmData() {
        Log.d(TAG, "dropRealmData");
        RealmResults<DataModel> results = this.mRealm.where(DataModel.class).findAll();
        // All changes to data must happen in a transaction
        this.mRealm.beginTransaction();
        // Delete all matches
        results.deleteAllFromRealm();
        this.mRealm.commitTransaction();
    }

    public void dropRealmTree() {
        Log.d(TAG, "dropRealmTree");
        RealmResults<FlowsTreeModel> results = this.mRealm.where(FlowsTreeModel.class).findAll();
        // All changes to data must happen in a transaction
        if (results.size() > 0) {
            this.mRealm.beginTransaction();
            // Delete all matches
            results.deleteAllFromRealm();
            this.mRealm.commitTransaction();
        }
    }

    public RealmResults getData() {
        Log.d(TAG, "getData");
        RealmQuery dataQuery = this.mRealm.where(DataModel.class);
        return dataQuery.findAllSorted(DATE, Sort.DESCENDING);
    }

    private RealmResults getTree() {
        RealmQuery treeQuery = this.mRealm.where(FlowsTreeModel.class);
        return treeQuery.findAll();
    }

    public boolean isValid(String text) {
        Log.d(TAG, "isValid");
        List<FlowsTreeModel> tree = getTree();
        String tmp;

        for (int i = 0; i < tree.size(); i++) {
            tmp = tree.get(i).toString();
            if (tmp.equals(text)) {
                return true;
            }
        }
        return false;
    }

    public RealmResults search(String queryString) {
        String SEARCH_DATE = "searchDate";
        String VIEW_DATE = "viewDate";
        String DESCRIPTION = "searchDescription";
        return this.mRealm.where(DataModel.class).beginGroup()
                .beginsWith(SEARCH_ARTICLE, queryString, Case.INSENSITIVE).or()
                .contains(SEARCH_ARTICLE, queryString, Case.INSENSITIVE).or()
                .equalTo(SEARCH_ARTICLE, queryString, Case.INSENSITIVE).or()
                .beginsWith(DESCRIPTION, queryString, Case.INSENSITIVE).or()
                .contains(DESCRIPTION, queryString, Case.INSENSITIVE).or()
                .equalTo(DESCRIPTION, queryString, Case.INSENSITIVE).or()
                .beginsWith(PREFIX, queryString, Case.INSENSITIVE).or()
                .contains(PREFIX, queryString, Case.INSENSITIVE).or()
                .equalTo(PREFIX, queryString, Case.INSENSITIVE).or()
                .beginsWith(VIEW_DATE, queryString, Case.INSENSITIVE).or()
                .contains(VIEW_DATE, queryString, Case.INSENSITIVE).or()
                .equalTo(VIEW_DATE, queryString, Case.INSENSITIVE).or()
                .beginsWith(COMMENT, queryString, Case.INSENSITIVE).or()
                .contains(COMMENT, queryString, Case.INSENSITIVE).or()
                .equalTo(COMMENT, queryString, Case.INSENSITIVE)
                .endGroup()
                .findAllSorted(SEARCH_DATE, Sort.DESCENDING);
    }

    public RealmResults searchTree(String queryString) {
        return this.mRealm.where(FlowsTreeModel.class).beginGroup()
                .beginsWith(TREE_SEARCH_NAME, queryString, Case.INSENSITIVE).or()
                .contains(TREE_SEARCH_NAME, queryString, Case.INSENSITIVE).or() //INSENSITIVE TO UPPER/LOWER CASES
                .equalTo(TREE_SEARCH_NAME, queryString, Case.INSENSITIVE).or()
                .beginsWith(PREFIX, queryString).or()
                .equalTo(PREFIX, queryString).or()
                .contains(PREFIX, queryString)
                .endGroup().findAll();
    }

    public void save(FlowsTreeModel model) {
        Log.d(TAG, "saveTreeListModel");
        this.mRealm.beginTransaction();
        mRealm.copyToRealmOrUpdate(model);
        this.mRealm.commitTransaction();
    }

    public void save(DataModel model) {
        Log.d(TAG, "saveDataModel");
        this.mRealm.beginTransaction();
        mRealm.copyToRealmOrUpdate(model);
        this.mRealm.commitTransaction();
    }

    public String getPrefixID(String prefix) {
        Log.d(TAG, "getPrefixID");
        RealmQuery idQuery = this.mRealm.where(FlowsTreeModel.class);
        idQuery.equalTo(PREFIX, prefix, Case.INSENSITIVE);
        FlowsTreeModel model;
        try {
            model = (FlowsTreeModel) idQuery.findFirst();
            return model.getId();
        } catch (NullPointerException e) {
            e.printStackTrace();
            Log.e(TAG, "getPrefixID: " + "Can't find prefix");
            return "no id";
        }
    }

    public void setServerPhotoURL(String id, String url) {
        DataModel model = getDataById(id);
        mRealm.beginTransaction();
        model.setServerPhotoURL(url);
        mRealm.commitTransaction();
    }

    public void setStateCode(String id, String stateCode) {
        DataModel model = getDataById(id);
        mRealm.beginTransaction();
        model.setStateCode(stateCode);
        mRealm.commitTransaction();
    }

    public void setComment(String id, String comment) {
        DataModel model = getDataById(id);
        mRealm.beginTransaction();
        model.setViewComment(comment);
        mRealm.commitTransaction();
    }

    public void setSynced(String id, boolean isSynced) {
        DataModel model = getDataById(id);
        mRealm.beginTransaction();
        model.setSynced(isSynced);
        mRealm.commitTransaction();
    }

    private DataModel getDataById(String id) {
        Log.d(TAG, "setField");
        RealmQuery dataQuery = this.mRealm.where(DataModel.class);
        dataQuery.equalTo(UID, id);
        DataModel model = null;
        try {
            model = (DataModel) dataQuery.findFirst();
        } catch (NullPointerException e) {
            e.printStackTrace();
            Log.d(TAG, "getDataById: no data with such id");
        }
        return model;
    }

    public RealmResults getNotSyncedData() {
        Log.d(TAG, "getNotSynced");
        RealmQuery query = this.mRealm.where(DataModel.class);
        String FIELD_IS_SYNCED = "isSynced";
        query.equalTo(FIELD_IS_SYNCED, false);
        return query.findAll();
    }

    public String[] getNotSyncedDataStatesIds() {
        Log.d(TAG, "getNotFinishedStates:");
        RealmQuery query = this.mRealm.where(DataModel.class);
        String FIELD_STATE_CODE = "stateCode";
        query.equalTo(FIELD_STATE_CODE, DataState.CREATED);
        query.or().equalTo(FIELD_STATE_CODE, DataState.REVIEW);
        List<DataModel> list = query.findAll();
        String[] ids = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            ids[i] = list.get(i).getUid();
        }
        return ids;
    }

    public boolean isStateCodeEqual(String uid, String stateCode) {
        RealmQuery query = this.mRealm.where(DataModel.class);
        query.equalTo(UID, uid);
        DataModel dataModel = (DataModel) query.findFirst();
        return TextUtils.equals(dataModel.getStateCode(), stateCode);
    }

    public void countData() {
        Log.d(TAG, "countData");
        List<DataModel> data = getData();
        for (DataModel model : data) {
            Log.i(TAG,
                    "searchDate = " + model.getSearchDate() + "\n" +
                            "date = " + model.getDate() + "\n" +
                            "uid = " + model.getUid() + "\n" +
                            "prefix = " + model.getPrefix() + "\n" +
                            "prefixID = " + model.getPrefixID() + "\n" +
                            "name = " + model.getViewArticle() + "\n" +
                            "comment = " + model.getViewDescription() + "\n" +
                            "storagePhotoURL = " + model.getStoragePhotoURL() + "\n" +
                            "serverPhotoURL = " + model.getServerPhotoURL() + "\n" +
                            "photoArrayLength = " + model.getPhoto().length + "\n" +
                            "latitude = " + model.getLatitude() + "\n" +
                            "longitude = " + model.getLongitude() + "\n" +
                            "stateCode = " + model.getStateCode() + "\n" +
                            "isSynced = " + model.isSynced());
        }
        Log.i(TAG, "countData: data.size is " + data.size());
        Log.i(TAG, "countData: not synced size is  " + getNotSyncedData().size());
    }

    public void countTree() {
        List<FlowsTreeModel> treeModels = getTree();
        for (FlowsTreeModel tree : treeModels) {
            Log.e(TAG, "countTree: getName " + tree.getViewName());
            Log.e(TAG, "countTree: getPrefix " + tree.getPrefix());
        }
    }
}