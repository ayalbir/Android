package com.example.myyoutube.api;

import com.example.myyoutube.entities.User;
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

public interface UserAPIService {

    @POST("/api/auth/signin")
    Call<JsonObject> signIn(@Body JsonObject requestBody);

    @POST("/api/users")
    Call<JsonObject> createUser(@Body JsonObject requestBody);

    @PUT("/api/users/{id}")
    Call<JsonObject> updateUser(@Path("id") int id, @Body JsonObject requestBody, @Header("Authorization") String token);
    @GET("/api/users/{id}")
    Call<User> getUserById(@Path("id") int id, @Header("Authorization") String token);
    @DELETE("/api/users/{id}")
    Call<JsonObject> deleteUser(@Path("id") int id, @Header("Authorization") String token);

    @GET("/api/users")
    Call<ArrayList<User>> getAllUsers();
}
