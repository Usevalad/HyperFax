package com.unnamed.b.atv.sample;

import android.app.Application;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by vsevolod on 05.04.17.
 */

public class MyApplication extends Application {
    private static MyasoApi myasoApi;
    private Retrofit retrofit;

    @Override
    public void onCreate() {
        super.onCreate();

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        retrofit = new Retrofit.Builder()
                .baseUrl("http://crm.myaso.net.ua/ext/") //Базовая часть адреса
                .addConverterFactory(GsonConverterFactory.create(gson)) //Конвертер, необходимый для преобразования JSON'а в объекты
                .build();
        myasoApi = retrofit.create(MyasoApi.class); //Создаем объект, при помощи которого будем выполнять запросы
    }

    public static MyasoApi getApi() {
        return myasoApi;
    }
}
