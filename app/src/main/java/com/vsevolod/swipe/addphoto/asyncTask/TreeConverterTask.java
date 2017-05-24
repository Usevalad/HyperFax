package com.vsevolod.swipe.addphoto.asyncTask;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.vsevolod.swipe.addphoto.R;
import com.vsevolod.swipe.addphoto.accountAuthenticator.AccountGeneral;
import com.vsevolod.swipe.addphoto.config.MyApplication;
import com.vsevolod.swipe.addphoto.config.RealmHelper;
import com.vsevolod.swipe.addphoto.model.query.TokenModel;
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

    @Override
    protected void onPreExecute() {
        mContext = MyApplication.getAppContext();
        mAccountManager = AccountManager.get(mContext);
        mRealmHelper = new RealmHelper();
    }

    @Override
    protected List<FlowsTreeModel> doInBackground(Void... params) {
        Log.d(TAG, "doInBackground");
        Account account = new Account(AccountGeneral.ARG_ACCOUNT_NAME, AccountGeneral.ARG_ACCOUNT_TYPE);
        String token;
        List<List<String>> list = null;
        List<FlowsTreeModel> result = new ArrayList<>();

        try {
            Log.e(TAG, "doInBackground: blockingGetAuthToken");
            token = mAccountManager.blockingGetAuthToken(account,
                    AccountGeneral.ARG_TOKEN_TYPE, true);

            Log.e(TAG, "doInBackground: getTree()");
            Response<ResponseFlowsTreeModel> response = MyApplication.getApi()
                    .getTree(new TokenModel(token)).execute();

            // TODO: 16.05.17 check response code and status here
            Log.e(TAG, "doInBackground: response.body().getList()");
            list = response.body().getList();

        } catch (OperationCanceledException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (AuthenticatorException e) {
            e.printStackTrace();
        }

        mRealmHelper.open();
        List<String> tmp;

        assert list != null;
        int MIN_LIST_SIZE = 100; //проверка на количество
        if (list.size() > MIN_LIST_SIZE) {
            mRealmHelper.dropRealmTree();
            for (int i = 0; i < list.size(); i++) {
                tmp = list.get(i);
                int FLOWS_TREE_MODEL_FIELDS_QUANTITY = 4;
                if (tmp.size() == FLOWS_TREE_MODEL_FIELDS_QUANTITY) {
                    int ID_NUMBER = 0;
                    String id = tmp.get(ID_NUMBER);
                    int NAME_NUMBER = 1;
                    String name = tmp.get(NAME_NUMBER);
                    int PREFIX_NUMBER = 2;
                    String prefix = tmp.get(PREFIX_NUMBER);
                    int PARENT_ID_NUMBER = 3;
                    String parentId = tmp.get(PARENT_ID_NUMBER);
                    mRealmHelper.save(new FlowsTreeModel(id, name, prefix, parentId));
                    publishProgress(String.valueOf(i));
                }
            }
        }
        mRealmHelper.close();
        return result;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        Log.d(TAG, "onProgressUpdate: count = " + values[0]);
    }


    @Override
    protected void onPostExecute(List<FlowsTreeModel> flowsTreeModels) {
        super.onPostExecute(flowsTreeModels);
        String message = mContext.getString(R.string.updated);
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
    }
}