package com.example.myyoutube.api;

import android.util.Log;
import android.widget.Toast;
import androidx.lifecycle.MutableLiveData;
import com.example.myyoutube.Converters;
import com.example.myyoutube.Helper;
import com.example.myyoutube.R;
import com.example.myyoutube.classes.Comment;
import com.example.myyoutube.classes.Video;
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

public class VideoAPI {
    Retrofit retrofit;
    VideoApiService webServiceAPI;

    public VideoAPI() {
        retrofit = new Retrofit.Builder()
                .baseUrl(Helper.context.getString(R.string.BaseUrl))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        webServiceAPI = retrofit.create(VideoApiService.class);
    }

    public void getVideos(String token, MutableLiveData<List<Video>> allVideos) {
        Call<ArrayList<JsonObject>> call = webServiceAPI.getVideos("Bearer " + token);
        call.enqueue(new Callback<ArrayList<JsonObject>>() {
            @Override
            public void onResponse(Call<ArrayList<JsonObject>> call, Response<ArrayList<JsonObject>> response) {
                if (response.isSuccessful()) {
                    ArrayList<JsonObject> jsonVideosList = response.body();
                    if (jsonVideosList != null) {
                        List<Video> videos = new ArrayList<>();
                        for (JsonObject jsonVideo : jsonVideosList) {
                            int videoId = Integer.parseInt(jsonVideo.get("_id").getAsString());
                            String channelEmail = jsonVideo.get("email").getAsString();
                            String create_date = jsonVideo.get("createdAt").getAsString();
                            String description = jsonVideo.get("description").getAsString();
                            String picBase64 = jsonVideo.get("pic").getAsString();
                            String title = jsonVideo.get("title").getAsString();
                            String urlBase64 = jsonVideo.get("url").getAsString();
                            String date = create_date.substring(0, 10);
                            String picString = picBase64.substring(picBase64.indexOf(',') + 1);
                            String pic = Converters.base64ToFilePath(picString, "image");

                            // Save the video file and get the file path
                            String urlString = urlBase64.substring(urlBase64.indexOf(',') + 1);
                            String url = Converters.base64ToFilePath(urlString, "video");

                            Video video = new Video(channelEmail, title, description, pic, url, new ArrayList<Comment>());
                            video.setId(videoId);
                            video.setDate(date);

                            // Set likedBy and dislikedBy if they exist
                            if (jsonVideo.has("likedBy")) {
                                List<String> likedBy = Converters.fromStringToStringList(jsonVideo.get("likedBy").getAsString());
                                video.setLikedBy(likedBy);
                            }

                            if (jsonVideo.has("dislikedBy")) {
                                List<String> dislikedBy = Converters.fromStringToStringList(jsonVideo.get("dislikedBy").getAsString());
                                video.setDislikedBy(dislikedBy);
                            }
                            videos.add(video);
                        }

                        new Thread(() -> {
                            for (Video video : VideoRepository.videoDao.getAllVideos()) {
                                Converters.deleteFileFromStorage(video.getPic());
                                VideoRepository.videoDao.delete(video);
                            }
                            for (Video video1 : videos) {
                                VideoRepository.videoDao.insert(video1);
                            }
                        }).start();
                        allVideos.postValue(videos);
                    } else {
                        Log.e("VideoAPI", "Failed to save image to internal storage");
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<JsonObject>> call, Throwable t) {
                Log.e("VideoAPI", t.getLocalizedMessage());
            }
        });
    }

    public void addVideo(Video videoToAdd, MutableLiveData<List<Video>> allVideos) {
        try {
            String token = videoToAdd.getEmail();
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
                            getVideos(token, allVideos);
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

    public void editVideo(Video videoToEdit, MutableLiveData<List<Video>> allVideos) {
        String token = videoToEdit.getEmail();
        JSONObject requestBodyJson = new JSONObject();
        try {
            requestBodyJson.put("id", videoToEdit.getId());
            requestBodyJson.put("email", videoToEdit.getEmail());
            requestBodyJson.put("createdAt", videoToEdit.getDate());
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
                        getVideos(token, allVideos);
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

    public void deleteVideo(Video videoToRemove, MutableLiveData<List<Video>> allVideos) {
        String channelEmail = videoToRemove.getEmail();
        int id = videoToRemove.getId();
        String token = videoToRemove.getEmail();
        Call<JsonObject> call = webServiceAPI.deleteVideo(channelEmail, id, "Bearer " + token);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject jsonObject = response.body();
                    if (jsonObject != null && jsonObject.has("deletedCount")) {
                        Converters.deleteFileFromStorage(videoToRemove.getPic());
                        new Thread(() -> VideoRepository.videoDao.delete(videoToRemove)).start();
                        getVideos(token, allVideos);
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
