package com.vsevolod.swipe.addphoto.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.vsevolod.swipe.addphoto.accountAuthenticator.AccountGeneral;

/**
 * Created by vsevolod on 18.06.17.
 */

public class SyncAlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        AccountGeneral.sync();
    }
}
