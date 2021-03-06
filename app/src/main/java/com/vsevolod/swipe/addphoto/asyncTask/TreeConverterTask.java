package com.vsevolod.swipe.addphoto.asyncTask;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.vsevolod.swipe.addphoto.R;
import com.vsevolod.swipe.addphoto.accountAuthenticator.AccountGeneral;
import com.vsevolod.swipe.addphoto.activity.NotificationActivity;
import com.vsevolod.swipe.addphoto.config.MyApplication;
import com.vsevolod.swipe.addphoto.config.PreferenceHelper;
import com.vsevolod.swipe.addphoto.config.RealmHelper;
import com.vsevolod.swipe.addphoto.constant.ResponseStatus;
import com.vsevolod.swipe.addphoto.constant.TreeSubStatus;
import com.vsevolod.swipe.addphoto.model.query.TreeQueryModel;
import com.vsevolod.swipe.addphoto.model.realm.FlowsTreeModel;
import com.vsevolod.swipe.addphoto.model.responce.ResponseFlowsTreeModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Response;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by vsevolod on 09.04.17.
 */
// TODO: 7/17/17 refactor
public class TreeConverterTask extends AsyncTask<Void, String, List<FlowsTreeModel>> {
    private final String TAG = this.getClass().getSimpleName();
    private RealmHelper mRealmHelper;
    private AccountManager mAccountManager;
    private PreferenceHelper mPreferenceHelper;
    private Context mContext;
    private String notify;
    private String message;
    private final int ID_NUMBER = 0; // TODO: 14.07.17 add to enum
    private final int NAME_NUMBER = 1;
    private final int PREFIX_NUMBER = 2;
    private final int PARENT_ID_NUMBER = 3;

    @Override
    protected void onPreExecute() {
        Log.e(TAG, "onPreExecute");
        mContext = MyApplication.getContext();
        mAccountManager = AccountManager.get(mContext);
        mRealmHelper = new RealmHelper();
        mPreferenceHelper = new PreferenceHelper();
    }

    @Override
    protected List<FlowsTreeModel> doInBackground(Void... params) {
        Log.e(TAG, "doInBackground");
        Account account = AccountGeneral.getAccount();
        String token;
        List<List<String>> list = null;
        List<FlowsTreeModel> result = new ArrayList<>();
        String modified;

        try {
            Log.e(TAG, "doInBackground: blockingGetAuthToken");
            token = mAccountManager.blockingGetAuthToken(account,
                    AccountGeneral.ARG_TOKEN_TYPE, true);
            Log.e(TAG, "doInBackground: token " + token);
            Response<ResponseFlowsTreeModel> response;

            Log.e(TAG, "doInBackground: get tree");
            response = MyApplication.getApi().getTree(new TreeQueryModel(token)).execute();

            if (response.code() == 201 || response.code() == 200) {
                switch (response.body().getStatus()) {
                    case ResponseStatus.AUTH:
                        message = "Нужна авторизация";
                        AccountGeneral.removeAccount(mContext, AccountManager.get(mContext));
                        Log.e(TAG, "Status AUTH. invalid token");
                        break;
                    case ResponseStatus.FAIL:
                        Toast.makeText(mContext, "Fail", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Status FAIL. error message: " + response.body().getError());
                        break;
                    case ResponseStatus.OK:
                        message = "Обновлено";
                        notify = response.body().getNotify();
                        String resultCode = response.body().getResult();
                        Log.e(TAG, "Status OK. notify: " + notify);
                        Log.e(TAG, "Status OK. result: " + resultCode);

                        if (TextUtils.equals(resultCode, TreeSubStatus.NM)) {
                            message = mContext.getString(R.string.already_have_last_version);
                        } else if (TextUtils.equals(resultCode, TreeSubStatus.CH) ||
                                TextUtils.equals(resultCode, TreeSubStatus.NT)) {

                            list = response.body().getList();
                            mPreferenceHelper.saveString(PreferenceHelper.APP_PREFERENCES_MODIFIED,
                                    response.body().getModified());
                            message = mContext.getString(R.string.updated);
                        }

                        break;
                    case ResponseStatus.PARAM:
                        message = "Parameters";
                        Log.e(TAG, "Status PARAM");
                        break;
                    default:
                        break;
                }
            } else {
                Log.e(TAG, "response code = " + response.code());
                Log.e(TAG, "response message = " + response.message());
            }
        } catch (OperationCanceledException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (AuthenticatorException e) {
            e.printStackTrace();
        }

        if (list != null && list.size() > 0) {
            mRealmHelper.open();
            mRealmHelper.dropRealmTree();
            String id, viewName, prefix, parentId, searchName;
            for (int i = 0; i < list.size(); i++) {
                id = list.get(i).get(ID_NUMBER);
                searchName = list.get(i).get(NAME_NUMBER).toLowerCase();
                viewName = list.get(i).get(NAME_NUMBER);
                prefix = list.get(i).get(PREFIX_NUMBER);
                parentId = list.get(i).get(PARENT_ID_NUMBER);
                mRealmHelper.save(new FlowsTreeModel(id, viewName, searchName, prefix, parentId));
                publishProgress(String.valueOf(i));
            }
            mRealmHelper.close();
        }
        return result;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        Log.e(TAG, "onProgressUpdate: count = " + values[0]);
        // TODO: 28.05.17 add some UI action
    }

    @Override
    protected void onPostExecute(List<FlowsTreeModel> flowsTreeModels) {
        super.onPostExecute(flowsTreeModels);
        Log.e(TAG, "onPostExecute");
        mPreferenceHelper.saveDate(PreferenceHelper.APP_PREFERENCES_FLOWS_UPDATE_DATE, new Date().getTime());
        if (!TextUtils.isEmpty(message)) {
            Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
            Log.e(TAG, "onPostExecute: message :" + message);
        }
        if (!TextUtils.isEmpty(notify)) {
            Intent intent = new Intent(mContext, NotificationActivity.class);
            intent.putExtra("notify", notify);
            PendingIntent pIntent = PendingIntent.getActivity(mContext, (int) System.currentTimeMillis(), intent, 0);
            Notification n = new Notification.Builder(mContext)
                    .setContentTitle(mContext.getString(R.string.app_name))
                    .setContentText(notify)
                    .setSmallIcon(R.drawable.ic_toolbar_logo
                    )
                    .setContentIntent(pIntent)
                    .setAutoCancel(true)
                    .build();
            NotificationManager notificationManager =
                    (NotificationManager) (mContext).getSystemService(NOTIFICATION_SERVICE);

            notificationManager.notify(0, n);
        }
    }
}