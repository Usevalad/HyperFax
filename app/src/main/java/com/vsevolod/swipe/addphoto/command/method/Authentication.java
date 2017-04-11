package com.vsevolod.swipe.addphoto.command.method;

import android.util.Log;

import com.vsevolod.swipe.addphoto.command.Api;
import com.vsevolod.swipe.addphoto.command.Command;
import com.vsevolod.swipe.addphoto.model.query.AuthModel;

/**
 * Created by vsevolod on 11.04.17.
 */

public class Authentication implements Command {
    private final String TAG = "Authentication";
    private Api mApi;

    public Authentication(Api api) {
        Log.d(TAG, "Authentication");
        this.mApi = api;
    }

    @Override
    public void execute() {
        Log.d(TAG, "execute");
        mApi.authenticate(new AuthModel("+380506361408", "admin"));
    }
}
