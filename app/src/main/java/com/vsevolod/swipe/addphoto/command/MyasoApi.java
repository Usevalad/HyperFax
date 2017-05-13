package com.vsevolod.swipe.addphoto.command;

import android.content.Intent;
import android.util.Log;

import com.vsevolod.swipe.addphoto.activity.LoginActivity;
import com.vsevolod.swipe.addphoto.activity.MainActivity;
import com.vsevolod.swipe.addphoto.asyncTask.TreeConverterTask;
import com.vsevolod.swipe.addphoto.config.Constants;
import com.vsevolod.swipe.addphoto.config.MyApplication;
import com.vsevolod.swipe.addphoto.config.PreferenceHelper;
import com.vsevolod.swipe.addphoto.config.RealmHelper;
import com.vsevolod.swipe.addphoto.model.query.AuthModel;
import com.vsevolod.swipe.addphoto.model.query.CommitModel;
import com.vsevolod.swipe.addphoto.model.query.ListModel;
import com.vsevolod.swipe.addphoto.model.query.SimpleAuthModel;
import com.vsevolod.swipe.addphoto.model.query.TokenModel;
import com.vsevolod.swipe.addphoto.model.realm.DataModel;
import com.vsevolod.swipe.addphoto.model.responce.CheckedInfo;
import com.vsevolod.swipe.addphoto.model.responce.ListResponse;
import com.vsevolod.swipe.addphoto.model.responce.ResponseFlowsTreeModel;
import com.vsevolod.swipe.addphoto.model.responce.UserModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.Part;

/**
 * Created by vsevolod on 11.04.17.
 */

public class MyasoApi implements Api {
    // FIXME: 17.04.17 need rx
    // FIXME: 19.04.17 time outs no connection
    // FIXME: 21.04.17 response status checker
    // FIXME: 21.04.17 create new query call order
    // FIXME: 21.04.17 wrap queries in asyncTask
    private final String TAG = this.getClass().getSimpleName();
    private PreferenceHelper mPreferenceHelper = new PreferenceHelper();

    public MyasoApi() {
    }

    @Override
    public void getTree(@Body TokenModel model) {
        Log.d(TAG, "getTree");
        MyApplication.getApi().getTree(model).enqueue(new Callback<ResponseFlowsTreeModel>() {
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
                t.printStackTrace();
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
                            String token = response.body().getToken().toString();
                            mPreferenceHelper.saveString(PreferenceHelper.APP_PREFERENCES_TOKEN, token);
                            // FIXME: 15.04.17  token = not found
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
                t.printStackTrace();
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
        MyApplication.getApi().verify(token).enqueue(new Callback<CheckedInfo>() {
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
                t.printStackTrace();
            }
        });
    }

    @Override
    public void uploadImage(@Part("body") RequestBody body) {
        Log.d(TAG, "uploadImage");
        MyApplication.getApi().postImage(body).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.e(TAG, "response.code(): " + String.valueOf(response.code()));

                try {
                    RealmHelper mRealmHelper = new RealmHelper();
                    mRealmHelper.open();
                    String token = mPreferenceHelper.getToken(); // FIXME: 13.05.17 get token from account manager
                    String link = response.body().string().toString();
                    DataModel model = mRealmHelper.getLastDataModel();
                    String id = model.getUid();
                    mRealmHelper.updatePhotoURL(id, link);
                    mRealmHelper.setSynced(id, true);
                    CommitModel commitModel = new CommitModel(
                            token,
                            link,
                            model.getUid(),
                            model.getPrefixID(),
                            model.getComment(),
                            model.getSearchDate(),
                            String.valueOf(model.getLatitude() + "," + model.getLongitude())
                    );

                    commit(commitModel);
                    mRealmHelper.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    @Override
    public void commit(@Body CommitModel commitModel) {
        Log.d(TAG, "commit");
        MyApplication.getApi().commit(commitModel).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d(TAG, "onResponse");
                if (response.isSuccessful()) {
                    try {
                        Log.e(TAG, "response.body().string().toString(): " + response.body().string().toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        Log.e(TAG, "response.errorBody().string().toString(): " + response.errorBody().string().toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    @Override
    public void list(@Body ListModel listModel) {
        Log.d(TAG, "list");
        MyApplication.getApi().list(listModel).enqueue(new Callback<ListResponse>() {
            @Override
            public void onResponse(Call<ListResponse> call, Response<ListResponse> response) {
                if (response.isSuccessful()) {
                    Log.e(TAG, "onResponse: ");
                    List<String> ids = new ArrayList<String>();
                    List<String> states = new ArrayList<String>();
                    ids = response.body().getIds();
                    states = response.body().stateCodes();
                    Log.d(TAG, "onResponse: id = " + ids.get(0));
                    Log.d(TAG, "onResponse: state = " + states.get(0));

                    RealmHelper realmHelper = new RealmHelper();
                    realmHelper.open();
                    for (int i = 0; i < ids.size(); i++) {

                        realmHelper.updateStateCode(ids.get(i), states.get(i));
                    }


                    for (int i = 0; i < response.body().getList().size(); i++) {
                        for (int j = 0; j < response.body().getList().get(i).size(); j++) {
                            Log.e(TAG, response.body().getList().get(i).get(j));
                        }
                    }
                    realmHelper.close();
                    MainActivity.setRecyclerViewAdapter();
                }
            }

            @Override
            public void onFailure(Call<ListResponse> call, Throwable t) {
                Log.e(TAG, "onFailure");
                t.printStackTrace();
            }
        });
    }

    private void startMainActivity() {
        Log.d(TAG, "startMainActivity");
        Intent mainIntent = new Intent(MyApplication.getAppContext(), MainActivity.class);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        MyApplication.getAppContext().startActivity(mainIntent);
    }

    private void startLoginActivity() {
        Log.d(TAG, "startLoginActivity");
        Intent loginIntent = new Intent(MyApplication.getAppContext(), LoginActivity.class);
        loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        MyApplication.getAppContext().startActivity(loginIntent);
    }
}
