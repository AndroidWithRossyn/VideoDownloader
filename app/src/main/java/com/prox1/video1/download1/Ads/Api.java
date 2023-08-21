package com.prox1.video1.download1.Ads;

import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface Api {

    String BASE_URL = "https://sdk1.jbcodertechnolab.com/";

    /**
     * The return type is important here
     * The class structure that you've defined in Call<T>
     * should exactly match with your json response
     * If you are not using another api, and using the same as mine
     * then no need to worry, but if you have your own API, make sure
     * you change the return type appropriately
     **/
//    @POST("get_all_data.php")
//    Call<List<Ads>> getAds();

    @POST("get_all_data.php")
//    Call<List<Ads>> getAds();
    Call<List<Ads>> getAds(@Body RequestBody body);
}
