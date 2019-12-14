package com.bvh.dev.checkapi;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface RetrofitInterface {
    @GET("getStoryByTrend")
    Call<List<MyStory>> getStoryByTrend();
}
