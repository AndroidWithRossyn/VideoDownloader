package com.prox1.video1.download1.Vpn_auto_connect.Utils;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIClient {
    private static Retrofit retrofit;

    public static Retrofit getRetrofitInstance(String str) {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder().baseUrl(str).addConverterFactory(GsonConverterFactory.create()).build();
        }
        return retrofit;
    }
}