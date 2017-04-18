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
// FIXME: 18.04.17 improve open/close by boolean
public class RealmHelper {
    private final String TAG = "RealmHelper";
    private Realm realm;
    public List<DataModel> data = new ArrayList<>();
    public List<FlowsTreeModel> tree = new ArrayList<>();
    boolean isOpen = false;

    public RealmHelper() {
        open();
        Log.d(TAG, "Realm constructor");
    }

    private void open() {
        Log.d(TAG, "open");
        Realm.init(MyApplication.getAppContext());
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder().build();
        Realm.setDefaultConfiguration(realmConfiguration);
        this.realm = Realm.getDefaultInstance();
        initRealm();
    }

    private void close() {
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
        open();

        RealmResults<DataModel> results = this.realm.where(DataModel.class).findAll();
        // All changes to data must happen in a transaction
        this.realm.beginTransaction();
        // Delete all matches
        results.deleteAllFromRealm();
        this.realm.commitTransaction();
        close();
    }

    public void dropRealmTree() {
        Log.d(TAG, "dropRealmTree");
        open();

        RealmResults<FlowsTreeModel> results = this.realm.where(FlowsTreeModel.class).findAll();
        // All changes to data must happen in a transaction
        this.realm.beginTransaction();
        // Delete all matches
        results.deleteAllFromRealm();
        this.realm.commitTransaction();
        close();
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
        open();
        RealmQuery query = this.realm.where(DataModel.class);
        query.contains("searchDate", queryString, Case.INSENSITIVE); //INSENSITIVE TO UPPER/LOWER CASES
        query.or().contains("name", queryString, Case.INSENSITIVE);
        query.or().beginsWith("prefix", queryString);
        close();
        return query.findAll();
    }

    public List<FlowsTreeModel> searchTree(String queryString) {
        open();
        RealmQuery query = this.realm.where(FlowsTreeModel.class);
        query.contains("name", queryString, Case.INSENSITIVE); //INSENSITIVE TO UPPER/LOWER CASES
        query.or().beginsWith("prefix", queryString);
        query.or().equalTo("prefix", queryString);
        close();
        return query.findAll();
    }

    public void save(List<FlowsTreeModel> flowsTreeModels) {
        Log.d(TAG, "saveTreeList");
        open();
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
        close();
    }

    public void save(FlowsTreeModel model) {
        Log.d(TAG, "saveTreeListModel");
        open();
        this.realm.beginTransaction();
        // Create an object
        FlowsTreeModel newModel = this.realm.createObject(FlowsTreeModel.class);
        // Set its fields
        newModel.setId(model.getId());
        newModel.setName(model.getName());
        newModel.setParentId(model.getParentId());
        newModel.setPrefix(model.getPrefix());

        this.realm.commitTransaction();
        close();
    }

    public void save(DataModel model) {
        Log.d(TAG, "saveDataModel");
        open();
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

        this.realm.commitTransaction();
        close();
    }


    public DataModel getLastDataModel() {
        Log.d(TAG, "getLastDataModel");
        open();
        RealmQuery dataQuery = this.realm.where(DataModel.class);
        RealmResults<DataModel> models = dataQuery.findAllSorted("searchDate", Sort.DESCENDING);
        close();
        return models.first();
    }
}