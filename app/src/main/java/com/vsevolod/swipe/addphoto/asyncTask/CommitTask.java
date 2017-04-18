package com.vsevolod.swipe.addphoto.asyncTask;

import android.os.AsyncTask;
import android.util.Log;

import com.vsevolod.swipe.addphoto.model.realm.DataModel;

/**
 * Created by vsevolod on 15.04.17.
 */

public class CommitTask extends AsyncTask<DataModel, Void, Void> {
    private final String TAG = "CommitTask";

    @Override
    protected Void doInBackground(DataModel... params) {
        Log.d(TAG, "doInBackground");
        try {
            Thread.sleep((1000*60));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return null;
    }
}
