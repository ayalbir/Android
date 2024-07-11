package com.example.myyoutube.repositories;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Room;

import com.example.myyoutube.AppDB;
import com.example.myyoutube.Helper;
import com.example.myyoutube.VideoDao;
import com.example.myyoutube.api.VideoAPI;
import com.example.myyoutube.classes.Video;

import java.util.LinkedList;
import java.util.List;

public class VideoRepository {
    public static VideoDao videoDao;
    private VideoListData videoListData;
    private VideoAPI videoAPI;
    private AppDB db;

    public VideoRepository() {
        new Thread(() -> {
            db = Room.databaseBuilder(Helper.context, AppDB.class, "FootubeDB").
                    fallbackToDestructiveMigration().build();
            videoDao = db.videoDao();
        }).start();

        videoAPI = new VideoAPI();
        videoListData = new VideoListData();

    }

    class VideoListData extends MutableLiveData<List<Video>> {
        public VideoListData() {
            super();
            new Thread(() -> {
                if (videoDao!=null) {
                    List<Video> posts = videoDao.getAllVideos();
                    videoListData.postValue(posts);
                }
            }).start();

        }

        protected void onActive() {
            super.onActive();

            new Thread(() -> {
                if (videoDao != null) {
                    videoListData.postValue(videoDao.getAllVideos());
                }
            }).start();
        }
    }

    public LiveData<List<Video>> getAllVideos() {
        return videoListData;
    }

//    public void addVideo(final Video video) {
//        videoAPI.createVideo(video);
//    }
//
//    public void updateVideo(final Video video) {
//        videoAPI.updateVideo(video.getId(), video);
//    }
//
//    public void deleteVideo(final Video video) {
//        videoAPI.deleteVideo(video.getId());
//    }
//
//    public void reloadVideos() {
//        videoAPI.getVideos(videoListData);
//    }
    public LiveData<Video> getVideoById(String id) {
//        videoAPI.getVideoById(videoListData, id);
//        return videoListData;
        return null;
    }
}
