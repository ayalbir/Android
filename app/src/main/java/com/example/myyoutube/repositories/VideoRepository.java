package com.example.myyoutube.repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Room;

import com.example.myyoutube.AppDB;
import com.example.myyoutube.Helper;
import com.example.myyoutube.dao.VideoDao;
import com.example.myyoutube.api.VideoAPI;
import com.example.myyoutube.entities.Video;

import java.util.List;

public class VideoRepository {
    public static VideoDao videoDao;
    private VideoListData videoListData;
    private final VideoAPI videoAPI;
    private AppDB db;

    public VideoRepository() {
        new Thread(() -> {
            db = Room.databaseBuilder(Helper.context, AppDB.class, "FootubeDB")
                    .fallbackToDestructiveMigration()
                    .build();
            videoDao = db.videoDao();
            videoListData = new VideoListData();
        }).start();

        videoAPI = new VideoAPI();
        reloadVideos();
    }

    class VideoListData extends MutableLiveData<List<Video>> {
        public VideoListData() {
            super();
            new Thread(() -> {
                if (videoDao != null) {
                    List<Video> videos = videoDao.getAllVideos();
                    postValue(videos);
                }
            }).start();
        }

        @Override
        protected void onActive() {
            super.onActive();
            reloadVideos();
        }
    }

    public LiveData<List<Video>> getAllVideos() {
        return videoListData;
    }

    public void addVideo(final Video video, String token) {
        videoAPI.addVideo(video, videoListData, token);
    }

    public void updateVideo(final Video video, String token) {
        videoAPI.editVideo(video, videoListData, token);
    }

    public void deleteVideo(final Video video, String token) {
        videoAPI.deleteVideo(video, videoListData, token);
    }

    public void reloadVideos() {
        videoAPI.getVideos(videoListData);
    }

    public LiveData<Video> getVideoById(int videoId) {
        return videoDao.getVideoById(videoId);
    }
}
