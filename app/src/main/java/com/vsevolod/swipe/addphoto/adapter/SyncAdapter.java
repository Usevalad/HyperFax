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

import com.google.firebase.crash.FirebaseCrash;
import com.vsevolod.swipe.addphoto.accountAuthenticator.AccountGeneral;
import com.vsevolod.swipe.addphoto.api.command.GetList;
import com.vsevolod.swipe.addphoto.api.command.PostPhoto;
import com.vsevolod.swipe.addphoto.asyncTask.TreeConverterTask;
import com.vsevolod.swipe.addphoto.config.MyApplication;
import com.vsevolod.swipe.addphoto.config.PreferenceHelper;
import com.vsevolod.swipe.addphoto.config.RealmHelper;
import com.vsevolod.swipe.addphoto.constant.Constants;
import com.vsevolod.swipe.addphoto.constant.Millisecond;
import com.vsevolod.swipe.addphoto.model.query.CommitModel;
import com.vsevolod.swipe.addphoto.model.realm.DataModel;
import com.vsevolod.swipe.addphoto.model.responce.CommitResponseModel;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * Created by vsevolod on 13.05.17.
 */
// TODO: 14.07.17 не изменять класс пока все запросы не будут вынесены в другое место
//потому, что это копия кода в ServerSyncTask

public class SyncAdapter extends AbstractThreadedSyncAdapter {
    private final String TAG = this.getClass().getSimpleName();
    private AccountManager mAccountManager;
    private RealmHelper mRealmHelper;
    private Context mContext;

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mAccountManager = AccountManager.get(context);
        mRealmHelper = new RealmHelper();
        mContext = context;
        Log.e(TAG, "SyncAdapter: constructor");
    }

    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        mAccountManager = AccountManager.get(context);
        mRealmHelper = new RealmHelper();
        mContext = context;
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
            new PostPhoto(mContext, mRealmHelper, getToken(account), dataModels).execute();
        }
        if (dataIds.length > 0) {
            new GetList(mContext, mRealmHelper, getToken(account)).execute();
        }

        mRealmHelper.close();

        long currentDate = new Date().getTime();
        long lastUpdate = new PreferenceHelper().getLastUpdate();
        if (currentDate - lastUpdate >= Millisecond.DAY) {
            new TreeConverterTask().execute();
        }
    }

    private String getToken(Account account) {
        Log.e(TAG, "getToken");
        String token = null;
        try {
            token = mAccountManager.blockingGetAuthToken(account,
                    AccountGeneral.ARG_TOKEN_TYPE, true);
            Log.e(TAG, "getToken: token" + token);
        } catch (OperationCanceledException e) {
            e.printStackTrace();
        } catch (AuthenticatorException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return token;
    }
}