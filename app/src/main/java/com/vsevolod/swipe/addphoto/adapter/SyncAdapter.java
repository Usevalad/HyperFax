package com.vsevolod.swipe.addphoto.adapter;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

import com.vsevolod.swipe.addphoto.accountAuthenticator.AccountGeneral;
import com.vsevolod.swipe.addphoto.config.Constants;
import com.vsevolod.swipe.addphoto.config.MyApplication;
import com.vsevolod.swipe.addphoto.config.RealmHelper;
import com.vsevolod.swipe.addphoto.model.query.CommitModel;
import com.vsevolod.swipe.addphoto.model.query.ListModel;
import com.vsevolod.swipe.addphoto.model.realm.DataModel;
import com.vsevolod.swipe.addphoto.model.responce.ListResponse;

import java.io.File;
import java.io.IOException;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * Created by vsevolod on 13.05.17.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {
    private final String TAG = this.getClass().getSimpleName();
    private AccountManager mAccountManager;
    private RealmHelper mRealmHelper;

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mAccountManager = AccountManager.get(context);
        mRealmHelper = new RealmHelper();
        Log.d(TAG, "SyncAdapter: constructor");
    }

    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        mAccountManager = AccountManager.get(context);
        mRealmHelper = new RealmHelper();
        Log.d(TAG, "SyncAdapter: constructor 2");
    }


    @Override
    public void onPerformSync(Account account, Bundle extras,
                              String authority, ContentProviderClient provider,
                              SyncResult syncResult) {
        Log.d(TAG, "onPerformSync");
        mRealmHelper.open();
        List<DataModel> dataModels = mRealmHelper.getNotSyncedData();
        Log.d(TAG, "onPerformSync: dataModels.size() " + dataModels.size());
        String[] dataIds = mRealmHelper.getNotSyncedDataStatesIds();
        Log.d(TAG, "onPerformSync: dataIds.length " + dataIds.length);
        if (dataModels.size() > 0) {
            uploadData(getToken(account), dataModels);
        }
        if (dataIds.length > 0) {
            getStateCodesFromServer(getToken(account), dataIds);
        }
        mRealmHelper.close();
    }

    private String getToken(Account account) {
        Log.d(TAG, "getToken");
        String token = null;
        try {
            token = mAccountManager.blockingGetAuthToken(account,
                    AccountGeneral.ARG_TOKEN_TYPE, true);
        } catch (OperationCanceledException e) {
            e.printStackTrace();
        } catch (AuthenticatorException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return token;
    }

    private void getStateCodesFromServer(String authToken, String[] dataIds) {
        Log.d(TAG, "getStateCodesFromServer");
        try {
            ListModel listModel = new ListModel(authToken, dataIds);
            Response<ListResponse> response = MyApplication.getApi().list(listModel).execute();
            Log.e(TAG, "onPerformSync: response code " + String.valueOf(response.code()));
            Log.e(TAG, "onPerformSync: response body " + String.valueOf(response.body()));
            List<String> ids = response.body().getIds();
            List<String> states = response.body().stateCodes();
            for (int i = 0; i < states.size(); i++) {
                if (!mRealmHelper.isStateCodeEqual(ids.get(i), states.get(i))) {
                    mRealmHelper.setStateCode(ids.get(i), states.get(i));
                    Log.d(TAG, "onResponse: id = " + ids.get(i));
                    Log.d(TAG, "onResponse: state = " + states.get(i));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void uploadData(String authToken, List<DataModel> notSyncedData) {
        Log.d(TAG, "uploadData");
        try {
            for (int i = 0; i < notSyncedData.size(); i++) {
                DataModel dataModel = notSyncedData.get(i);
                File imageFile = new File(dataModel.getPhotoURL());
                RequestBody requestBody = RequestBody.create(MediaType.parse(Constants.MEDIA_TYPE_IMAGE), imageFile);
                Response<ResponseBody> postImageResponse = MyApplication.getApi().postImage(requestBody).execute();
                Log.e(TAG, "onPerformSync: response code " + String.valueOf(postImageResponse.code()));
                Log.e(TAG, "onPerformSync: response body " + String.valueOf(postImageResponse.body()));
                String link = postImageResponse.body().string();
                String id = dataModel.getUid();
                mRealmHelper.setPhotoURL(id, link);
                mRealmHelper.setSynced(id, true);
                mRealmHelper.setStateCode(id, Constants.DATA_MODEL_STATE_CREATED);
                CommitModel commitModel = new CommitModel(
                        authToken,
                        link,
                        dataModel.getUid(),
                        dataModel.getPrefixID(),
                        dataModel.getComment(),
                        dataModel.getSearchDate(),
                        String.valueOf(dataModel.getLatitude() + "," + dataModel.getLongitude())
                );

                Response<ResponseBody> commitResponse = MyApplication.getApi().commit(commitModel).execute();
                Log.e(TAG, "responseBody.body().string().toString(): " + commitResponse.body().string());
                Log.e(TAG, "responseBody.code() " + commitResponse.code());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}