package com.example.myyoutube.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myyoutube.entities.Comment;
import com.example.myyoutube.repositories.CommentRepository;

import java.util.List;

public class CommentViewModel extends ViewModel {
    private CommentRepository commentRepository;
    private MutableLiveData<List<Comment>> commentsLiveData;
    private MutableLiveData<String> messageLiveData;

    public CommentViewModel() {
        commentRepository = new CommentRepository();
        commentsLiveData = (MutableLiveData<List<Comment>>) commentRepository.getCommentsLiveData();
        messageLiveData = (MutableLiveData<String>) commentRepository.getMessageLiveData();
    }

    public LiveData<List<Comment>> getCommentsLiveData() {
        return commentsLiveData;
    }

    public LiveData<String> getMessageLiveData() {
        return messageLiveData;
    }

    public void fetchCommentsByVideoId(String videoId) {
        commentRepository.fetchCommentsByVideoId(videoId);
    }

    public void addComment(Comment comment) {
        commentRepository.addComment(comment);
    }

    public void updateComment(Comment comment) {
        commentRepository.updateComment(comment);
    }

    public void deleteComment(Comment comment) {
        commentRepository.deleteComment((comment.getVideoId()));
    }
}
