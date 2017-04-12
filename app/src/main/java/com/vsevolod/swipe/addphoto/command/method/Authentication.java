package com.vsevolod.swipe.addphoto.command.method;

import android.util.Log;

import com.vsevolod.swipe.addphoto.command.Api;
import com.vsevolod.swipe.addphoto.command.Command;
import com.vsevolod.swipe.addphoto.config.PreferenceHelper;
import com.vsevolod.swipe.addphoto.model.query.AuthModel;

/**
 * Created by vsevolod on 11.04.17.
 */

public class Authentication implements Command {
    private final String TAG = "Authentication";
    private Api mApi;
    private PreferenceHelper helper = new PreferenceHelper();

    public Authentication(Api api) {
        Log.d(TAG, "Authentication");
        this.mApi = api;
    }

    @Override
    public void execute() {
        Log.d(TAG, "execute");
        String phoneNumber = helper.getPhone();
        String password = helper.getPassword();
        mApi.authenticate(new AuthModel(phoneNumber, password));
    }
}
