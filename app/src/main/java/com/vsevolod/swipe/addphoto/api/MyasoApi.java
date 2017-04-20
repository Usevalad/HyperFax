package com.vsevolod.swipe.addphoto.api;

import com.vsevolod.swipe.addphoto.model.query.AuthModel;
import com.vsevolod.swipe.addphoto.model.query.CommitModel;
import com.vsevolod.swipe.addphoto.model.query.ListModel;
import com.vsevolod.swipe.addphoto.model.query.SimpleAuthModel;
import com.vsevolod.swipe.addphoto.model.query.TokenModel;
import com.vsevolod.swipe.addphoto.model.responce.CheckedInfo;
import com.vsevolod.swipe.addphoto.model.responce.ListResponse;
import com.vsevolod.swipe.addphoto.model.responce.ResponseFlowsTreeModel;
import com.vsevolod.swipe.addphoto.model.responce.UserInfoModel;
import com.vsevolod.swipe.addphoto.model.responce.UserModel;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;

/**
 * Created by vsevolod on 07.04.17.
 */

public interface MyasoApi {
    @POST("ext/apit/tree")
    Call<ResponseFlowsTreeModel> getTree(@Body TokenModel model);

    @POST("ext/apit/auth")
    Call<UserModel> authenticate(@Body AuthModel user);

    @POST("ext/apit/auth")
    Call<UserInfoModel> authenticate(@Body SimpleAuthModel user);

    @POST("ext/apit/check")
    Call<CheckedInfo> verify(@Body TokenModel user);

     //    @Headers("Content-Length: ?")
    //ext/api/upload
    @PUT("ext/apit/upload")
    Call<ResponseBody> postImage(@Body RequestBody body);

    @POST("ext/apit/commit")//ext/api/commit
    Call<ResponseBody> commit(@Body CommitModel commitModel);

    @POST("ext/apit/list")
    Call<ListResponse> list(@Body ListModel listModel);
}


