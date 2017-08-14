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
 * Created by vsevolod on 26.03.17.
 */

public class RealmHelper {
    private final String TAG = this.getClass().getSimpleName();
    private final String DATE = "date";
    private final String SEARCH_ARTICLE = "searchArticle";
    private final String TREE_SEARCH_NAME = "searchName";
    private final String PREFIX = "prefix";
    private final String UID = "uid";
    public final String COMMENT = "searchComment";
    public final String SERVER_PHOTO_URL = "serverPhotoURL";
    public final String IS_SYNCED = "isSynced";
    public final String STATE_CODE = "stateCOde";
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
        // Create an object// FIXME: 8/11/1 mRealm.copyToRealmOrUpdate(model);
        FlowsTreeModel newModel = this.mRealm.createObject(FlowsTreeModel.class);
        // Set its fields
        newModel.setId(model.getId());
        newModel.setViewName(model.getViewName());
        newModel.setSearchName(model.getSearchName());
        newModel.setParentId(model.getParentId());
        newModel.setPrefix(model.getPrefix());

        this.mRealm.commitTransaction();
    }

    public void save(DataModel model) {//// FIXME: 8/11/1 mRealm.copyToRealmOrUpdate(model);
        Log.d(TAG, "saveDataModel");
        this.mRealm.beginTransaction();
        // Create an object
        DataModel newModel = this.mRealm.createObject(DataModel.class, UUID.randomUUID().toString());
        // Set its fields
        newModel.setSearchDate(model.getSearchDate());
        newModel.setPrefix(model.getPrefix());
        newModel.setPhoto(model.getPhoto());
        newModel.setLatitude(model.getLatitude());
        newModel.setLongitude(model.getLongitude());
        newModel.setViewDescription(model.getViewDescription());
        newModel.setSearchDescription(model.getSearchDescription());
        newModel.setStateCode(model.getStateCode());
        newModel.setStoragePhotoURL(model.getStoragePhotoURL());
        newModel.setViewArticle(model.getViewArticle());
        newModel.setSearchArticle(model.getSearchArticle());
        newModel.setPrefixID(model.getPrefixID());
        newModel.setDate(model.getDate());
        newModel.setViewDate(model.getViewDate());

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

    /**
     * @param id        id of post to update
     * @param fieldName name of field to update (serverPhotoUrl, isSynced, comment, etc)
     * @param value     new field value to update (if field name is 'isSynced' - can be null)
     * @param isSynced  new isSynced value (if field name not 'isSynced' - never used)
     */

    public void setField(String id, String fieldName, String value, boolean isSynced) {
        Log.d(TAG, "setField");
// FIXME: 8/11/17 refactor
        RealmQuery dataQuery = this.mRealm.where(DataModel.class);
        dataQuery.equalTo(UID, id);
        DataModel model;
        try {
            model = (DataModel) dataQuery.findFirst();
        } catch (NullPointerException e) {
            e.printStackTrace();
            Log.d(TAG, "getDataById: no data with such id");
            return;
        }

        this.mRealm.beginTransaction();
        switch (fieldName) {
            case SERVER_PHOTO_URL:
                model.setServerPhotoURL(value);
                break;
            case COMMENT:
                model.setViewComment(value);
                model.setSearchComment(value.toLowerCase());
                break;
            case IS_SYNCED:
                model.setSynced(isSynced);
                break;
            case STATE_CODE:
                model.setStateCode(value);
                break;
            default:
                break;
        }
        this.mRealm.copyToRealmOrUpdate(model);
        this.mRealm.commitTransaction();
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