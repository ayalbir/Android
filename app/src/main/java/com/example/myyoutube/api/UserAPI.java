package com.example.myyoutube.api;

import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;

import com.example.myyoutube.Helper;
import com.example.myyoutube.R;
import com.example.myyoutube.TokenService;
import com.example.myyoutube.dao.UserDao;
import com.example.myyoutube.entities.User;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Time;
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
    UserDao userDao;

    public UserAPI(UserDao userDao) {
        

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
                .client(httpClient.build())
                .build();

        this.userDao = userDao;
        userServiceAPI = retrofit.create(UserAPIService.class);
    }

    public void getAllUsers(MutableLiveData<List<User>> usersListData) {
        Log.e("CurrentTime: ", String.valueOf(System.currentTimeMillis()));
        Call<List<User>> call = userServiceAPI.getAllUsers();
        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful()) {
                    List<User> users = response.body();
                    if (users != null && userDao != null) {
                        if (!userDao.getAllUsers().isEmpty()) {
                            userDao.clear();
                        }
                        for (User user : users) {
                            userDao.insert(user);
                        }
                        usersListData.postValue(users);
                    } else {
                        Log.e("VideoAPI", "Failed to fetch videos");
                    }
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Log.e("UserAPI", t.getLocalizedMessage());
            }
        });
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
                    Helper.getConnectedUser().setId(response.body().get("_id").getAsString());
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

}
