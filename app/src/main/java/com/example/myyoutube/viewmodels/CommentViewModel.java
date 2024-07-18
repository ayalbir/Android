package com.example.myyoutube.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.example.myyoutube.Helper;
import com.example.myyoutube.TokenService;
import com.example.myyoutube.entities.Comment;
import com.example.myyoutube.entities.Video;
import com.example.myyoutube.repositories.CommentRepository;

import java.util.List;

public class CommentViewModel extends ViewModel {
    private CommentRepository commentRepository;
    private LiveData<List<Comment>> commentsLiveData;

    public CommentViewModel(String videoId) {
        commentRepository = new CommentRepository(videoId);
        commentsLiveData = commentRepository.get();
    }

    String token = TokenService.getInstance().getToken();
    public void getCommentsForVideo() {
        commentRepository.getCommentsForVideo();
    }
    public LiveData<List<Comment>> get(){
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
