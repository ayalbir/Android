package com.example.myyoutube.repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;


import com.example.myyoutube.api.CommentsAPI;
import com.example.myyoutube.entities.Comment;

import java.util.List;

public class CommentRepository {
    private CommentsAPI commentAPI;
    private MutableLiveData<List<Comment>> commentsLiveData;
    private MutableLiveData<String> messageLiveData;

    public CommentRepository() {
        commentAPI = new CommentsAPI();
        commentsLiveData = new MutableLiveData<>();
        messageLiveData = new MutableLiveData<>();
    }

    public LiveData<List<Comment>> getCommentsLiveData() {
        return commentsLiveData;
    }

    public LiveData<String> getMessageLiveData() {
        return messageLiveData;
    }

    public void fetchCommentsByVideoId(String videoId) {
        commentAPI.fetchCommentsByVideoId(videoId, commentsLiveData);
    }

    public void addComment(Comment comment) {
        commentAPI.addComment(comment, messageLiveData);
    }

    public void updateComment(Comment comment) {
        commentAPI.updateComment(comment, messageLiveData);
    }

    public void deleteComment(String commentId) {
        commentAPI.deleteComment(commentId, messageLiveData);
    }
}
