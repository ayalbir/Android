package com.example.myyoutube.api;

import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;

import com.example.myyoutube.Helper;
import com.example.myyoutube.R;
import com.example.myyoutube.TokenService;
import com.example.myyoutube.entities.User;
import com.example.myyoutube.repositories.UserRepository;
import com.example.myyoutube.repositories.VideoRepository;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UserAPI {
    Retrofit retrofit;
    UserAPIService userServiceAPI;


    public UserAPI() {
        retrofit = new Retrofit.Builder()
                .baseUrl(Helper.context.getString(R.string.BaseUrl))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        userServiceAPI = retrofit.create(UserAPIService.class);
    }

    public void signIn(String email, String password) {
        JSONObject requestBodyJson = new JSONObject();
        try {
            requestBodyJson.put("email", email);
            requestBodyJson.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Call<JsonObject> call = userServiceAPI.signIn((JsonObject) JsonParser.parseString(requestBodyJson.toString()));
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject jsonObject = response.body();
                    if (jsonObject != null && jsonObject.has("token")) {
                        String token = jsonObject.get("token").getAsString();
                        TokenService.getInstance().setToken(token);
                    } else {
                        Toast.makeText(Helper.context, "Invalid response from server", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(Helper.context, "Login failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("UserAPI", t.getLocalizedMessage());
                Toast.makeText(Helper.context, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void createUser(User user, MutableLiveData<String> messageLiveData) {
        JSONObject requestBodyJson = new JSONObject();
        try {
            requestBodyJson.put("email", user.getEmail());
            requestBodyJson.put("password", user.getPassword());
            requestBodyJson.put("firstName", user.getFirstName());
            requestBodyJson.put("familyName", user.getFamilyName());
            requestBodyJson.put("birthdate", user.getBirthdate());
            requestBodyJson.put("gender", user.getGender());
            requestBodyJson.put("profileImage", user.getProfileImage());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Call<JsonObject> call = userServiceAPI.createUser((JsonObject) JsonParser.parseString(requestBodyJson.toString()));
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    messageLiveData.postValue("User created successfully");
                } else {
                    messageLiveData.postValue("Failed to create user");
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("UserAPI", t.getLocalizedMessage());
                messageLiveData.postValue("Network error");
            }
        });
    }

    public void getUserById(int id, String token, MutableLiveData<User> userLiveData) {
        Call<User> call = userServiceAPI.getUserById(id,token);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    userLiveData.postValue(response.body());
                } else {
                    Toast.makeText(Helper.context, "Failed to fetch user data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("UserAPI", t.getLocalizedMessage());
                Toast.makeText(Helper.context, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void updateUser(int id, User user, String token, MutableLiveData<String> messageLiveData) {
        JSONObject requestBodyJson = new JSONObject();
        try {
            requestBodyJson.put("email", user.getEmail());
            requestBodyJson.put("password", user.getPassword());
            requestBodyJson.put("firstName", user.getFirstName());
            requestBodyJson.put("familyName", user.getFamilyName());
            requestBodyJson.put("birthdate", user.getBirthdate());
            requestBodyJson.put("gender", user.getGender());
            requestBodyJson.put("profileImage", user.getProfileImage());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Call<JsonObject> call = userServiceAPI.updateUser(id, (JsonObject) JsonParser.parseString(requestBodyJson.toString()), "Bearer " + token);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    messageLiveData.postValue("User updated successfully");
                } else {
                    messageLiveData.postValue("Failed to update user");
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("UserAPI", t.getLocalizedMessage());
                messageLiveData.postValue("Network error");
            }
        });
    }

    public void deleteUser(int id, String token, MutableLiveData<String> messageLiveData) {
        Call<JsonObject> call = userServiceAPI.deleteUser(id, "Bearer " + token);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    messageLiveData.postValue("User deleted successfully");
                } else {
                    messageLiveData.postValue("Failed to delete user");
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("UserAPI", t.getLocalizedMessage());
                messageLiveData.postValue("Network error");
            }
        });
    }

    public void getAllUsers(String token, MutableLiveData<List<User>> usersLiveData) {
        Call<ArrayList<User>> call = userServiceAPI.getAllUsers();
        call.enqueue(new Callback<ArrayList<User>>() {
            @Override
            public void onResponse(Call<ArrayList<User>> call, Response<ArrayList<User>> response) {
                if (response.isSuccessful()) {
                    usersLiveData.postValue(response.body());
                } else {
                    Toast.makeText(Helper.context, "Failed to fetch users", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<User>> call, Throwable t) {
                Log.e("UserAPI", t.getLocalizedMessage());
                Toast.makeText(Helper.context, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
