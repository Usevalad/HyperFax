package com.unnamed.b.atv.sample;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by vsevolod on 05.04.17.
 */

public interface MyasoApi {
    @POST("/api/tree")
    Call<ModelList> getData(@Query("token") String token );
}