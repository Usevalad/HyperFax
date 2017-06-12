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

/**
 * Created by vsevolod on 13.03.17.
 */

public class MyApplication extends Application {
    private final static String TAG = "MyApplication";
    private static Context mContext;
    private static MyasoApi mMyasoApi;
    private Retrofit mRetrofit;
    private static String mAppVersionName;
    private static int mAppVersionCode;
    private static String mBuildDate;
    private static int mBuildVersion;
    private static String mAndroidVersionRelease;
    private static String mDeviceModel;

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        super.onCreate();
        MyApplication.mContext = getApplicationContext();
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        mRetrofit = new Retrofit.Builder()
                .addConverterFactory(ScalarsConverterFactory.create())
                //Конвертер, необходимый для преобразования JSON'а в объекты
                .addConverterFactory(GsonConverterFactory.create(gson))
                //Базовая часть адреса
                .baseUrl(Constants.BASE_URL)
                .build();

        //Создаем объект, при помощи которого будем выполнять запросы
        mMyasoApi = mRetrofit.create(MyasoApi.class);

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
        mAppVersionName = pInfo.versionName;
        mAppVersionCode = pInfo.versionCode;
        mBuildVersion = Build.VERSION.SDK_INT;


        DeviceName.with(MyApplication.getAppContext()).request(new DeviceName.Callback() {

            @Override
            public void onFinished(DeviceName.DeviceInfo info, Exception error) {
                String manufacturer = info.manufacturer;  // "Samsung"
                String marketName = info.marketName;            // "Galaxy S7 Edge"
                String model = info.model;                // "SAMSUNG-SM-G935A"
                String codename = info.codename;          // "hero2lte"
                String deviceName = info.getName();       // "Galaxy S7 Edge"
                mDeviceModel = manufacturer + " " + marketName;
            }
        });

    }

    public static MyasoApi getApi() {
        Log.d(TAG, "getApi");
        return mMyasoApi;
    }

    public static Context getAppContext() {
        return MyApplication.mContext;
    }

//    @Override
//    protected void attachBaseContext(Context base) {
//        super.attachBaseContext(base);
//        MultiDex.install(this);//?
//    }

    public static int getAppVersionCode() {
        return mAppVersionCode;
    }

    public static String getBuildDate() {
        return mBuildDate;
    }

    public static int getBuildVersion() {
        return mBuildVersion;
    }

    public static String getDeviceModel() {
        return mDeviceModel;
    }
}
