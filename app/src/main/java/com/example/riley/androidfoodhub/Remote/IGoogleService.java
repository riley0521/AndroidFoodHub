package com.example.riley.androidfoodhub.Remote;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created by riley on 1/21/2018.
 */

public interface IGoogleService {
    @GET
    Call<String> getAddressName(@Url String url);
}
