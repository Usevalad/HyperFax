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
import com.vsevolod.swipe.addphoto.constant.Constants;
import com.vsevolod.swipe.addphoto.util.MyDateUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
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

        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();

        Retrofit mRetrofit = new Retrofit.Builder()
                .addConverterFactory(ScalarsConverterFactory.create())
                .client(okHttpClient)
                //Конвертер, необходимый для преобразования JSON'а в объекты
                .addConverterFactory(GsonConverterFactory.create(gson))
                //Базовая часть адреса
                .baseUrl(Constants.BASE_URL)
                .build();

        //Создаем объект, при помощи которого будем выполнять запросы
        mMyasoApi = mRetrofit.create(MyasoApi.class);

        PackageInfo pInfo = null;

        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        Date buildDate = new Date(BuildConfig.TIMESTAMP);
        mBuildDate = MyDateUtil.searchDateFormat.format(buildDate);
        mAppVersionName = pInfo.versionName;
        mAppVersionCode = pInfo.versionCode;
        mBuildVersion = Build.VERSION.SDK_INT;


        DeviceName.with(MyApplication.getContext()).request(new DeviceName.Callback() {

            @Override
            public void onFinished(DeviceName.DeviceInfo info, Exception error) {
                String manufacturer = info.manufacturer;  // "Samsung"
                String marketName = info.marketName;      // "Galaxy S7 Edge"
                String model = info.model;                // "SAMSUNG-SM-G935A"
                String codename = info.codename;          // "hero2lte"
                String deviceName = info.getName();       // "Galaxy S7 Edge"
                mDeviceModel = manufacturer + " " + marketName;
            }
        });

    }

    public static MyasoApi getApi() {
        return mMyasoApi;
    }

    public static Context getContext() {
        return MyApplication.mContext;
    }

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

    public static String getAppVersionName() {
        return mAppVersionName;
    }

}