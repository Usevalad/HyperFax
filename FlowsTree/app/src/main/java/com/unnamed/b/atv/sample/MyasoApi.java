package com.unnamed.b.atv.sample;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by vsevolod on 05.04.17.
 */

public interface MyasoApi {
    @POST("/api/tree")
    Call<ModelList> getData(@Body PostModel body);
}