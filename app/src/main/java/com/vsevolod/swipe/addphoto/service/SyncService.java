package com.vsevolod.swipe.addphoto.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.vsevolod.swipe.addphoto.adapter.SyncAdapter;

public class SyncService extends Service {
    private final String TAG = this.getClass().getSimpleName();
    /**
     * Define a Service that returns an IBinder for the
     * sync adapter class, allowing the sync adapter framework to call
     * onPerformSync().
     */
    // Storage for an instance of the sync adapter
    private SyncAdapter sSyncAdapter = null;
    // Object to use as a thread-safe lock
    private final Object sSyncAdapterLock = new Object();

    /*
     * Instantiate the sync adapter object.
     */
    @Override
    public void onCreate() {
        Log.e(TAG, "onCreate");
        /*
         * Create the sync adapter as a singleton.
         * Set the sync adapter as syncable
         * Disallow parallel syncs
         */
        synchronized (sSyncAdapterLock) {
            if (sSyncAdapter == null) {
                Log.e(TAG, "onCreate sSyncAdapter==null");
                sSyncAdapter = new SyncAdapter(getApplicationContext(), true);
            }
        }
    }

    /**
     * Return an object that allows the system to invoke
     * the sync adapter.
     */
    @Override
    public IBinder onBind(Intent intent) {
        Log.e(TAG, "onBind");
        /*
         * Get the object that allows external processes
         * to call onPerformSync(). The object is created
         * in the base class code when the SyncAdapter
         * constructors call super()
         */
        return sSyncAdapter.getSyncAdapterBinder();
    }
}