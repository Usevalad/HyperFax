package com.vsevolod.swipe.addphoto.command.method;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.util.Log;

import com.vsevolod.swipe.addphoto.accountAuthenticator.AccountGeneral;
import com.vsevolod.swipe.addphoto.activity.LoginActivity;
import com.vsevolod.swipe.addphoto.command.Api;
import com.vsevolod.swipe.addphoto.command.Command;
import com.vsevolod.swipe.addphoto.config.Constants;
import com.vsevolod.swipe.addphoto.config.MyApplication;
import com.vsevolod.swipe.addphoto.config.PreferenceHelper;
import com.vsevolod.swipe.addphoto.model.query.TokenModel;

/**
 * Created by vsevolod on 11.04.17.
 */

public class GetTree implements Command {
    private final String TAG = this.getClass().getSimpleName();
    private Api mApi;

    public GetTree(Api Api) {
        Log.d(TAG, "GetTree");
        this.mApi = Api;
    }

    @Override
    public void execute() {
        Log.d(TAG, "execute");
        PreferenceHelper mPreferenceHelper = new PreferenceHelper();
        AccountManager am = AccountManager.get(MyApplication.getAppContext());
        Account account = new Account(AccountGeneral.ARG_ACCOUNT_NAME, AccountGeneral.ARG_ACCOUNT_TYPE);


        String token = am.peekAuthToken(account, AccountGeneral.ARG_AUTH_TYPE);
        Log.e(TAG, "execute: " + token );
        // FIXME: 11.05.17 token == null. no token in account manager
        if (token == null) {
            token = mPreferenceHelper.getToken();
            Log.e(TAG, "execute: " + token );
        }
        mApi.getTree(new TokenModel(token));
    }
}
