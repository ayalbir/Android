package com.example.myyoutube.repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Room;

import com.example.myyoutube.AppDB;
import com.example.myyoutube.Helper;
import com.example.myyoutube.VideoDao;
import com.example.myyoutube.api.VideoAPI;
import com.example.myyoutube.classes.Video;

import java.util.List;

public class VideoRepository {
    public static VideoDao videoDao;
    private VideoListData videoListData;
    private final VideoAPI videoAPI;
    private AppDB db;

    public VideoRepository() {
        new Thread(() -> {
            db = Room.databaseBuilder(Helper.context, AppDB.class, "FootubeDB")
                    .fallbackToDestructiveMigration().build();
            videoDao = db.videoDao();
            videoListData = new VideoListData();
        }).start();

        videoAPI = new VideoAPI();
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

    public void addVideo(final Video video) {
        videoAPI.addVideo(video, new MutableLiveData<>());
    }

    public void updateVideo(final Video video) {
        videoAPI.editVideo(video, new MutableLiveData<>());
    }

    public void deleteVideo(final Video video) {
        videoAPI.deleteVideo(video, new MutableLiveData<>());
    }

    public void reloadVideos() {
        //videoAPI.getVideos(Helper.getToken(), videoListData);
    }

    public LiveData<Video> getVideoById(int id) {
        MutableLiveData<Video> videoData = new MutableLiveData<>();
        new Thread(() -> {
            Video video = videoDao.getVideoById(id);
            videoData.postValue(video);
        }).start();
        return videoData;
    }

    public LiveData<List<Video>> getVideosByUserEmail(String userEmail) {
        MutableLiveData<List<Video>> videosByUserEmail = new MutableLiveData<>();
        new Thread(() -> {
            List<Video> videos = videoDao.getVideosByUserEmail(userEmail);
            videosByUserEmail.postValue(videos);
        }).start();
        return videosByUserEmail;
    }
}
