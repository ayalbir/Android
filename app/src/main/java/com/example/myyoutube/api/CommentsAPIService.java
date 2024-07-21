package com.example.myyoutube.api;

import com.google.gson.JsonObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface CommentsAPIService {

    @GET("api/videos/{videoId}/comments")
    Call<ArrayList<JsonObject>> getCommentsByVideoId(@Path("videoId") String videoId);
    @POST("api/videos/{videoId}/comments")
    Call<JsonObject> addComment(@Path("videoId") String videoId, @Body JsonObject comment, @Header("Authorization") String token);

    @PUT("api/comments/{id}")
    Call<JsonObject> updateComment(@Path("id") String id, @Body JsonObject comment, @Header("Authorization") String token);

    @DELETE("api/videos/{pid}/comments/{cid}")
    Call<JsonObject> deleteComment(@Path("pid") String pid, @Path("cid") String cid, @Header("Authorization") String token);
}
