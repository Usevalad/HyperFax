package com.vsevolod.swipe.addphoto.adapter;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.PeriodicSync;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.vsevolod.swipe.addphoto.R;
import com.vsevolod.swipe.addphoto.accountAuthenticator.AccountGeneral;
import com.vsevolod.swipe.addphoto.asyncTask.TreeConverterTask;
import com.vsevolod.swipe.addphoto.config.Constants;
import com.vsevolod.swipe.addphoto.config.MyApplication;
import com.vsevolod.swipe.addphoto.config.RealmHelper;
import com.vsevolod.swipe.addphoto.model.query.CommitModel;
import com.vsevolod.swipe.addphoto.model.query.ListQueryModel;
import com.vsevolod.swipe.addphoto.model.realm.DataModel;
import com.vsevolod.swipe.addphoto.model.responce.CommitResponseModel;
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
        Log.e(TAG, "SyncAdapter: constructor");
    }

    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        mAccountManager = AccountManager.get(context);
        mRealmHelper = new RealmHelper();
        Log.e(TAG, "SyncAdapter: constructor 2");
    }

    @Override
    public void onPerformSync(Account account, Bundle extras,
                              String authority, ContentProviderClient provider,
                              SyncResult syncResult) {
        Log.e(TAG, "onPerformSync");
        mRealmHelper.open();
        List<DataModel> dataModels = mRealmHelper.getNotSyncedData();
        Log.e(TAG, "onPerformSync: NotSyncedData.size() " + dataModels.size());
        String[] dataIds = mRealmHelper.getNotSyncedDataStatesIds();
        Log.e(TAG, "onPerformSync: NotSyncedDataStatesIds.length " + dataIds.length);
        if (dataModels.size() > 0) {
            uploadData(getToken(account), dataModels);
        }
        if (dataIds.length > 0) {
            getStateCodesFromServer(getToken(account), dataIds);
        }
        mRealmHelper.close();
        updateFlowsTree();
    }

    private void updateFlowsTree() {
        Log.e(TAG, "updateFlowsTree");
        TreeConverterTask task = new TreeConverterTask();
        task.execute();
    }

    private String getToken(Account account) {
        Log.e(TAG, "getToken");
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
        Log.e(TAG, "getStateCodesFromServer");
        try {
            ListQueryModel listQueryModel = new ListQueryModel(authToken, dataIds);
            Response<ListResponse> response = MyApplication.getApi().getList(listQueryModel).execute();
            if (response.isSuccessful()) {
                if (TextUtils.equals(response.body().getStatus(), Constants.RESPONSE_STATUS_OK)) {
                    Log.e(TAG, "getList: response code " + String.valueOf(response.code()));
                    Log.e(TAG, "getList: response body " + String.valueOf(response.body()));
                    List<String> ids = response.body().getIds();
                    List<String> states = response.body().stateCodes();
                    for (int i = 0; i < states.size(); i++) {
                        if (!mRealmHelper.isStateCodeEqual(ids.get(i), states.get(i))) {
                            mRealmHelper.setStateCode(ids.get(i), states.get(i));
                            Log.e(TAG, "onResponse: id = " + ids.get(i));
                            Log.e(TAG, "onResponse: state = " + states.get(i));
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void uploadData(String authToken, List<DataModel> notSyncedData) {
        Log.e(TAG, "uploadData");
        try {
            for (int i = 0; i < notSyncedData.size(); i++) {
                DataModel dataModel = notSyncedData.get(i);
                File imageFile = new File(dataModel.getStoragePhotoURL());
                RequestBody requestBody = RequestBody.create(MediaType.parse(Constants.MEDIA_TYPE_IMAGE), imageFile);
                Response<ResponseBody> postImageResponse = MyApplication.getApi().postImage(requestBody).execute();
                Log.e(TAG, "uploadData: response code " + String.valueOf(postImageResponse.code()));
                Log.e(TAG, "uploadData: response body " + String.valueOf(postImageResponse.body()));
                String link = postImageResponse.body().string();
                String id = dataModel.getUid();
                Log.e(TAG, "uploadData: link" + link);
                Log.e(TAG, "uploadData: id" + id);
                CommitModel commitModel = new CommitModel(
                        authToken,
                        link,
                        dataModel.getUid(),
                        dataModel.getPrefixID(),
                        dataModel.getComment(),
                        dataModel.getSearchDate(),
                        String.valueOf(dataModel.getLatitude() + "," + dataModel.getLongitude())
                );

                Response<CommitResponseModel> commitResponse = MyApplication.getApi().commit(commitModel).execute();
                Log.e(TAG, "commit : commitResponse.status :" + commitResponse.body().getStatus());
                Log.e(TAG, "commit : commitResponse.code() " + commitResponse.code());
                Log.e(TAG, "commit : commitResponse.log() " + commitResponse.body().getLog());
                switch (commitResponse.body().getStatus()) {
                    case Constants.RESPONSE_STATUS_PARAM:
                        mRealmHelper.setStateCode(id, Constants.DATA_MODEL_STATE_PARAM);
                        mRealmHelper.setSynced(id, true);
                        break;
                    case Constants.RESPONSE_STATUS_OK:
                        mRealmHelper.setPhotoURL(id, link);
                        mRealmHelper.setSynced(id, true);
                        mRealmHelper.setStateCode(id, Constants.DATA_MODEL_STATE_CREATED);
                        break;
                    default:
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Log.e(TAG, "configurePeriodicSync");
        /*

        добавил этот метод для периодической синхронизации. Вызываю его из mainActivity onCreate
        данные, ктороые я добавляю в этом методе отбражаются в contentResolver.getPeriodicSyncs,
        но синхронизация не происходит

         */
        AccountManager manager = AccountManager.get(context);
        Account account = manager.getAccountsByType(AccountGeneral.ARG_ACCOUNT_TYPE)[0];
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder()
                    .syncPeriodic(syncInterval, flexTime)
                    .setSyncAdapter(account, authority)
                    .setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
            List<PeriodicSync> s = ContentResolver.getPeriodicSyncs(account,
                    context.getString(R.string.content_authority));
            Log.e(TAG, "setPeriodicSync: size" + s.size());
            for (int i = 0; i < s.size(); i++) {
                Log.e(TAG, "setPeriodicSync: toString " + s.get(i).toString());
            }

        } else {
            ContentResolver.addPeriodicSync(account, authority, new Bundle(), syncInterval);
            List<PeriodicSync> s = ContentResolver.getPeriodicSyncs(account, context.getString(R.string.content_authority));
            Log.e(TAG, " else setPeriodicSync: size" + s.size());
            for (int i = 0; i < s.size(); i++) {
                Log.e(TAG, "setPeriodicSync: toString " + s.get(i).toString());

            }

        }
    }

}