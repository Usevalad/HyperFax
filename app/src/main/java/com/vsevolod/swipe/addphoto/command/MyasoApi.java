package com.vsevolod.swipe.addphoto.command;

import android.content.Intent;
import android.util.Log;

import com.vsevolod.swipe.addphoto.activity.LoginActivity;
import com.vsevolod.swipe.addphoto.activity.MainActivity;
import com.vsevolod.swipe.addphoto.asyncTask.TreeConverterTask;
import com.vsevolod.swipe.addphoto.config.Constants;
import com.vsevolod.swipe.addphoto.config.MyApplication;
import com.vsevolod.swipe.addphoto.config.PreferenceHelper;
import com.vsevolod.swipe.addphoto.model.query.AuthModel;
import com.vsevolod.swipe.addphoto.model.query.SimpleAuthModel;
import com.vsevolod.swipe.addphoto.model.query.TokenModel;
import com.vsevolod.swipe.addphoto.model.responce.CheckedInfo;
import com.vsevolod.swipe.addphoto.model.responce.ResponseFlowsTreeModel;
import com.vsevolod.swipe.addphoto.model.responce.UserModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Body;

/**
 * Created by vsevolod on 11.04.17.
 */

public class MyasoApi implements Api {
    private final String TAG = "MyasoApi";
    private PreferenceHelper mPreferenceHelper = new PreferenceHelper();

    public MyasoApi() {
    }

    @Override
    public void getTree(@Body TokenModel model) {
        Log.d(TAG, "getTree");
        MyApplication.getApi().getList(model).enqueue(new Callback<ResponseFlowsTreeModel>() {
            @Override
            public void onResponse(Call<ResponseFlowsTreeModel> call, Response<ResponseFlowsTreeModel> response) {
                Log.d(TAG, "onResponse");
                if (response.isSuccessful()) {
                    List<String> columns = response.body().getColumns();
                    List<List<String>> list = response.body().getList();
                    if (list != null) {
                        Log.d(TAG, list.toString());
                        Log.d(TAG, list.get(0).toString());

                        TreeConverterTask task = new TreeConverterTask();
                        task.execute(response.body());
                    }
                }
                Intent intent = new Intent(MyApplication.getAppContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                MyApplication.getAppContext().startActivity(intent);
            }

            @Override
            public void onFailure(Call<ResponseFlowsTreeModel> call, Throwable t) {

            }
        });
    }

    @Override
    public void authenticate(@Body AuthModel user) {
        Log.d(TAG, "authenticate");
        MyApplication.getApi().authenticate(user).enqueue(new Callback<UserModel>() {
            @Override
            public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                Log.d(TAG, "onResponse");
                if (response.body() != null) {
                    Log.d(TAG, "onResponse: body != null");
                    switch (response.body().getStatus()) {
                        case Constants.RESPONSE_STATUS_AUTH:
                            Log.e(TAG, "onResponse: need auth");
                            startLoginActivity();
                            break;
                        case Constants.RESPONSE_STATUS_PARAM:
                            Log.e(TAG, "onResponse: неверный набор параметров");
                            //empty token
                            startLoginActivity();
                            break;
                        case Constants.RESPONSE_STATUS_FAIL:
                            Log.e(TAG, "onResponse: сбой обработки задачи по внешней причине");
                            break;
                        case Constants.RESPONSE_STATUS_OK: // here i need to understand what was the
                                                            // call model to know how to react
                            Log.e(TAG, "onResponse: выполнено успешно, ождиается корректный " +
                                    "протокол выдачи конкретной задачи");
                            String token = response.body().getToken();
                            mPreferenceHelper.saveString(PreferenceHelper.APP_PREFERENCES_TOKEN, token);
                            startMainActivity();
                            break;
                        case Constants.RESPONSE_STATUS_BAD:
                            Log.e(TAG, "onResponse: задача не поддерживается сервером");
                            break;
                        case Constants.RESPONSE_STATUS_INIT:
                            Log.e(TAG, "onResponse: проблема на сервере или неверные JSON-данные в POST");
                            break;
                        case Constants.RESPONSE_STATUS_DIE:
                            Log.e(TAG, "onResponse: задача внезапно умерла при обработке");
                            break;
                        default:
                            Log.e(TAG, "onResponse: default");
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<UserModel> call, Throwable t) {

            }
        });
        Log.d(TAG, "authenticate");
    }

    @Override
    public void authenticate(@Body SimpleAuthModel user) {
        Log.d(TAG, "authenticate");
    }

    @Override
    public void verify(@Body TokenModel token) {
        Log.d(TAG, "verify");
        MyApplication.getApi().verify(new TokenModel("")).enqueue(new Callback<CheckedInfo>() {
            @Override
            public void onResponse(Call<CheckedInfo> call, Response<CheckedInfo> response) {
                Log.d(TAG, "onResponse");
                if (response.body() != null) {
                    Log.d(TAG, "onResponse: body != null");
                    switch (response.body().getStatus()) {
                        case Constants.RESPONSE_STATUS_AUTH:
                            Log.e(TAG, "onResponse: need auth");
                            startLoginActivity();
                            break;
                        case Constants.RESPONSE_STATUS_PARAM:
                            Log.e(TAG, "onResponse: неверный набор параметров");
                            //empty token
                            startLoginActivity();
                            break;
                        case Constants.RESPONSE_STATUS_FAIL:
                            Log.e(TAG, "onResponse: сбой обработки задачи по внешней причине");
                            break;
                        case Constants.RESPONSE_STATUS_OK:
                            Log.e(TAG, "onResponse: выполнено успешно, ождиается корректный " +
                                    "протокол выдачи конкретной задачи");
                            startMainActivity();
                            break;
                        case Constants.RESPONSE_STATUS_BAD:
                            Log.e(TAG, "onResponse: задача не поддерживается сервером");
                            break;
                        case Constants.RESPONSE_STATUS_INIT:
                            Log.e(TAG, "onResponse: проблема на сервере или неверные JSON-данные в POST");
                            break;
                        case Constants.RESPONSE_STATUS_DIE:
                            Log.e(TAG, "onResponse: задача внезапно умерла при обработке");
                            break;
                        default:
                            Log.e(TAG, "onResponse: default");
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<CheckedInfo> call, Throwable t) {
                Log.d(TAG, "onFailure");
            }
        });
    }

    private void startMainActivity() {
        Intent mainIntent = new Intent(MyApplication.getAppContext(), MainActivity.class);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        MyApplication.getAppContext().startActivity(mainIntent);
    }

    private void startLoginActivity() {
        Intent loginIntent = new Intent(MyApplication.getAppContext(), LoginActivity.class);
        loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        MyApplication.getAppContext().startActivity(loginIntent);
    }
}
