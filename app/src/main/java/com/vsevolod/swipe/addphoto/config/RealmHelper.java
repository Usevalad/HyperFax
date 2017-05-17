package com.vsevolod.swipe.addphoto.config;

import android.content.Context;
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
    private Context mContext;
    private final String TAG = this.getClass().getSimpleName();
    private final String FIELD_DATE = "date";
    private final String FIELD_SEARCH_DATE = "searchDate";
    private final String FIELD_NAME = "name";
    private final String FIELD_PREFIX = "prefix";
    private final String FIELD_UID = "uid";
    private final String FIELD_IS_SYNCED = "isSynced";
    private final String FIELD_STATE_CODE = "stateCode";
    private Realm realm;

    public RealmHelper(Context context) {
        this.mContext = context;
        Log.d(TAG, "Realm constructor");
    }

    public void open() {
        Log.d(TAG, "open");
        Realm.init(mContext);
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


    public List<DataModel> getData() {
        Log.d(TAG, "getData");
        RealmQuery dataQuery = this.realm.where(DataModel.class);
        return dataQuery.findAllSorted(FIELD_DATE, Sort.DESCENDING);
    }

    public List<FlowsTreeModel> getTree() {
        RealmQuery treeQuery = this.realm.where(FlowsTreeModel.class);
        return treeQuery.findAll();
    }

    public boolean isValid(String text) {
        Log.d(TAG, "isValid");
        List<FlowsTreeModel> tree = getTree();
        String tmp;

        for (int i = 0; i < tree.size(); i++) {
            tmp = tree.get(i).getName() + " @" + tree.get(i).getPrefix();
            if (tmp.equals(text)) {
                return true;
            }
        }

        return false;
    }

    public List<DataModel> search(String queryString) {
        RealmQuery query = this.realm.where(DataModel.class);
        query.contains(FIELD_SEARCH_DATE, queryString, Case.INSENSITIVE); //INSENSITIVE TO UPPER/LOWER CASES
        query.or().contains(FIELD_NAME, queryString, Case.INSENSITIVE);
        query.or().equalTo(FIELD_NAME, queryString, Case.INSENSITIVE);
        query.or().beginsWith(FIELD_PREFIX, queryString);
        return query.findAll();
    }

    public List<FlowsTreeModel> searchTree(String queryString) {
        RealmQuery query = this.realm.where(FlowsTreeModel.class);
        query.contains(FIELD_NAME, queryString, Case.INSENSITIVE); //INSENSITIVE TO UPPER/LOWER CASES
        query.or().beginsWith(FIELD_PREFIX, queryString);
        query.or().equalTo(FIELD_PREFIX, queryString);
        return query.findAll();
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
        newModel.setPhotoURL(model.getPhotoURL());
        newModel.setName(model.getName());
        newModel.setPrefixID(model.getPrefixID());
        newModel.setDate(model.getDate());

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

    public void setPhotoURL(String id, String imageUrl) {
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
        model.setPhotoURL(imageUrl);
        this.realm.copyToRealmOrUpdate(model);
        this.realm.commitTransaction();
        Log.d(TAG, "updateServerPhotoURL: updated " + imageUrl);
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

    public List<DataModel> getNotSyncedData() {
        Log.d(TAG, "getNotSynced");
        RealmQuery query = this.realm.where(DataModel.class);
        query.equalTo(FIELD_IS_SYNCED, false);
        return query.findAll();
    }

    public String[] getNotSyncedDataStatesIds() {
        Log.d(TAG, "getNotFinishedStates:");
        RealmQuery query = this.realm.where(DataModel.class);
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
        for (int i = 0; i < data.size(); i++) {
            String searchDate = data.get(i).getSearchDate();
            String prefix = data.get(i).getPrefix();
            String name = data.get(i).getName();
            String photoUri = data.get(i).getPhotoURL();
            String comment = data.get(i).getComment();
            String latitude = String.valueOf(data.get(i).getLatitude());
            String longitude = String.valueOf(data.get(i).getLongitude());
            String stateCode = data.get(i).getStateCode();
            String photoArrayLength = String.valueOf(data.get(i).getPhoto().length);
            String prefixID = data.get(i).getPrefixID();
            String date = String.valueOf(data.get(i).getDate());
            String isSync = String.valueOf(data.get(i).isSynced());
            Log.i(TAG,
                    "searchDate = " + searchDate + "\n" +
                            "date = " + date + "\n" +
                            "prefix = " + prefix + "\n" +
                            "prefixID = " + prefixID + "\n" +
                            "name = " + name + "\n" +
                            "comment = " + comment + "\n" +
                            "photoUri = " + photoUri + "\n" +
                            "photoArrayLength = " + photoArrayLength + "\n" +
                            "latitude = " + latitude + "\n" +
                            "longitude = " + longitude + "\n" +
                            "stateCode = " + stateCode + "\n" +
                            "isSynced = " + isSync);
        }
        Log.i(TAG, "countData: data.size is " + data.size());
        Log.i(TAG, "countData: not synced size is  " + getNotSyncedData().size());
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