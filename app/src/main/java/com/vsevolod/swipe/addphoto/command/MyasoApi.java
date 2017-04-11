package com.vsevolod.swipe.addphoto.command;

import android.content.Intent;
import android.util.Log;

import com.vsevolod.swipe.addphoto.activity.MainActivity;
import com.vsevolod.swipe.addphoto.asyncTask.TreeConverterTask;
import com.vsevolod.swipe.addphoto.config.Constants;
import com.vsevolod.swipe.addphoto.config.MyApplication;
import com.vsevolod.swipe.addphoto.config.PreferenceHelper;
import com.vsevolod.swipe.addphoto.model.query.AuthModel;
import com.vsevolod.swipe.addphoto.model.query.SimpleAuthModel;
import com.vsevolod.swipe.addphoto.model.query.TokenModel;
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

                    Log.d(TAG, list.toString());
                    Log.d(TAG, list.get(0).toString());

                    TreeConverterTask task = new TreeConverterTask();
                    task.execute(response.body());
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
                //Данные успешно пришли, но надо проверить response.body() на null
                Log.e(TAG, "onResponse");
                Log.e(TAG, String.valueOf(response.code()));
                UserModel user = null;
                if (response.isSuccessful()) {
                    Log.e(TAG, "onResponse: body != null");
                    Log.e(TAG, response.body().toString());
                    user = response.body();
                    Log.e(TAG, user.getActive());
                    Log.e(TAG, user.getStatus());
                    Log.e(TAG, user.getToken());
                }

                mPreferenceHelper.saveString(PreferenceHelper.APP_PREFERENCES_TOKEN, user.getToken());

                switch (user.getStatus()) {
                    case Constants.RESPONSE_STATUS_OK:
//                                Intent intent = new Intent(mContext, MainActivity.class);
//                                startActivity(intent);
                        break;
                    case Constants.RESPONSE_STATUS_BAD:
                        Log.e(TAG, "onResponse: BAD");
                        break;
                    case Constants.RESPONSE_STATUS_INIT:
                        Log.e(TAG, "onResponse: INIT");
                        break;
                    case Constants.RESPONSE_STATUS_DIE:
                        Log.e(TAG, "onResponse: DIE");
                        break;
                    default:
                        break;
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
    public void verify(@Body TokenModel user) {
        Log.d(TAG, "verify");
    }
}
