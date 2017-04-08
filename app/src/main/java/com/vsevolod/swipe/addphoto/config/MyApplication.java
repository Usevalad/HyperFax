package com.vsevolod.swipe.addphoto.config;

import android.app.Application;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vsevolod.swipe.addphoto.api.MyasoApi;
import com.vsevolod.swipe.addphoto.model.query.AuthModel;
import com.vsevolod.swipe.addphoto.model.answer.UserModel;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by vsevolod on 13.03.17.
 */

public class MyApplication extends Application {
    private final String TAG = "MyApplication";
    private static MyasoApi myasoApi;
    private Retrofit retrofit;

    @Override
    public void onCreate() {
        super.onCreate();
//        RealmConfiguration config = new
//                Builder(getBaseContext()).build();
//        Realm.setDefaultConfiguration(config);
        Realm.init(getApplicationContext());
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder().build();
        Realm.setDefaultConfiguration(realmConfiguration);
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        retrofit = new Retrofit.Builder()
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson)) //Конвертер, необходимый для преобразования JSON'а в объекты
                .baseUrl("http://crm.myaso.net.ua/ext/") //Базовая часть адреса
                .build();

        myasoApi = retrofit.create(MyasoApi.class); //Создаем объект, при помощи которого будем выполнять запросы

        myasoApi.authenticate(new AuthModel("+380506361408", "admin"))
                .enqueue(new Callback<UserModel>() {
                    @Override
                    public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                        //Данные успешно пришли, но надо проверить response.body() на null
                        Log.e(TAG, "onResponse");
                        Log.e(TAG, String.valueOf(response.code()));

                        if (response.isSuccessful()) {
                            Log.e(TAG, "onResponse: body != null");
                            Log.e(TAG, response.body().toString());
                            UserModel user = response.body();
                            Log.e(TAG, user.getActive());
                            Log.e(TAG, user.getStatus());
                            Log.e(TAG, user.getToken());
                        }
                    }

                    @Override
                    public void onFailure(Call<UserModel> call, Throwable t) {
                        Log.wtf(TAG, "onFailure", t);
                        //Произошла ошибка
                    }
                });

    }


    public static MyasoApi getApi() {
        return myasoApi;
    }
}
