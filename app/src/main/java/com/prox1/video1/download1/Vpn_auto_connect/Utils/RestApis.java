package com.prox1.video1.download1.Vpn_auto_connect.Utils;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RestApis {
    @GET("/")
    Call<ApiResponse> requestip(@Query("format") String str);
}