package com.vsevolod.swipe.addphoto.asyncTask;

import android.os.AsyncTask;
import android.util.Log;

import com.vsevolod.swipe.addphoto.config.Constants;
import com.vsevolod.swipe.addphoto.config.MyApplication;
import com.vsevolod.swipe.addphoto.model.query.AuthModel;
import com.vsevolod.swipe.addphoto.model.responce.UserModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by vsevolod on 10.05.17.
 */

public class AuthTask extends AsyncTask<AuthModel, Void, Void> {
    private final String TAG = AuthTask.class.getSimpleName();

    @Override
    protected Void doInBackground(AuthModel... params) {
        AuthModel user = params[0];
        MyApplication.getApi().authenticate(user).enqueue(new Callback<UserModel>() {
            @Override
            public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                Log.d(TAG, "onResponse");
                if (response.body() != null) {
                    Log.d(TAG, "onResponse: body != null");
                    switch (response.body().getStatus()) {
                        case Constants.RESPONSE_STATUS_AUTH:
                            Log.e(TAG, "onResponse: need auth");
//                            startLoginActivity();
                            break;
                        case Constants.RESPONSE_STATUS_PARAM:
                            Log.e(TAG, "onResponse: неверный набор параметров");
                            //empty token
//                            startLoginActivity();
                            break;
                        case Constants.RESPONSE_STATUS_FAIL:
                            Log.e(TAG, "onResponse: сбой обработки задачи по внешней причине");
                            break;
                        case Constants.RESPONSE_STATUS_OK: // here i need to understand what was the
                            // call model to know how to react
                            Log.e(TAG, "onResponse: выполнено успешно, ождиается корректный " +
                                    "протокол выдачи конкретной задачи");
                            String token = response.body().getToken().toString();
//                            mPreferenceHelper.saveString(PreferenceHelper.APP_PREFERENCES_TOKEN, token);
                            // FIXME: 15.04.17  token = not found
//                            startMainActivity();
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
                t.printStackTrace();
            }
        });
        Log.d(TAG, "authenticate");


        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }
}
