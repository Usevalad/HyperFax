package com.vsevolod.swipe.addphoto.command;

import com.vsevolod.swipe.addphoto.model.query.AuthModel;
import com.vsevolod.swipe.addphoto.model.query.SimpleAuthModel;
import com.vsevolod.swipe.addphoto.model.query.TokenModel;

import retrofit2.http.Body;

/**
 * Created by vsevolod on 11.04.17.
 */

public interface Api {
    void getTree(@Body TokenModel model);

    void authenticate(@Body AuthModel user);

    void authenticate(@Body SimpleAuthModel user);

    void verify(@Body TokenModel user);
}
