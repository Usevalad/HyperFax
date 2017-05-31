package com.vsevolod.swipe.addphoto.api;

import com.vsevolod.swipe.addphoto.model.query.AuthModel;
import com.vsevolod.swipe.addphoto.model.query.CommitModel;
import com.vsevolod.swipe.addphoto.model.query.ListQueryModel;
import com.vsevolod.swipe.addphoto.model.query.TreeQueryModel;
import com.vsevolod.swipe.addphoto.model.responce.AuthResponseModel;
import com.vsevolod.swipe.addphoto.model.responce.CommitResponseModel;
import com.vsevolod.swipe.addphoto.model.responce.ListResponse;
import com.vsevolod.swipe.addphoto.model.responce.ResponseFlowsTreeModel;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.PUT;

/**
 * Created by vsevolod on 07.04.17.
 */
public interface MyasoApi {
    @POST("ext/api/auth")
    Call<AuthResponseModel> authenticate(@Body AuthModel user);

    @POST("ext/api/tree")
    Call<ResponseFlowsTreeModel> getTree(@Body TreeQueryModel model);

    @PUT("ext/api/upload")
    Call<ResponseBody> postImage(@Body RequestBody body);

    @POST("ext/api/commit")
    Call<CommitResponseModel> commit(@Body CommitModel commitModel);

    @POST("ext/api/list")
    Call<ListResponse> getList(@Body ListQueryModel listQueryModel);
}


