package com.bvh.dev.checkapi;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RetrofitAPI {
    private static String BASE_URL = "http://144.202.9.52/";
    private static Retrofit retrofit;

    private static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static RetrofitInterface getService() {
        return getRetrofitInstance().create(RetrofitInterface.class);
    }

    public static String getBaseUrl() {
        return BASE_URL;
    }

    public static void setRetrofit(Retrofit retrofit) {
        RetrofitAPI.retrofit = retrofit;
    }

    public static void setBaseUrl(String url) {
        BASE_URL = url;
    }
}
