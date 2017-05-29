package com.vsevolod.swipe.addphoto.config;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jaredrummler.android.device.DeviceName;
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
    private static String mHyperFaxVersionName;
    private static int mHyperFaxVersionCode;
    private static String mBuildDate;
    private static int mAndroidVersion;
    private static String mAndroidVersionRelease;
    private static String mAndroidModel;

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
        mHyperFaxVersionName = pInfo.versionName;
        mHyperFaxVersionCode = pInfo.versionCode;
        mAndroidVersion = Build.VERSION.SDK_INT;


        DeviceName.with(MyApplication.getAppContext()).request(new DeviceName.Callback() {

            @Override
            public void onFinished(DeviceName.DeviceInfo info, Exception error) {
                String manufacturer = info.manufacturer;  // "Samsung"
                String marketName = info.marketName;            // "Galaxy S7 Edge"
                String model = info.model;                // "SAMSUNG-SM-G935A"
                String codename = info.codename;          // "hero2lte"
                String deviceName = info.getName();       // "Galaxy S7 Edge"
                // FYI: We are on the UI thread.
                mAndroidModel = manufacturer + " " + marketName;
            }
        });

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

    public static int getVersionCode() {
        return mHyperFaxVersionCode;
    }

    public static String getBuildDate() {
        return mBuildDate;
    }

    public static int getAndroidVersion() {
        return mAndroidVersion;
    }

    public static String getAndroidModel() {
        return mAndroidModel;
    }
}
