package com.example.myyoutube.api;



import com.example.myyoutube.classes.Video;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface VideoApiService {

    @GET("videos")
    Call<List<Video>> getVideos();

    @POST("videos")
    Call<Void> createVideo(@Body Video video);

    @PUT("videos/{id}")
    Call<Void> updateVideo(@Path("id") int id, @Body Video video);

    @DELETE("videos/{id}")
    Call<Void> deleteVideo(@Path("id") int id);
}