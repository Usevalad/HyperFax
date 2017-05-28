package com.vsevolod.swipe.addphoto.asyncTask;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.vsevolod.swipe.addphoto.R;
import com.vsevolod.swipe.addphoto.accountAuthenticator.AccountGeneral;
import com.vsevolod.swipe.addphoto.config.Constants;
import com.vsevolod.swipe.addphoto.config.MyApplication;
import com.vsevolod.swipe.addphoto.config.PreferenceHelper;
import com.vsevolod.swipe.addphoto.config.RealmHelper;
import com.vsevolod.swipe.addphoto.model.query.FirstTreeQueryModel;
import com.vsevolod.swipe.addphoto.model.query.TreeQueryModel;
import com.vsevolod.swipe.addphoto.model.realm.FlowsTreeModel;
import com.vsevolod.swipe.addphoto.model.responce.ResponseFlowsTreeModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

/**
 * Created by vsevolod on 09.04.17.
 */

public class TreeConverterTask extends AsyncTask<Void, String, List<FlowsTreeModel>> {
    private final String TAG = this.getClass().getSimpleName();
    private RealmHelper mRealmHelper;
    private AccountManager mAccountManager;
    private Context mContext;
    private boolean isFirst;
    private String notify;
    private String message;
    private final int ID_NUMBER = 0;
    private final int NAME_NUMBER = 1;
    private final int PREFIX_NUMBER = 2;
    private final int PARENT_ID_NUMBER = 3;

    public TreeConverterTask(boolean isFirst) {
        this.isFirst = isFirst;
    }

    @Override
    protected void onPreExecute() {
        mContext = MyApplication.getAppContext();
        mAccountManager = AccountManager.get(mContext);
        mRealmHelper = new RealmHelper();
    }

    @Override
    protected List<FlowsTreeModel> doInBackground(Void... params) {
        Log.e(TAG, "doInBackground");
        Account account = new Account(new PreferenceHelper().getAccountName(), AccountGeneral.ARG_ACCOUNT_TYPE);
        String token;
        List<List<String>> list = null;
        List<FlowsTreeModel> result = new ArrayList<>();
        String modified;

        try {
            Log.e(TAG, "doInBackground: blockingGetAuthToken");
            token = mAccountManager.blockingGetAuthToken(account,
                    AccountGeneral.ARG_TOKEN_TYPE, true);
            Response<ResponseFlowsTreeModel> response;

            if (isFirst) {
                Log.e(TAG, "doInBackground: firstTree");
                response = MyApplication.getApi().getFirstTree(new FirstTreeQueryModel(token)).execute();
            } else {
                Log.e(TAG, "doInBackground: get tree");
                response = MyApplication.getApi().getTree(new TreeQueryModel(token)).execute();
            }

            if (response.code() == 201 || response.code() == 200) {
                switch (response.body().getStatus()) {
                    case Constants.RESPONSE_STATUS_AUTH:
                        Log.e(TAG, "Status AUTH. invalid token");
                        break;
                    case Constants.RESPONSE_STATUS_FAIL:
                        Log.e(TAG, "Status FAIL. error message: " + response.body().getError());
                        break;
                    case Constants.RESPONSE_STATUS_OK:
                        notify = response.body().getNotify();
                        String resultCode = response.body().getResult();
                        Log.e(TAG, "Status OK. notify: " + notify);
                        Log.e(TAG, "Status OK. result: " + resultCode);

                        if (TextUtils.equals(resultCode, Constants.RESPONSE_TREE_SUB_STATUS_NM)) {
                            message = mContext.getString(R.string.already_have_last_version);
                        } else if (TextUtils.equals(resultCode, Constants.RESPONSE_TREE_SUB_STATUS_CH) ||
                                TextUtils.equals(resultCode, Constants.RESPONSE_TREE_SUB_STATUS_NT)) {

                            list = response.body().getList();
                            new PreferenceHelper().saveString(PreferenceHelper.APP_PREFERENCES_MODIFIED,
                                    response.body().getModified());
                            message = mContext.getString(R.string.updated);
                        }

                        break;
                    case Constants.RESPONSE_STATUS_PARAM:
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
            List<String> tmp;
            mRealmHelper.dropRealmTree();
            for (int i = 0; i < list.size(); i++) {
                tmp = list.get(i);
                String id = tmp.get(ID_NUMBER);
                String name = tmp.get(NAME_NUMBER);
                String prefix = tmp.get(PREFIX_NUMBER);
                String parentId = tmp.get(PARENT_ID_NUMBER);
                mRealmHelper.save(new FlowsTreeModel(id, name, prefix, parentId));
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
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
        // TODO: 28.05.17 send notification
    }
}