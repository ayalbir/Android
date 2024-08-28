package com.example.myyoutube.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.example.myyoutube.TokenService;
import com.example.myyoutube.entities.Comment;
import com.example.myyoutube.entities.Video;
import com.example.myyoutube.repositories.VideoRepository;

import java.util.ArrayList;
import java.util.List;

public class VideosViewModel extends ViewModel {
    private static VideosViewModel instance;
    private final VideoRepository videoRepository;
    String token = TokenService.getInstance().getToken();
    private LiveData<List<Video>> videosLiveData;

    public VideosViewModel() {
        videoRepository = new VideoRepository();
        videosLiveData = new LiveData<List<Video>>() {
            @Override
            public void observeForever(@NonNull Observer<? super List<Video>> observer) {
                super.observeForever(observer);
                get();
            }
        };
    }

    public static VideosViewModel getInstance() {
        if (instance == null) {
            instance = new VideosViewModel();
        }
        return instance;
    }

    public LiveData<List<Video>> get() {
        videosLiveData = videoRepository.getAllVideos();
        return videosLiveData;
    }

    public void add(Video video) {
        videoRepository.addVideo(video, token);
    }

    public void update(Video video) {
        videoRepository.updateVideo(video, token);
    }

    public void delete(Video video) {
        videoRepository.deleteVideo(video, token);
    }

    public Video getVideoById(String id) {
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

    public List<Video> getVideosByUserEmail(String email) {
        return videoRepository.getVideoDao().getVideosByUserEmail(email);
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

    public void likeVideo(Video video) {
        videoRepository.likeVideo(video.getEmail(), video.getId(), token);
    }

    public void disLikeVideo(Video video) {
        videoRepository.disLikeVideo(video.getEmail(), video.getId(), token);
    }

    public void updateViews(Video video) {
        videoRepository.updateViews(video.getId());
    }

}
