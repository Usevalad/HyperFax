package com.vsevolod.swipe.addphoto.config;

import android.util.Log;
import android.widget.Toast;

import com.vsevolod.swipe.addphoto.model.realm.DataModel;
import com.vsevolod.swipe.addphoto.model.realm.FlowsTreeModel;

import java.util.ArrayList;
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
    private Realm realm;
    public List<DataModel> data = new ArrayList<>();
    public List<FlowsTreeModel> tree = new ArrayList<>();

    public RealmHelper() {
        Log.d(TAG, "Realm constructor");
    }

    public void open() {
        Log.d(TAG, "open");
        Realm.init(MyApplication.getAppContext());
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder().build();
        Realm.setDefaultConfiguration(realmConfiguration);
        this.realm = Realm.getDefaultInstance();
        initRealm();
    }

    public void close() {
        Log.d(TAG, "close");
        this.realm.close();
    }

    public Realm getRealm() {
        return realm;
    }

    private void initRealm() {
        Log.d(TAG, "initRealmData");
        //data
        RealmQuery dataQuery = this.realm.where(DataModel.class);
        RealmResults<DataModel> dataResults = dataQuery.findAllSorted("date", Sort.DESCENDING);
        this.data = dataResults;
        //tree
        RealmQuery treeQuery = this.realm.where(FlowsTreeModel.class);
        RealmResults<FlowsTreeModel> treeResults = treeQuery.findAll();
        this.tree = treeResults;
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
        this.realm.beginTransaction();
        // Delete all matches
        results.deleteAllFromRealm();
        this.realm.commitTransaction();
    }


    public List<DataModel> getData() {
        Log.d(TAG, "getData");
        return this.data;
    }

    public boolean isValid(String text) {
        Log.d(TAG, "isValid");
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
        query.contains("searchDate", queryString, Case.INSENSITIVE); //INSENSITIVE TO UPPER/LOWER CASES
        query.or().contains("name", queryString, Case.INSENSITIVE);
        query.or().equalTo("name", queryString, Case.INSENSITIVE);
        query.or().beginsWith("prefix", queryString);
        return query.findAll();
    }

    public List<FlowsTreeModel> searchTree(String queryString) {
        RealmQuery query = this.realm.where(FlowsTreeModel.class);
        query.contains("name", queryString, Case.INSENSITIVE); //INSENSITIVE TO UPPER/LOWER CASES
        query.or().beginsWith("prefix", queryString);
        query.or().equalTo("prefix", queryString);
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
        RealmResults<DataModel> models = dataQuery.findAllSorted("date", Sort.DESCENDING);
        return models.first();
    }

    public String getPrefixID(String prefix) {
        Log.d(TAG, "getPrefixID");
        RealmQuery idQuery = this.realm.where(FlowsTreeModel.class);
        idQuery.equalTo("prefix", prefix, Case.INSENSITIVE);
        FlowsTreeModel model;
        try {
            model = (FlowsTreeModel) idQuery.findFirst();
            return model.getId();
        } catch (NullPointerException e) {
            e.printStackTrace();
            Toast.makeText(MyApplication.getAppContext(), "Can't find prefix", Toast.LENGTH_SHORT).show();
            return "no id";
        }
    }

    public void updateStateCode(String id, String stateCode) {
        Log.d(TAG, "updateStateCode");

        RealmQuery dataQuery = this.realm.where(DataModel.class);
        dataQuery.equalTo("uid", id);
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

    public void updatePhotoURL(String id, String imageUrl) {
        Log.d(TAG, "updateServerPhotoURL");

        RealmQuery dataQuery = this.realm.where(DataModel.class);
        dataQuery.equalTo("uid", id);
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
        dataQuery.equalTo("uid", id);
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


    public List<String> dataQueue() {
        Log.d(TAG, "dataQueue");
        List<String> ids = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            String stateCode = data.get(i).getStateCode();
            if (stateCode.equals(Constants.STATE_CODE_CREATED) ||
                    stateCode.equals(Constants.STATE_CODE_REVIEW)) {
                ids.add(data.get(i).getUid());
            }
        }
        return ids;
    }

    public void countData() {
        Log.d(TAG, "countData" + "data.size() is " + data.size());
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
    }
}