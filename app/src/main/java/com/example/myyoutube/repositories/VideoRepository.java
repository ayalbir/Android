package com.example.myyoutube.repositories;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myyoutube.AppDB;
import com.example.myyoutube.MyApplication;
import com.example.myyoutube.VideoDao;
import com.example.myyoutube.api.VideoAPI;
import com.example.myyoutube.classes.Video;

import java.util.LinkedList;
import java.util.List;

public class VideoRepository {
    private VideoDao videoDao;
    private VideoListData videoListData;
    private VideoAPI videoAPI;

    public VideoRepository() {
        AppDB db = AppDB.getInstance(MyApplication.context);
        videoDao = db.videoDao();
        videoListData = new VideoListData();
        videoAPI = new VideoAPI(videoListData, videoDao);
    }

    class VideoListData extends MutableLiveData<List<Video>> {

        public VideoListData() {
            super();
            setValue(new LinkedList<>());

        }

        @Override
        protected void onActive() {
            super.onActive();

           new Thread(() -> {
              videoListData.postValue(videoDao.getAllVideos());
           }).start();

            videoAPI = new VideoAPI(videoListData, videoDao);
            videoAPI.getVideos();
        }
    }

    public LiveData<List<Video>> getAllVideos() {
        return videoListData;
    }

    public void addVideo(final Video video) {
        videoAPI.createVideo(video);
    }

    public void updateVideo(final Video video) {
        videoAPI.updateVideo(video.getId(), video);
    }

    public void deleteVideo(final Video video) {
        videoAPI.deleteVideo(video.getId());
    }

    public void reloadVideos() {
        videoAPI.getVideos();
    }
}
