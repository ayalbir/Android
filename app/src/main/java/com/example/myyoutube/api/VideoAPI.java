package com.example.myyoutube.api;



import androidx.lifecycle.MutableLiveData;

import com.example.myyoutube.MyApplication;
import com.example.myyoutube.R;
import com.example.myyoutube.VideoDao;
import com.example.myyoutube.classes.Video;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class VideoAPI {
    private final MutableLiveData<List<Video>> videoListData;
    private final VideoDao dao;
    Retrofit retrofit;
    VideoApiService videoApiService;

    public VideoAPI(MutableLiveData<List<Video>> videoListData, VideoDao dao) {
        this.videoListData = videoListData;
        this.dao = dao;

        retrofit = new Retrofit.Builder()
                .baseUrl(MyApplication.context.getString(R.string.BaseUrl))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        videoApiService = retrofit.create(VideoApiService.class);
    }

    public void getVideos() {
        Call<List<Video>> call = videoApiService.getVideos();
        call.enqueue(new Callback<List<Video>>() {
            @Override
            public void onResponse(Call<List<Video>> call, Response<List<Video>> response) {
                new Thread(() -> {
                    // dao.clear(); // Clear existing data
                    if (response.body() != null) {
                        // Insert new data
                        dao.insert(response.body().toArray(new Video[0]));
                        // Update LiveData
                        videoListData.postValue(dao.getAllVideos());
                    }
                }).start();
            }

            @Override
            public void onFailure(Call<List<Video>> call, Throwable t) {

                call.cancel();
            }
        });
    }

    public void createVideo(Video video) {
        Call<Void> call = videoApiService.createVideo(video);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                getVideos();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                call.cancel();
            }
        });
    }

    public void updateVideo(int id, Video video) {
        Call<Void> call = videoApiService.updateVideo(id, video);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                getVideos();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                call.cancel();
            }
        });
    }

    public void deleteVideo(int id) {
        Call<Void> call = videoApiService.deleteVideo(id);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                getVideos();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                call.cancel();
            }
        });
    }

}
