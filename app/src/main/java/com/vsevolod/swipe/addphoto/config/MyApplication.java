package com.vsevolod.swipe.addphoto.config;

import android.app.Application;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vsevolod.swipe.addphoto.api.MyasoApi;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by vsevolod on 13.03.17.
 */

public class MyApplication extends Application {
    private final static String TAG = "MyApplication";
    private static MyasoApi myasoApi;
    private Retrofit retrofit;

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        super.onCreate();
        Realm.init(getApplicationContext());
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder().build();
        Realm.setDefaultConfiguration(realmConfiguration);
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        retrofit = new Retrofit.Builder()
                .addConverterFactory(ScalarsConverterFactory.create())
                //Конвертер, необходимый для преобразования JSON'а в объекты
                .addConverterFactory(GsonConverterFactory.create(gson))
                //Базовая часть адреса
                .baseUrl("http://crm.myaso.net.ua/ext/")
                .build();

        //Создаем объект, при помощи которого будем выполнять запросы
        myasoApi = retrofit.create(MyasoApi.class);
    }

    public static MyasoApi getApi() {
        Log.d(TAG, "getApi");
        return myasoApi;
    }
}
