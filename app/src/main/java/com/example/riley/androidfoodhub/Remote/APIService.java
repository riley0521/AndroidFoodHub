package com.example.riley.androidfoodhub.Remote;



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
                    "Authorization:key=AAAAPrlvedY:APA91bHi0AZS1CWWrva4pl718tPn7Ybvh9iMwM1wDFIqv7z_rCbclsrUQZh5i24v1QYBo1FHyhJNklFGJzDT2wozezM9IWjEd1msRNtzajNvS1ZpO4dT-tHTEm3MHIbkPk2r2BuSAtUZ"
            }
    )
    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body DataMessage body);
}
