package com.example.myyoutube.api;

import android.util.Log;
import android.widget.Toast;
import androidx.lifecycle.MutableLiveData;
import com.example.myyoutube.Converters;
import com.example.myyoutube.Helper;
import com.example.myyoutube.R;
import com.example.myyoutube.TokenService;
import com.example.myyoutube.entities.Comment;
import com.example.myyoutube.entities.Video;
import com.example.myyoutube.repositories.VideoRepository;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class VideoAPI {
    Retrofit retrofit;
    VideoApiService webServiceAPI;
    private MutableLiveData<List<Video>> allVideos;

    public VideoAPI() {

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

        webServiceAPI = retrofit.create(VideoApiService.class);
        allVideos = new MutableLiveData<>();
    }
    public void getVideos(MutableLiveData<List<Video>> allVideos) {
        Call<List<Video>> call = webServiceAPI.getVideos();
        call.enqueue(new Callback<List<Video>>() {
            @Override
            public void onResponse(Call<List<Video>> call, Response<List<Video>> response) {
                if (response.isSuccessful()) {
                    List<Video> videos = response.body();
                    if (videos != null) {
                        new Thread(() -> {
                            for (Video video : VideoRepository.videoDao.getAllVideos()) {
                                Converters.deleteFileFromStorage(video.getPic());
                                VideoRepository.videoDao.delete(video);
                            }
                            for (Video video : videos) {
                                VideoRepository.videoDao.insert(video);
                            }
                        }).start();

                        if (!videos.isEmpty()) {
                            allVideos.postValue(videos);
                        }
                    } else {
                        Log.e("VideoAPI", "Failed to fetch videos");
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Video>> call, Throwable t) {
                Log.e("VideoAPI", t.getLocalizedMessage());
            }
        });
    }


    public void addVideo(Video videoToAdd, MutableLiveData<List<Video>> allVideos, String token) {
        try {
            JSONObject requestBodyJson = new JSONObject();
            requestBodyJson.put("email", videoToAdd.getEmail());
            requestBodyJson.put("description", videoToAdd.getDescription());
            requestBodyJson.put("pic", videoToAdd.getPic());
            requestBodyJson.put("title", videoToAdd.getTitle());
            requestBodyJson.put("url", videoToAdd.getUrl());
            Object jsonParser = JsonParser.parseString(requestBodyJson.toString());

            Call<JsonObject> call = webServiceAPI.createVideo(videoToAdd.getEmail(), jsonParser, "Bearer " + token);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.isSuccessful()) {
                        JsonObject jsonObject = response.body();
                        if (jsonObject != null && jsonObject.has("insertedId")) {
                            new Thread(() -> VideoRepository.videoDao.insert(videoToAdd)).start();
                            getVideos(allVideos);
                        } else {
                            Toast.makeText(Helper.context, "Video cannot be uploaded due to validation failure.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    Log.e("VideoAPI", t.getLocalizedMessage());
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void editVideo(Video videoToEdit, MutableLiveData<List<Video>> allVideos, String token) {
        JSONObject requestBodyJson = new JSONObject();
        try {
            requestBodyJson.put("id", videoToEdit.getId());
            requestBodyJson.put("email", videoToEdit.getEmail());
            requestBodyJson.put("createdAt", videoToEdit.getCreatedAt());
            requestBodyJson.put("description", videoToEdit.getDescription());
            requestBodyJson.put("pic", videoToEdit.getPic());
            requestBodyJson.put("title", videoToEdit.getTitle());
            requestBodyJson.put("url", videoToEdit.getUrl());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Object jsonParser = JsonParser.parseString(requestBodyJson.toString());

        Call<JsonObject> call = webServiceAPI.updateVideo(videoToEdit.getEmail(), videoToEdit.getId(), jsonParser, "Bearer " + token);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject jsonObject = response.body();
                    if (jsonObject != null && jsonObject.has("modifiedCount")) {
                        new Thread(() -> VideoRepository.videoDao.update(videoToEdit)).start();
                        getVideos(allVideos);
                        Toast.makeText(Helper.context, "Video updated", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(Helper.context, "Video cannot be updated due to validation failure.", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("VideoAPI", t.getLocalizedMessage());
            }
        });
    }

    public void deleteVideo(Video videoToRemove, MutableLiveData<List<Video>> allVideos, String token) {
        String channelEmail = videoToRemove.getEmail();
        String id = videoToRemove.getId();
        Call<JsonObject> call = webServiceAPI.deleteVideo(channelEmail, id, "Bearer " + token);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject jsonObject = response.body();
                    if (jsonObject != null && jsonObject.has("deletedCount")) {
                        Converters.deleteFileFromStorage(videoToRemove.getPic());
                        new Thread(() -> VideoRepository.videoDao.delete(videoToRemove)).start();
                        getVideos(allVideos);
                        Toast.makeText(Helper.context, "Video deleted", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("VideoAPI", t.getLocalizedMessage());
            }
        });
    }

}