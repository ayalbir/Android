package com.example.myyoutube.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.myyoutube.TokenService;
import com.example.myyoutube.entities.Comment;
import com.example.myyoutube.repositories.CommentRepository;

import java.util.List;

public class CommentViewModel extends ViewModel {
    private CommentRepository commentRepository;
    private LiveData<List<Comment>> commentsLiveData;
    private String token;

    public CommentViewModel() {
        token = TokenService.getInstance().getToken();
    }

    public void init(String videoId) {
        if (commentRepository != null) {
            return; // ViewModel is created per Activity, so videoId won't change
        }
        commentRepository = new CommentRepository(videoId);
        commentsLiveData = commentRepository.getCommentsForVideo();
    }

    public LiveData<List<Comment>> getCommentsForVideo() {
        return commentsLiveData;
    }

    public void addComment(Comment comment) {
        commentRepository.addComment(comment, token);
    }

    public void updateComment(Comment comment) {
        commentRepository.updateComment(comment, token);
    }

    public void deleteComment(Comment comment) {
        commentRepository.deleteComment(comment, token);
    }
}
