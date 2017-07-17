package com.vsevolod.swipe.addphoto.asyncTask;

import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.vsevolod.swipe.addphoto.accountAuthenticator.AccountGeneral;
import com.vsevolod.swipe.addphoto.activity.MainActivity;
import com.vsevolod.swipe.addphoto.api.command.GetList;
import com.vsevolod.swipe.addphoto.api.command.PostPhoto;
import com.vsevolod.swipe.addphoto.config.MyApplication;
import com.vsevolod.swipe.addphoto.config.PreferenceHelper;
import com.vsevolod.swipe.addphoto.config.RealmHelper;
import com.vsevolod.swipe.addphoto.constant.Constants;
import com.vsevolod.swipe.addphoto.model.realm.DataModel;

import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * Created by vsevolod on 14.07.17.
 * Async needed for start and stop refresh animation
 */
// TODO: 14.07.17 не изменять класс пока все запросы не будут вынесены в другое место
//потому, что это копия кода в синк адаптере
public class ServerSyncTask extends AsyncTask<Void, Void, Void> {
    private final String TAG = getClass().getSimpleName();
    private RealmHelper mRealmHelper = new RealmHelper();
    private Context mContext;
    private AccountManager mAccountManager;

    @Override
    protected void onPreExecute() {
        Log.e(TAG, "onPreExecute");
        MainActivity.swipeRefreshLayout.setRefreshing(true);
        mContext = MyApplication.getAppContext();
        mAccountManager = AccountManager.get(mContext);
    }

    @Override
    protected Void doInBackground(Void... params) {
        Log.e(TAG, "doInBackground");

        String token = null;
        try {
            token = mAccountManager.blockingGetAuthToken(AccountGeneral.getAccount(),
                    AccountGeneral.ARG_TOKEN_TYPE, true);
        } catch (OperationCanceledException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (AuthenticatorException e) {
            e.printStackTrace();
        }
        mRealmHelper.open();
        List<DataModel> dataModels = mRealmHelper.getNotSyncedData();
        Log.e(TAG, "onPerformSync: NotSyncedData.size() " + dataModels.size());
        String[] dataIds = mRealmHelper.getNotSyncedDataStatesIds();
        Log.e(TAG, "onPerformSync: NotSyncedDataStatesIds.length " + dataIds.length);
        if (dataModels.size() > 0) {
            uploadData(token, dataModels);
        }
        if (dataIds.length > 0) {
            getStateCodesFromServer(token, dataIds);
        }

        mRealmHelper.close();

        long currentDate = new Date().getTime();
        long lastUpdate = new PreferenceHelper().getLastUpdate();
        if (currentDate - lastUpdate >= Constants.MILLISECONDS_DAY) {
            updateFlowsTree();
        }
        return null;
    }


    private void getStateCodesFromServer(String authToken, String[] dataIds) {
        Log.e(TAG, "getStateCodesFromServer");
        new GetList(mContext, mRealmHelper, authToken).execute();
    }

    private void uploadData(String authToken, List<DataModel> notSyncedData) {
        Log.e(TAG, "uploadData");
        new PostPhoto(mContext, mRealmHelper, authToken, notSyncedData).execute();
    }

    private void updateFlowsTree() {
        Log.e(TAG, "updateFlowsTree");
        TreeConverterTask task = new TreeConverterTask();
        task.execute();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        Log.e(TAG, "onPostExecute");
        MainActivity.swipeRefreshLayout.setRefreshing(false);
    }
}