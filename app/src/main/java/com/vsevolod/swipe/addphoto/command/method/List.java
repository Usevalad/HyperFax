package com.vsevolod.swipe.addphoto.command.method;

import android.util.Log;

import com.vsevolod.swipe.addphoto.command.Api;
import com.vsevolod.swipe.addphoto.command.Command;
import com.vsevolod.swipe.addphoto.model.query.ListModel;

/**
 * Created by vsevolod on 20.04.17.
 */

public class List implements Command {
    private final String TAG = "List";
    private Api mApi;

    public List(Api mApi) {
        Log.d(TAG, "List");
        this.mApi = mApi;
    }

    public void execute(ListModel listModel) {
        Log.d(TAG, "execute");
        mApi.list(listModel);
    }

    @Override
    public void execute() {
        Log.e(TAG, "execute: wrong method");
    }
}
