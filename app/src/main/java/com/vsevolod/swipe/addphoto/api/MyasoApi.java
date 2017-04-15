package com.vsevolod.swipe.addphoto.api;

import com.vsevolod.swipe.addphoto.model.query.AuthModel;
import com.vsevolod.swipe.addphoto.model.query.CommitModel;
import com.vsevolod.swipe.addphoto.model.query.SimpleAuthModel;
import com.vsevolod.swipe.addphoto.model.query.TokenModel;
import com.vsevolod.swipe.addphoto.model.responce.CheckedInfo;
import com.vsevolod.swipe.addphoto.model.responce.ResponseFlowsTreeModel;
import com.vsevolod.swipe.addphoto.model.responce.UserInfoModel;
import com.vsevolod.swipe.addphoto.model.responce.UserModel;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;

/**
 * Created by vsevolod on 07.04.17.
 */

public interface MyasoApi {
    @POST("api/tree")
    Call<ResponseFlowsTreeModel> getList(@Body TokenModel model);

    @POST("api/auth")
    Call<UserModel> authenticate(@Body AuthModel user);

    @POST("api/auth")
    Call<UserInfoModel> authenticate(@Body SimpleAuthModel user);

    @POST("api/check")
    Call<CheckedInfo> verify(@Body TokenModel user);

    @Multipart //    @Headers("Content-Length: ?")
    @PUT("api/upload")//// FIXME: 15.04.17 do i need @Part("name")?
    Call<ResponseBody> postImage(@Part MultipartBody.Part image, @Part("name") RequestBody name);

    @POST("api/commit")
    Call<ResponseBody> commit(@Body CommitModel commitModel);
}


