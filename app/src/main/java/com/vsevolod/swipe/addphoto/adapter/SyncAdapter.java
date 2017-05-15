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
import com.vsevolod.swipe.addphoto.model.realm.DataModel;

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
// TODO: 15.05.17 little bit refactor
public class SyncAdapter extends AbstractThreadedSyncAdapter {
    private final String TAG = this.getClass().getSimpleName();
    private AccountManager mAccountManager;
    private RealmHelper mRealmHelper;
    private DataModel dataModel;

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
        try {
            final String authToken = mAccountManager.blockingGetAuthToken(account,
                    AccountGeneral.ARG_TOKEN_TYPE, true); // TODO: 15.05.17 wrap in a uploadData method
            List<DataModel> notSyncedData = mRealmHelper.getNotSynced();
            // TODO: 15.05.17 add some logic to check states (list\getList)

            for (int i = 0; i < notSyncedData.size(); i++) {
                dataModel = notSyncedData.get(i);
                File imageFile = new File(dataModel.getPhotoURL());
                RequestBody requestBody = RequestBody.create(MediaType.parse(Constants.MEDIA_TYPE_IMAGE), imageFile);
                Response<ResponseBody> postImageResponse = MyApplication.getApi().postImage(requestBody).execute();
                Log.e(TAG, "onPerformSync: response code " + String.valueOf(postImageResponse.code()));
                Log.e(TAG, "onPerformSync: response body " + String.valueOf(postImageResponse.body()));
                String link = postImageResponse.body().string();
                String id = dataModel.getUid();
                mRealmHelper.updatePhotoURL(id, link);
                mRealmHelper.setSynced(id, true);
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

        } catch (AuthenticatorException e) {
            e.printStackTrace();
        } catch (OperationCanceledException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mRealmHelper.close();
    }
}