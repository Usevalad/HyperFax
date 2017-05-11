package com.vsevolod.swipe.addphoto.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.vsevolod.swipe.addphoto.accountAuthenticator.HyperFaxAuthenticator;

public class HyperFaxAuthenticatorService extends Service {
    private final String TAG = HyperFaxAuthenticatorService.class.getSimpleName();
    private HyperFaxAuthenticator mAuthenticator;

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        // Create a new authenticator object
        mAuthenticator = new HyperFaxAuthenticator(this);
    }

    /*
     * When the system binds to this Service to make the RPC call
     * return the authenticator's IBinder.
     */
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
        return mAuthenticator.getIBinder();
    }
}
