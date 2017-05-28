package com.vsevolod.swipe.addphoto.config;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vsevolod.swipe.addphoto.BuildConfig;
import com.vsevolod.swipe.addphoto.api.MyasoApi;

import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

//import android.support.multidex.MultiDex;

/**
 * Created by vsevolod on 13.03.17.
 */

public class MyApplication extends Application {
    private final static String TAG = "MyApplication";
    private static Context context;
    private static MyasoApi myasoApi;
    private Retrofit retrofit;
    private static String mVersionName;
    private static int mVersionCode;
    private static String mBuildDate;

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

//        if (LeakCanary.isInAnalyzerProcess(this)) {
//            // This process is dedicated to LeakCanary for heap analysis.
//            // You should not init your app in this process.
//            return;
//        }
//        LeakCanary.install(this);

        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date buildDate = new Date(BuildConfig.TIMESTAMP);
        mBuildDate = format.format(buildDate);
        mVersionName = pInfo.versionName;
        mVersionCode = pInfo.versionCode;

    }

    public static MyasoApi getApi() {
        Log.d(TAG, "getApi");
        return myasoApi;
    }

    public static Context getAppContext() {
        return MyApplication.context;
    }

//    @Override
//    protected void attachBaseContext(Context base) {
//        super.attachBaseContext(base);
//        MultiDex.install(this);//?
//    }


    public static String getVersionName() {
        return mVersionName;
    }

    public static int getVersionCode() {
        return mVersionCode;
    }

    public static String getBuildDate() {
        return mBuildDate;
    }
}
