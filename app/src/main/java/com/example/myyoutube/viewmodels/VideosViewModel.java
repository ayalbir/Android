package com.example.myyoutube.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myyoutube.TokenService;
import com.example.myyoutube.entities.Comment;
import com.example.myyoutube.entities.Video;
import com.example.myyoutube.repositories.VideoRepository;

import java.util.ArrayList;
import java.util.List;

public class VideosViewModel extends ViewModel {
    private static VideosViewModel instance;
    private MutableLiveData<List<Video>> videosLiveData;
    private final VideoRepository videoRepository;

    public VideosViewModel() {
        videoRepository = new VideoRepository();
        videosLiveData = new MutableLiveData<>(new ArrayList<>());
        loadVideos();
    }

    public static VideosViewModel getInstance() {
        if (instance == null) {
            instance = new VideosViewModel();
        }
        return instance;
    }

    public LiveData<List<Video>> get() {
        return videosLiveData;
    }

    String token = TokenService.getInstance().getToken();
    public void add(Video video) {
        videoRepository.addVideo(video ,token);
        loadVideos();
    }

    public void update(Video video) {
        videoRepository.updateVideo(video, token);
        loadVideos();
    }

    public void delete(Video video) {
        videoRepository.deleteVideo(video, token);
        loadVideos();
    }

    public void loadVideos() {
        videoRepository.reloadVideos();
    }

    public LiveData<Video> getVideoById(int id) {
        return videoRepository.getVideoById(id);
    }

    public void updateCommentsEmail(String oldEmail, String newEmail) {
        List<Video> videos = videosLiveData.getValue();
        if (videos != null) {
            for (Video video : videos) {
                for (Comment comment : video.getComments()) {
                    if (comment.getEmail().equalsIgnoreCase(oldEmail)) {
                        comment.setEmail(newEmail);
                    }
                }
                update(video);
            }
        }
    }

    public LiveData<List<Video>> getVideosByUserEmail(String email) {
        MutableLiveData<List<Video>> userVideosLiveData = new MutableLiveData<>();
        List<Video> userVideos = new ArrayList<>();

        for (Video video : videosLiveData.getValue()) {
            if (video.getEmail().equals(email)) {
                userVideos.add(video);
            }
        }

        userVideosLiveData.postValue(userVideos);
        return userVideosLiveData;
    }

    public void removeVideosByUser(String email) {
        List<Video> videosToRemove = new ArrayList<>();

        for (Video video : videosLiveData.getValue()) {
            if (video.getEmail().equals(email)) {
                videosToRemove.add(video);
            }
        }

        for (Video video : videosToRemove) {
            videoRepository.deleteVideo(video, TokenService.getInstance().getToken());
        }
    }

}
