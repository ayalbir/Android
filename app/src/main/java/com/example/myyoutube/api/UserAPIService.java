package com.example.myyoutube.api;

import com.example.myyoutube.entities.User;
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

public interface UserAPIService {

    @POST("api/tokens")
    Call<JsonObject> signIn(@Body JsonObject requestBody);

    @POST("api/users")
    Call<JsonObject> createUser(@Body JsonObject requestBody);

    @GET("api/users/{email}")
    Call<JsonObject> getUserByEmail(@Path("email") String email);


    @PUT("api/users/{email}")
    Call<JsonObject> updateUser(@Path("email") String email, @Body JsonObject requestBody, @Header("Authorization") String token);

    @DELETE("api/users/{email}")
    Call<JsonObject> deleteUser(@Path("email") String email, @Header("Authorization") String token);

    @GET("api/users")
    Call<List<User>> getAllUsers();
}
