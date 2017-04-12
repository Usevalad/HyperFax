package com.vsevolod.swipe.addphoto.command.method;

import android.util.Log;

import com.vsevolod.swipe.addphoto.command.Api;
import com.vsevolod.swipe.addphoto.command.Command;
import com.vsevolod.swipe.addphoto.config.PreferenceHelper;
import com.vsevolod.swipe.addphoto.model.query.TokenModel;

/**
 * Created by vsevolod on 11.04.17.
 */

public class Check implements Command {
    private final String TAG = "Check";
    private Api mApi;

    public Check(Api api) {
        Log.d(TAG, "Check");
        this.mApi = api;
    }

    @Override
    public void execute() {
        Log.d(TAG, "execute");
        PreferenceHelper mPreferenceHelper = new PreferenceHelper();
        String token = mPreferenceHelper.getUserTitlte();

        mApi.verify(new TokenModel(token));
    }
}
