package com.example.myyoutube.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myyoutube.Helper;
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
        videosLiveData = new MutableLiveData<>();
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

    public List<Video> getVideosFromDao() {
        return videoRepository.getVideoDao().getAllVideos();
    }

    public void clearVideoDao() {
         videoRepository.clearVideoDao();
    }

    public LiveData<List<Video>> getSuggestedVideos(String videoId) {
        videosLiveData = videoRepository.getSuggestedVideos(videoId);
        return videosLiveData;
    }

    public LiveData<List<Video>> getVideosByUserEmail(String email) {
        return videoRepository.getVideosByUserEmail(email);
    }

    public List<Video> getVideosByEmailFromDao(String email) {
        return videoRepository.getVideoDao().getVideosByUserEmail(email);
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

    public void deleteVideosByEmail() {
        videoRepository.deleteVideosByEmail(Helper.getConnectedUser().getEmail(), token);
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
