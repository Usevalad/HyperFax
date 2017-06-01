package com.vsevolod.swipe.addphoto.config;

import android.text.TextUtils;
import android.util.Log;

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
 * Created by vsevolod on 26.03.17.
 */
public class RealmHelper {
    private final String TAG = this.getClass().getSimpleName();
    private final String FIELD_DATE = "date";
    private final String FIELD_NAME = "name";
    private final String FIELD_PREFIX = "prefix";
    private final String FIELD_UID = "uid";
    private Realm realm;

    public RealmHelper() {
        Log.d(TAG, "Realm constructor");
    }

    public void open() {
        Log.d(TAG, "open");
        Realm.init(MyApplication.getAppContext());
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder().build();
        Realm.setDefaultConfiguration(realmConfiguration);
        this.realm = Realm.getDefaultInstance();
    }

    public void close() {
        Log.d(TAG, "close");
        this.realm.close();
    }

    public Realm getRealm() {
        return realm;
    }

    public void dropRealmData() {
        Log.d(TAG, "dropRealmData");
        RealmResults<DataModel> results = this.realm.where(DataModel.class).findAll();
        // All changes to data must happen in a transaction
        this.realm.beginTransaction();
        // Delete all matches
        results.deleteAllFromRealm();
        this.realm.commitTransaction();
    }

    public void dropRealmTree() {
        Log.d(TAG, "dropRealmTree");
        RealmResults<FlowsTreeModel> results = this.realm.where(FlowsTreeModel.class).findAll();
        // All changes to data must happen in a transaction
        if (results.size() > 0) {
            this.realm.beginTransaction();
            // Delete all matches
            results.deleteAllFromRealm();
            this.realm.commitTransaction();
        }
    }


    public RealmResults getData() {
        Log.d(TAG, "getData");
        RealmQuery dataQuery = this.realm.where(DataModel.class);
        return dataQuery.findAllSorted(FIELD_DATE, Sort.DESCENDING);
    }

    private RealmResults getTree() {
        RealmQuery treeQuery = this.realm.where(FlowsTreeModel.class);
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
        String FIELD_SEARCH_DATE = "searchDate";
        String FIELD_COMMENT = "comment";
        String FIELD_VIEW_DATE = "viewDate";
        return this.realm.where(DataModel.class).beginGroup().
                contains(FIELD_SEARCH_DATE, queryString, Case.INSENSITIVE).or()
                .beginsWith(FIELD_NAME, queryString, Case.INSENSITIVE).or()
                .contains(FIELD_NAME, queryString, Case.INSENSITIVE).or()
                .equalTo(FIELD_NAME, queryString, Case.INSENSITIVE).or()
                .beginsWith(FIELD_COMMENT, queryString, Case.INSENSITIVE).or()
                .contains(FIELD_COMMENT, queryString, Case.INSENSITIVE).or()
                .beginsWith(FIELD_PREFIX, queryString, Case.INSENSITIVE).or()
                .contains(FIELD_PREFIX, queryString, Case.INSENSITIVE).or()
                .equalTo(FIELD_PREFIX, queryString, Case.INSENSITIVE).or()
                .beginsWith(FIELD_VIEW_DATE, queryString, Case.INSENSITIVE).or()
                .contains(FIELD_VIEW_DATE, queryString, Case.INSENSITIVE).or()
                .equalTo(FIELD_VIEW_DATE, queryString, Case.INSENSITIVE)
                .endGroup()
                .findAllSorted(FIELD_SEARCH_DATE, Sort.ASCENDING);
    }

    public RealmResults searchTree(String queryString) {
        return this.realm.where(FlowsTreeModel.class).beginGroup()
                .beginsWith(FIELD_NAME, queryString, Case.INSENSITIVE).or()
                .contains(FIELD_NAME, queryString, Case.INSENSITIVE).or() //INSENSITIVE TO UPPER/LOWER CASES
                .equalTo(FIELD_NAME, queryString, Case.INSENSITIVE).or()
                .beginsWith(FIELD_PREFIX, queryString).or()
                .equalTo(FIELD_PREFIX, queryString).or()
                .contains(FIELD_PREFIX, queryString)
                .endGroup().findAll();
    }

    public void save(List<FlowsTreeModel> flowsTreeModels) {
        Log.d(TAG, "saveTreeList");
        FlowsTreeModel tmp;
        for (int i = 0; i < flowsTreeModels.size(); i++) {
            this.realm.beginTransaction();
            // Create an object
            tmp = flowsTreeModels.get(i);
            FlowsTreeModel model = this.realm.createObject(FlowsTreeModel.class);
            // Set its fields
            model.setId(tmp.getId());
            model.setName(tmp.getName());
            model.setParentId(tmp.getParentId());
            model.setPrefix(tmp.getPrefix());

            this.realm.commitTransaction();
        }
    }

    public void save(FlowsTreeModel model) {
        Log.d(TAG, "saveTreeListModel");
        this.realm.beginTransaction();
        // Create an object
        FlowsTreeModel newModel = this.realm.createObject(FlowsTreeModel.class);
        // Set its fields
        newModel.setId(model.getId());
        newModel.setName(model.getName());
        newModel.setParentId(model.getParentId());
        newModel.setPrefix(model.getPrefix());

        this.realm.commitTransaction();
    }

    public void save(DataModel model) {
        Log.d(TAG, "saveDataModel");
        this.realm.beginTransaction();
        // Create an object
        DataModel newModel = this.realm.createObject(DataModel.class, UUID.randomUUID().toString());
        // Set its fields
        newModel.setSearchDate(model.getSearchDate());
        newModel.setPrefix(model.getPrefix());
        newModel.setPhoto(model.getPhoto());
        newModel.setLatitude(model.getLatitude());
        newModel.setLongitude(model.getLongitude());
        newModel.setComment(model.getComment());
        newModel.setStateCode(model.getStateCode());
        newModel.setStoragePhotoURL(model.getStoragePhotoURL());
        newModel.setName(model.getName());
        newModel.setPrefixID(model.getPrefixID());
        newModel.setDate(model.getDate());
        newModel.setViewDate(model.getViewDate());

        this.realm.commitTransaction();
    }


    public DataModel getLastDataModel() {
        Log.d(TAG, "getLastDataModel");
        RealmQuery dataQuery = this.realm.where(DataModel.class);
        RealmResults<DataModel> models = dataQuery.findAllSorted(FIELD_DATE, Sort.DESCENDING);
        return models.first();
    }

    public String getPrefixID(String prefix) {
        Log.d(TAG, "getPrefixID");
        RealmQuery idQuery = this.realm.where(FlowsTreeModel.class);
        idQuery.equalTo(FIELD_PREFIX, prefix, Case.INSENSITIVE);
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

    public void setPhotoURL(String id, String serverPhotoURL) {
        Log.d(TAG, "updateServerPhotoURL");

        RealmQuery dataQuery = this.realm.where(DataModel.class);
        dataQuery.equalTo(FIELD_UID, id);
        DataModel model;
        try {
            model = (DataModel) dataQuery.findFirst();
        } catch (NullPointerException e) {
            e.printStackTrace();
            Log.d(TAG, "getDataById: no data with such id");
            return;
        }

        this.realm.beginTransaction();
        model.setServerPhotoURL(serverPhotoURL);
        this.realm.copyToRealmOrUpdate(model);
        this.realm.commitTransaction();
        Log.d(TAG, "updateServerPhotoURL: updated " + serverPhotoURL);
    }

    public void setSynced(String id, boolean isSynced) {
        Log.d(TAG, "setSynced");

        RealmQuery dataQuery = this.realm.where(DataModel.class);
        dataQuery.equalTo(FIELD_UID, id);
        DataModel model;
        try {
            model = (DataModel) dataQuery.findFirst();
        } catch (NullPointerException e) {
            e.printStackTrace();
            Log.d(TAG, "getDataById: no data with such id");
            return;
        }

        this.realm.beginTransaction();
        model.setSynced(isSynced);
        this.realm.copyToRealmOrUpdate(model);
        this.realm.commitTransaction();
        Log.d(TAG, "setSynced: isSynced " + isSynced);
    }

    public RealmResults getNotSyncedData() {
        Log.d(TAG, "getNotSynced");
        RealmQuery query = this.realm.where(DataModel.class);
        String FIELD_IS_SYNCED = "isSynced";
        query.equalTo(FIELD_IS_SYNCED, false);
        return query.findAll();
    }

    public String[] getNotSyncedDataStatesIds() {
        Log.d(TAG, "getNotFinishedStates:");
        RealmQuery query = this.realm.where(DataModel.class);
        String FIELD_STATE_CODE = "stateCode";
        query.equalTo(FIELD_STATE_CODE, Constants.DATA_MODEL_STATE_CREATED);
        query.or().equalTo(FIELD_STATE_CODE, Constants.DATA_MODEL_STATE_REVIEW);
        List<DataModel> list = query.findAll();
        String[] ids = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            ids[i] = list.get(i).getUid();
        }
        return ids;
    }

    public boolean isStateCodeEqual(String uid, String stateCode) {
        RealmQuery query = this.realm.where(DataModel.class);
        query.equalTo(FIELD_UID, uid);
        DataModel dataModel = (DataModel) query.findFirst();
        return TextUtils.equals(dataModel.getStateCode(), stateCode);
    }

    public void countData() {
        Log.d(TAG, "countData");
        List<DataModel> data = getData();
        for (DataModel model: data) {
            Log.i(TAG,
                    "searchDate = " +  model.getSearchDate() + "\n" +
                            "date = " + model.getDate() + "\n" +
                            "prefix = " + model.getPrefix() + "\n" +
                            "prefixID = " + model.getPrefixID() + "\n" +
                            "name = " + model.getName() + "\n" +
                            "comment = " + model.getComment() + "\n" +
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
            Log.e(TAG, "countTree: getName " + tree.getName());
            Log.e(TAG, "countTree: getPrefix " + tree.getPrefix());
        }
    }

    public void setStateCode(String id, String stateCode) {
        Log.d(TAG, "setStateCode");
        RealmQuery dataQuery = this.realm.where(DataModel.class);
        dataQuery.equalTo(FIELD_UID, id);
        DataModel model;
        try {
            model = (DataModel) dataQuery.findFirst();
        } catch (NullPointerException e) {
            e.printStackTrace();
            Log.d(TAG, "getDataById: no data with such id");
            return;
        }
        this.realm.beginTransaction();
        model.setStateCode(stateCode);
        this.realm.copyToRealmOrUpdate(model);
        this.realm.commitTransaction();
        Log.d(TAG, "updateStateCode: updated " + stateCode);
    }
}