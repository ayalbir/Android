package com.example.myyoutube.classes;

import java.util.ArrayList;
import java.util.List;

public class VideoManager {
    private static VideoManager videoManager;
    private static List<Video> videos;

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

    public List<Video> getVideosByUserEmail(String userEmail) {
        List<Video> userVideos = new ArrayList<>();
        for (Video video : videos) {
            if (video.getChannelEmail().equals(userEmail)) {
                userVideos.add(video);
            }
        }
        return userVideos;
    }

    public void addVideo(Video video) {
        videos.add(video);
    }

    public void updateVideo(Video updatedVideo) {
        for (int i = 0; i < videos.size(); i++) {
            if (videos.get(i).getId() == updatedVideo.getId()) {
                videos.set(i, updatedVideo);
                return;
            }
        }
    }

    public void removeVideo(Video video) {
        videos.remove(video);
    }
}
