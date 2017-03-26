package com.vsevolod.swipe.addphoto;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by vsevolod on 13.03.17.
 */

public class MyApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
//        RealmConfiguration config = new
//                Builder(getBaseContext()).build();
//        Realm.setDefaultConfiguration(config);
        Realm.init(getApplicationContext());
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder().build();
        Realm.setDefaultConfiguration(realmConfiguration);

    }
}
