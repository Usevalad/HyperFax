package com.vsevolod.swipe.addphoto.config;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.leakcanary.LeakCanary;
import com.vsevolod.swipe.addphoto.api.MyasoApi;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by vsevolod on 13.03.17.
 */

public class MyApplication extends Application {
    private final static String TAG = "MyApplication";
    private static Context context;
    private static MyasoApi myasoApi;
    private Retrofit retrofit;

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        super.onCreate();
        MyApplication.context = getApplicationContext();
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        retrofit = new Retrofit.Builder()
                .addConverterFactory(ScalarsConverterFactory.create())
                //Конвертер, необходимый для преобразования JSON'а в объекты
                .addConverterFactory(GsonConverterFactory.create(gson))
                //Базовая часть адреса
                .baseUrl(Constants.BASE_URL)
                .build();

        //Создаем объект, при помощи которого будем выполнять запросы
        myasoApi = retrofit.create(MyasoApi.class);

        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
    }

    public static MyasoApi getApi() {
        Log.d(TAG, "getApi");
        return myasoApi;
    }

    public static Context getAppContext() {
        return MyApplication.context;
    }
}
