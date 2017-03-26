package com.vsevolod.swipe.addphoto.config;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.vsevolod.swipe.addphoto.Model;

import java.util.ArrayList;
import java.util.List;

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
    public List<Model> data = new ArrayList<>();

    public RealmHelper(Context context) {
        Log.d(TAG, "Realm constructor");
        this.realm = Realm.getDefaultInstance();
        this.context = context;
        initRealmData();
    }

    private void initRealmData() {
        Log.d(TAG, "initRealmData");
        //db
        RealmQuery query = this.realm.where(Model.class);
        RealmResults<Model> results = query.findAllSorted("date", Sort.DESCENDING);
        this.data = results;
    }

    public void dropRealm() {
        Log.d(TAG, "dropRealm");

        RealmResults<Model> results = this.realm.where(Model.class).findAll();
        // All changes to data must happen in a transaction
        this.realm.beginTransaction();
        // Delete all matches
        results.deleteAllFromRealm();
        this.realm.commitTransaction();

        Toast.makeText(this.context, "Данные удалены", Toast.LENGTH_SHORT).show();
    }

    public void saveToRealm(Model model) {
        Log.d(TAG, "saveToRealm");

        this.realm.beginTransaction();
        // Create an object
        Model newModel = this.realm.createObject(Model.class);

        // Set its fields
        newModel.setDate(model.getDate());
        newModel.setPath(model.getPath());
        newModel.setPhoto(model.getPhoto());
        newModel.setPhotoURI(model.getPhotoURI());

        this.realm.commitTransaction();
    }

    public List<Model> getData() {
        Log.d(TAG, "getData");
        return this.data;
    }
}
