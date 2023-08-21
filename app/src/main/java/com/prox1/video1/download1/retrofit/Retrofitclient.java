package com.prox1.video1.download1.retrofit;


import com.prox1.video1.download1.admobmanager.MyApplication;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Retrofitclient {
    private static Retrofit retrofit;
    private static final String BASE_URL = MyApplication.Base_Url;
    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
