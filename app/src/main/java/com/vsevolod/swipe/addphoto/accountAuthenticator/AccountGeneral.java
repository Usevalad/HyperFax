package com.vsevolod.swipe.addphoto.accountAuthenticator;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.vsevolod.swipe.addphoto.R;
import com.vsevolod.swipe.addphoto.config.MyApplication;
import com.vsevolod.swipe.addphoto.config.PreferenceHelper;

/**
 * Created by vsevolod on 12.05.17.
 */

public class AccountGeneral {
    private static final String TAG = "AccountGeneral";
    public static final String ARG_ACCOUNT_TYPE = "com.vsevolod.swipe.addphoto";//(R.string.account_type);
    public static final String ARG_TOKEN_TYPE = "com.vsevolod.swipe.addphoto.EXTRA_TOKEN_TYPE";
    public static final String ARG_AUTH_TYPE = "AUTH_TYPE";
    public static final String ARG_ACCOUNT_NAME = "Hyper Fax";
    public static final String ARG_IS_ADDING_NEW_ACCOUNT = "IS_ADDING_ACCOUNT";
    public static final String KEY_ERROR_MESSAGE = "ERR_MSG";
    public static final String PARAM_USER_PASS = "USER_PASS";
    public static final String KEY_ACCOUNT_PHONE_NUMBER = "com.vsevolod.swipe.addphoto.phoneNumber";

    public static Account getAccount() {
        return new Account(new PreferenceHelper().getAccountName(), ARG_ACCOUNT_TYPE);
    }

    public static void finishLogin(Context context, Intent intent, String password) {
        Account account = getAccount();
        AccountManager accountManager = AccountManager.get(context);
        Account[] acc = accountManager.getAccountsByType(ARG_ACCOUNT_TYPE);
        String authToken = intent.getStringExtra(AccountManager.KEY_AUTHTOKEN);
        String authority = context.getString(R.string.content_authority);
        ContentResolver resolver = context.getContentResolver();

        if (acc.length == 0) {
            Log.e(TAG, "finishLogin: adding new account");
            if (accountManager.addAccountExplicitly(account, password, null)) {
                ContentResolver.setMasterSyncAutomatically(true);
                ContentResolver.setIsSyncable(account, authority, 1);
//                resolver.setSyncAutomatically(account, authority, true);
                ContentResolver.addPeriodicSync(
                        account,
                        authority,
                        Bundle.EMPTY,
                        60);
                accountManager.setAuthToken(account, ARG_TOKEN_TYPE, authToken);
            }

        } else {
            Log.e(TAG, "finishLogin: changing password in existed account");
            // Password change only
            accountManager.setPassword(account, password);
        }

        resolver.requestSync(account,
                authority,
                Bundle.EMPTY);
    }

    public static void sync() {
        Log.e(TAG, "sync:");
        Context context = MyApplication.getAppContext();
        ContentResolver.requestSync(
                getAccount(),
                context.getString(R.string.content_authority),
                Bundle.EMPTY
        );
    }

}