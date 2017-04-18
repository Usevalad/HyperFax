package com.vsevolod.swipe.addphoto.config;

import android.util.Log;

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
// FIXME: 10.04.17 open/close realm stuff. think about how to init
public class RealmHelper {
    private final String TAG = "RealmHelper";
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

    private void initRealm() {
        Log.d(TAG, "initRealmData");
        //data
        RealmQuery dataQuery = this.realm.where(DataModel.class);
        RealmResults<DataModel> dataResults = dataQuery.findAllSorted("searchDate", Sort.DESCENDING);
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

    public List<FlowsTreeModel> getTree() {
        Log.d(TAG, "getTree");
        return tree;
    }

    public List<DataModel> search(String queryString) {
        RealmQuery query = this.realm.where(DataModel.class);
        query.contains("searchDate", queryString, Case.INSENSITIVE); //INSENSITIVE TO UPPER/LOWER CASES
        query.or().contains("name", queryString, Case.INSENSITIVE);
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
        newModel.setViewDate(model.getViewDate());
        newModel.setLatitude(model.getLatitude());
        newModel.setLongitude(model.getLongitude());
        newModel.setComment(model.getComment());
        newModel.setStateCode(model.getStateCode());
        newModel.setPhotoURI(model.getPhotoURI());
        newModel.setName(model.getName());
        newModel.setServerPhotoURL(model.getServerPhotoURL());
//        newModel.setUid(model.getUid());

        this.realm.commitTransaction();
    }


    public DataModel getLastDataModel() {
        Log.d(TAG, "getLastDataModel");
        RealmQuery dataQuery = this.realm.where(DataModel.class);
        RealmResults<DataModel> models = dataQuery.findAllSorted("searchDate", Sort.DESCENDING);

        return models.first();
    }
}