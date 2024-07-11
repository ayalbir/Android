package com.example.myyoutube.api;



import com.example.myyoutube.classes.Video;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface VideoApiService {
    @GET("videos")
    Call<ArrayList<JsonObject>> getVideos(@Header("authorization") String token);

    @GET("users/{email}/videos")
    Call<ArrayList<JsonObject>> getUserVideos(@Path("email") String email, @Header("authorization") String token);

    @POST("users/{email}/videos")
    Call<JsonObject> createVideo(@Path("email") String email, @Body Object jsonVideo, @Header("authorization") String token);

    @PUT("users/{email}/videos/{vid}")
    Call<JsonObject> updateVideo(@Path("email") String email, @Path("vid") int videoId, @Body Object jsonVideo, @Header("authorization") String token);

    @DELETE("users/{email}/videos/{vid}")
    Call<JsonObject> deleteVideo(@Path("email") String email, @Path("vid") int videoId, @Header("authorization") String token);
}