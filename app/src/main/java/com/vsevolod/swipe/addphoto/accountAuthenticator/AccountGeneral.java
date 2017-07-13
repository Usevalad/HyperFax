package com.vsevolod.swipe.addphoto.accountAuthenticator;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.IntentCompat;
import android.text.TextUtils;
import android.util.Log;

import com.vsevolod.swipe.addphoto.activity.MainActivity;
import com.vsevolod.swipe.addphoto.config.Constants;
import com.vsevolod.swipe.addphoto.config.PreferenceHelper;
import com.vsevolod.swipe.addphoto.receiver.SyncAlarmReceiver;

/**
 * Created by vsevolod on 12.05.17.
 */

public class AccountGeneral {
    private static final String TAG = "AccountGeneral";
    // R.string.account_type
    public static final String ARG_ACCOUNT_TYPE = "com.vsevolod.swipe.addphoto";
    public static final String ARG_TOKEN_TYPE = "com.vsevolod.swipe.addphoto.EXTRA_TOKEN_TYPE";
    public static final String ARG_AUTH_TYPE = "AUTH_TYPE";
    public static final String ARG_ACCOUNT_NAME = "Hyper Fax";
    public static final String ARG_IS_ADDING_NEW_ACCOUNT = "IS_ADDING_ACCOUNT";
    public static final String KEY_ERROR_MESSAGE = "ERR_MSG";
    public static final String PARAM_USER_PASS = "USER_PASS";
    public static final String KEY_ACCOUNT_PHONE_NUMBER = "com.vsevolod.swipe.addphoto.phoneNumber";
    //   R.string.content_authority
    public static final String CONTENT_AUTHORITY = "com.vsevolod.swipe.addphoto.provider.StubProvider";

    public static Account getAccount() {
        String accountName = new PreferenceHelper().getAccountName();
//        accountName = TextUtils.isEmpty(accountName) ? ARG_ACCOUNT_NAME : accountName;
        return new Account(accountName, ARG_ACCOUNT_TYPE);
    }

    public static void finishLogin(Context context, Intent intent, String password) {
        Account account = getAccount();
        AccountManager accountManager = AccountManager.get(context);
        Account[] acc = accountManager.getAccountsByType(ARG_ACCOUNT_TYPE);
        String authToken = intent.getStringExtra(AccountManager.KEY_AUTHTOKEN);

        if (acc.length == 0) {
            Log.e(TAG, "finishLogin: adding new account");
            if (accountManager.addAccountExplicitly(account, password, null)) {
                accountManager.setAuthToken(account, ARG_TOKEN_TYPE, authToken);
                setAutomaticSync(account);
            }

        } else {
            Log.e(TAG, "finishLogin: changing password in existed account");
            // Password change only
            accountManager.setPassword(account, password);
        }
    }

    /*
        turning on syncable
     */
    private static void setAutomaticSync(Account account) {
        ContentResolver.setMasterSyncAutomatically(true);
        ContentResolver.setIsSyncable(account, CONTENT_AUTHORITY, 1);
        ContentResolver.setSyncAutomatically(account, CONTENT_AUTHORITY, true);
    }

    /*
        account force sync
     */
    public static void sync() {
        Log.e(TAG, "sync:");
        setAutomaticSync(getAccount());
        ContentResolver.requestSync(
                getAccount(),
                CONTENT_AUTHORITY,
                Bundle.EMPTY
        );
    }

    /*
        setting periodic sync via alarm manage, cause SynAdapter periodic sync doesn't work
     */
    public static void setPeriodicSync(Context context) {
        PendingIntent pendingIntent;
        AlarmManager manager;
        Intent alarmIntent = new Intent(context, SyncAlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);
        manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
                Constants.MILLISECONDS_HOUR, pendingIntent);
    }

    /*
        cancelling periodic sync
     */
    public static void cancelPeriodicSync(Context context) {
        PendingIntent pendingIntent;
        AlarmManager manager;
        Intent alarmIntent = new Intent(context, SyncAlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);
        manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        manager.cancel(pendingIntent);
    }

    public static void removeAccount(Context context, AccountManager accountManager) {

        Account account = getAccount();
        // loop through all accounts to remove them
        if (TextUtils.equals(account.type, AccountGeneral.ARG_ACCOUNT_TYPE)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                accountManager.removeAccountExplicitly(account);
            } else {
                accountManager.removeAccount(account, null, null);
            }
        }

        Intent intent = new Intent(context, MainActivity.class);
        ComponentName cn = intent.getComponent();
        Intent mainIntent = IntentCompat.makeRestartActivityTask(cn);
        context.startActivity(mainIntent);
    }
}