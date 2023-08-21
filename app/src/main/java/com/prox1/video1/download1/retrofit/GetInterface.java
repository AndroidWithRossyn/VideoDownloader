package com.prox1.video1.download1.retrofit;



import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface GetInterface {
    @POST("com.prox1_video1_download1_KvRD9j.php?")
    Call<Example> getStatus(@Query("package_name") String str , @Query("status") int app_status, @Query(value = "api_key", encoded = true) String key);

    @POST("com.prox1_video1_download1_KvRD9j.php?")
    Call<Example> getDebugStatus(@Query("package_name") String str , @Query("status") int app_status, @Query(value = "api_key", encoded = true) String key, @Query("debug") boolean IsDebug);
}
