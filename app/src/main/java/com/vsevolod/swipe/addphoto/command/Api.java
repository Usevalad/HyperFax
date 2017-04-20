package com.vsevolod.swipe.addphoto.command;

import com.vsevolod.swipe.addphoto.model.query.AuthModel;
import com.vsevolod.swipe.addphoto.model.query.CommitModel;
import com.vsevolod.swipe.addphoto.model.query.ListModel;
import com.vsevolod.swipe.addphoto.model.query.SimpleAuthModel;
import com.vsevolod.swipe.addphoto.model.query.TokenModel;

import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.Part;

/**
 * Created by vsevolod on 11.04.17.
 */

public interface Api {
    void getTree(@Body TokenModel model);

    void authenticate(@Body AuthModel user);

    void authenticate(@Body SimpleAuthModel user);

    void verify(@Body TokenModel user);

    void uploadImage(@Part RequestBody body);

    void commit(@Body CommitModel commitModel);

    void list(@Body ListModel listModel);
}
