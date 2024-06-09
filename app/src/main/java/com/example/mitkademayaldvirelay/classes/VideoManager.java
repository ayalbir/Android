package com.example.mitkademayaldvirelay.classes;

import java.util.ArrayList;
import java.util.List;

public class VideoManager {
    private static VideoManager videoManager;
    private List<Video> videos;

    private VideoManager() {
        videos = new ArrayList<>();
    }

    public static VideoManager getVideoManager() {
        if (videoManager == null) {
            videoManager = new VideoManager();
        }
        return videoManager;
    }

    public List<Video> getVideos() {
        return videos;
    }

    public void setVideos(List<Video> videos) {
        this.videos = videos;
    }

    public Video getVideoById(int id) {
        for (Video video : videos) {
            if (video.getId() == id) {
                return video;
            }
        }
        return null;
    }
}
