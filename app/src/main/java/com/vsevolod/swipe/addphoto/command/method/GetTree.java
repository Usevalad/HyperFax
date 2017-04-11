package com.vsevolod.swipe.addphoto.command.method;

import android.util.Log;

import com.vsevolod.swipe.addphoto.command.Api;
import com.vsevolod.swipe.addphoto.command.Command;
import com.vsevolod.swipe.addphoto.config.MyApplication;
import com.vsevolod.swipe.addphoto.config.PreferenceHelper;
import com.vsevolod.swipe.addphoto.model.query.TokenModel;

/**
 * Created by vsevolod on 11.04.17.
 */

public class GetTree implements Command {
    private final String TAG = "GetTree";
    private Api mApi;

    public GetTree(Api Api) {
        Log.d(TAG, "GetTree");
        this.mApi = Api;
    }

    @Override
    public void execute() {
        Log.d(TAG, "execute");
        PreferenceHelper mPreferenceHelper = new PreferenceHelper();
        String token = mPreferenceHelper.getToken();
        mApi.getTree(new TokenModel(token));
    }
}
