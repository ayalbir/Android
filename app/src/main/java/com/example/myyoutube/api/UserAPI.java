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
import com.example.myyoutube.viewmodels.UserManager;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UserAPI {
    Retrofit retrofit;
    UserAPIService userServiceAPI;

    public UserAPI() {

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(chain -> {
            Request original = chain.request();

            // Add the JWT token to the request headers
            Request request = original.newBuilder()
                    .header("Authorization", "Bearer " + TokenService.getInstance().getToken())
                    .method(original.method(), original.body())
                    .build();

            return chain.proceed(request);
        });

        retrofit = new Retrofit.Builder()
                .baseUrl(Helper.context.getString(R.string.BaseUrl))
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build()).build();


        userServiceAPI = retrofit.create(UserAPIService.class);
    }

    /*public UserAPI() {
        retrofit = new Retrofit.Builder()
                .baseUrl(Helper.context.getString(R.string.BaseUrl))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        userServiceAPI = retrofit.create(UserAPIService.class);
    }*/

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
    public void getUserByEmail(String email, MutableLiveData<User> userLiveData) {
        Call<JsonObject> call = userServiceAPI.getUserByEmail(email);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject jsonObject = response.body();
                    if (jsonObject != null) {
                        String email = jsonObject.get("email").getAsString();
                        String password = jsonObject.get("password").getAsString();
                        String firstName = jsonObject.get("firstName").getAsString();
                        String familyName = jsonObject.get("familyName").getAsString();
                        String birthdate = jsonObject.get("birthdate").getAsString();
                        String gender = jsonObject.get("gender").getAsString();
                        String profileImageBase64 = jsonObject.get("profileImage").getAsString();
                        String profileImage = profileImageBase64.substring(profileImageBase64.indexOf(',') + 1);

                        User user = new User(email, password, firstName, familyName, UserManager.getTempDate(), gender, profileImage);
                        userLiveData.postValue(user);
                    } else {
                        Toast.makeText(Helper.context, "Failed to fetch user data", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(Helper.context, "Failed to fetch user data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("UserAPI", t.getLocalizedMessage());
                // Toast.makeText(Helper.context, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void updateUser(String email, User user, String token, MutableLiveData<String> messageLiveData) {
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

        Call<JsonObject> call = userServiceAPI.updateUser(email, (JsonObject) JsonParser.parseString(requestBodyJson.toString()), "Bearer " + token);
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

    public void deleteUser(String email, String token, MutableLiveData<String> messageLiveData) {
        Call<JsonObject> call = userServiceAPI.deleteUser(email, "Bearer " + token);
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
