package com.example.riley.androidfoodhub.Remote;


import com.example.riley.androidfoodhub.BuildConfig;
import com.example.riley.androidfoodhub.Model.DataMessage;
import com.example.riley.androidfoodhub.Model.MyResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by riley on 12/23/2017.
 */

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=" + BuildConfig.AUTHORIZATION_KEY
            }
    )
    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body DataMessage body);
}
