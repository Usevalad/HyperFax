package com.vsevolod.swipe.addphoto.api;

import com.vsevolod.swipe.addphoto.model.responce.UserInfoModel;
import com.vsevolod.swipe.addphoto.model.query.AuthModel;
import com.vsevolod.swipe.addphoto.model.responce.CheckedInfo;
import com.vsevolod.swipe.addphoto.model.responce.FlowsTreeModel;
import com.vsevolod.swipe.addphoto.model.query.SimpleAuthModel;
import com.vsevolod.swipe.addphoto.model.query.TokenModel;
import com.vsevolod.swipe.addphoto.model.responce.UserModel;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by vsevolod on 07.04.17.
 */

public interface MyasoApi {
    @POST("api/tree")
    Call<FlowsTreeModel> getList(@Body TokenModel model);

    @POST("api/auth")
    Call<UserModel> authenticate(@Body AuthModel user);

    @POST("api/auth")
    Call<UserInfoModel> authenticate(@Body SimpleAuthModel user);

    @POST("api/check")
    Call<CheckedInfo> verify(@Body TokenModel user);
}
