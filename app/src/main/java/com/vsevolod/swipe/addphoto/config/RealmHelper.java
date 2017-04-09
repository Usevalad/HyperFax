package com.vsevolod.swipe.addphoto.config;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.vsevolod.swipe.addphoto.model.realm.DataModel;
import com.vsevolod.swipe.addphoto.model.realm.FlowsTreeModel;

import java.util.ArrayList;
import java.util.List;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by vsevolod on 26.03.17.
 */

public class RealmHelper {
    private final String TAG = "RealmHelper";
    private Context context;
    private Realm realm;
    public List<DataModel> data = new ArrayList<>();
    public List<FlowsTreeModel> tree = new ArrayList<>();

    public RealmHelper(Context context) {
        Log.d(TAG, "Realm constructor");
        this.realm = Realm.getDefaultInstance();
        this.context = context;
        initRealmData();
    }

    private void initRealmData() {
        Log.d(TAG, "initRealmData");
        //db
        RealmQuery query = this.realm.where(DataModel.class);
        RealmResults<DataModel> results = query.findAllSorted("date", Sort.DESCENDING);
        this.data = results;
    }

    public void dropRealm() {
        Log.d(TAG, "dropRealm");

        RealmResults<DataModel> results = this.realm.where(DataModel.class).findAll();
        // All changes to data must happen in a transaction
        this.realm.beginTransaction();
        // Delete all matches
        results.deleteAllFromRealm();
        this.realm.commitTransaction();

        Toast.makeText(this.context, "Данные удалены", Toast.LENGTH_SHORT).show();
    }

    public void saveToRealm(DataModel model) {
        Log.d(TAG, "saveToRealm");

        this.realm.beginTransaction();
        // Create an object
        DataModel newModel = this.realm.createObject(DataModel.class);
        // Set its fields
        newModel.setDate(model.getDate());
        newModel.setPath(model.getPath());
        newModel.setPhoto(model.getPhoto());
        newModel.setPhotoURI(model.getPhotoURI());

        this.realm.commitTransaction();
    }

    public List<DataModel> getData() {
        Log.d(TAG, "getData");
        return this.data;
    }

    public List<String> getAllDates() {
        Log.d(TAG, "getAllDates");
        List<String> dates = new ArrayList<>();
        for (int i = 0; i < this.data.size(); i++) {
            dates.add(data.get(i).getDate());
        }
        return dates;
    }

    public List<String> getAllPaths() {
        Log.d(TAG, "getAllPaths");
        List<String> paths = new ArrayList<>();
        for (int i = 0; i < this.data.size(); i++) {
            paths.add(data.get(i).getPath());
        }
        return paths;
    }

    public List<DataModel> getSearchResults(String queryString) {
        RealmQuery query = this.realm.where(DataModel.class);
        query.contains("date", queryString, Case.INSENSITIVE); //INSENSITIVE TO UPPER/LOWER CASES
        query.or().contains("path", queryString);
        RealmResults<DataModel> results = query.findAll();

        return results;
    }
}
