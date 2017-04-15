package com.vsevolod.swipe.addphoto.command.method;

import android.util.Log;

import com.vsevolod.swipe.addphoto.command.Api;
import com.vsevolod.swipe.addphoto.command.Command;
import com.vsevolod.swipe.addphoto.config.PreferenceHelper;
import com.vsevolod.swipe.addphoto.model.query.CommitModel;
import com.vsevolod.swipe.addphoto.model.realm.DataModel;

/**
 * Created by vsevolod on 15.04.17.
 */

public class Commit implements Command {
    private final String TAG = "GetTree";
    private Api mApi;
    private PreferenceHelper mPreferenceHelper = new PreferenceHelper();

    public Commit(Api Api) {
        Log.d(TAG, "GetTree");
        this.mApi = Api;
    }

    @Override
    public void execute() {
        Log.d(TAG, "execute");
    }

    public void execute(DataModel model) {
        String token = mPreferenceHelper.getToken();
        // TODO: 15.04.17 add more attributes in CommitModel.java
        CommitModel commitModel = new CommitModel(
                token,
                model.getServerPhotoURL(),
                model.getUid()
        );

        mApi.commit(commitModel);
    }
}
