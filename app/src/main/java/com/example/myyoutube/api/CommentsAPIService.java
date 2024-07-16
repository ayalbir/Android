package com.example.myyoutube.api;

import com.google.gson.JsonObject;
import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface CommentsAPIService {

    @GET("/api/comments/{videoId}")
    Call<ArrayList<JsonObject>> getCommentsByVideoId(@Path("videoId") int videoId);

    @POST("/api/comments")
    Call<JsonObject> addComment(@Body JsonObject comment);

    @PUT("/api/comments/{id}")
    Call<JsonObject> updateComment(@Path("id") String id, @Body JsonObject comment);

    @DELETE("/api/comments/{id}")
    Call<JsonObject> deleteComment(@Path("id") String id);
}
