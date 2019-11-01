package com.example.riley.androidfoodhub.Remote;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by riley on 12/23/2017.
 */

public class RetrofitClient {
    private static Retrofit retrofit = null;
    private static Retrofit retrofit1 = null;
    public static Retrofit getClient(String baseURL)
    {
        if(retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseURL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static Retrofit getGoogleClient(String basURL)
    {
        if(retrofit1 == null) {
            retrofit1 = new Retrofit.Builder()
                    .baseUrl(basURL)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build();
        }
        return retrofit1;
    }
}
