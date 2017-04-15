package com.vsevolod.swipe.addphoto.command.method;

import android.util.Log;

import com.vsevolod.swipe.addphoto.command.Api;
import com.vsevolod.swipe.addphoto.command.Command;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by vsevolod on 13.04.17.
 */

public class UploadPhoto implements Command {
    private final String TAG = "UploadPhoto";
    private Api mApi;

    public UploadPhoto(Api api) {
        Log.d(TAG, "Authentication");
        this.mApi = api;
    }

    public void execute(MultipartBody.Part body, RequestBody name) {
        Log.d(TAG, "execute");
        mApi.uploadImage(body, name);
    }

    @Override
    public void execute() {

    }
}
