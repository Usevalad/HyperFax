package com.vsevolod.swipe.addphoto.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.vsevolod.swipe.addphoto.R;
import com.vsevolod.swipe.addphoto.asyncTask.TreeConverterTask;
import com.vsevolod.swipe.addphoto.config.Constants;
import com.vsevolod.swipe.addphoto.config.MyApplication;
import com.vsevolod.swipe.addphoto.config.PreferenceHelper;
import com.vsevolod.swipe.addphoto.model.query.AuthModel;
import com.vsevolod.swipe.addphoto.model.query.TokenModel;
import com.vsevolod.swipe.addphoto.model.realm.FlowsTreeModel;
import com.vsevolod.swipe.addphoto.model.responce.ResponseFlowsTreeModel;
import com.vsevolod.swipe.addphoto.model.responce.UserModel;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StartActivity extends AppCompatActivity {
    private final String TAG = "StartActivity";
    private Context mContext;
    private PreferenceHelper mPreferenceHelper;
    public static List<FlowsTreeModel> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        mContext = this;
        mPreferenceHelper = new PreferenceHelper(this);
        getServerAccess();
        getTree();
    }

    private void getServerAccess() {
        MyApplication.getApi().authenticate(new AuthModel("+380506361408", "admin"))
                .enqueue(new Callback<UserModel>() {
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
                                Toast.makeText(mContext, "BAD!!!", Toast.LENGTH_SHORT).show();
                                break;
                            case Constants.RESPONSE_STATUS_INIT:
                                Toast.makeText(mContext, "INIT!!!", Toast.LENGTH_SHORT).show();
                                break;
                            case Constants.RESPONSE_STATUS_DIE:
                                Toast.makeText(mContext, "DIE!!!", Toast.LENGTH_SHORT).show();
                                break;
                            default:
                                break;
                        }
                    }

                    @Override
                    public void onFailure(Call<UserModel> call, Throwable t) {
                        Log.wtf(TAG, "onFailure", t);
                        //Произошла ошибка
                    }
                });
    }


    private void getTree() {
        String token = mPreferenceHelper.getToken();
        MyApplication.getApi().getList(new TokenModel(token)).enqueue(new Callback<ResponseFlowsTreeModel>() {
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

                Intent intent = new Intent(mContext, MainActivity.class);
                startActivity(intent);
            }

            @Override
            public void onFailure(Call<ResponseFlowsTreeModel> call, Throwable t) {

            }
        });
    }
}