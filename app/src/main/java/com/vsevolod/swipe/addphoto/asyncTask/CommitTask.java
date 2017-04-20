package com.vsevolod.swipe.addphoto.asyncTask;

import android.os.AsyncTask;
import android.util.Log;

import com.vsevolod.swipe.addphoto.command.MyasoApi;
import com.vsevolod.swipe.addphoto.command.method.UploadPhoto;
import com.vsevolod.swipe.addphoto.model.realm.DataModel;

import okhttp3.RequestBody;

/**
 * Created by vsevolod on 15.04.17.
 */

public class CommitTask extends AsyncTask<RequestBody, Void, Void> {
    private final String TAG = "CommitTask";
    private MyasoApi api = new MyasoApi();

    @Override
    protected Void doInBackground(RequestBody... params) {
        Log.d(TAG, "doInBackground");
        RequestBody reqFile = params[0];
        UploadPhoto uploadPhoto = new UploadPhoto(api);
        uploadPhoto.execute(reqFile);
        return null;
    }
}
