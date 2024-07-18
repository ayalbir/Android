package com.example.myyoutube.repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Room;

import com.example.myyoutube.AppDB;
import com.example.myyoutube.Helper;
import com.example.myyoutube.api.CommentsAPI;
import com.example.myyoutube.dao.CommentDao;
import com.example.myyoutube.entities.Comment;

import java.util.List;

public class CommentRepository {
    private CommentDao commentDao;
    private CommentsAPI commentsAPI;
    private MutableLiveData<List<Comment>> commentsLiveData;
    private AppDB db;

    public CommentRepository() {
        new Thread(() -> {
            db = Room.databaseBuilder(Helper.context, AppDB.class, "FootubeDB")
                    .fallbackToDestructiveMigration()
                    .build();
            commentDao = db.commentDao();
        }).start();

        commentsAPI = new CommentsAPI(commentDao);
        commentsLiveData = new MutableLiveData<>();
    }

    public LiveData<List<Comment>> getCommentsByVideoId(String videoId) {
        loadCommentsFromServer(videoId);
        return commentsLiveData;
    }

    private void loadCommentsFromServer(String videoId) {
        commentsAPI.fetchCommentsByVideoId(videoId, commentsLiveData);
    }

    public MutableLiveData<List<Comment>> getCommentsLiveData() {
        return commentsLiveData;
    }

    public void addComment(Comment comment) {
        commentsAPI.addComment(comment, new MutableLiveData<>());
    }

    public void updateComment(Comment comment) {
        commentsAPI.updateComment(comment, new MutableLiveData<>());
    }

    public void deleteComment(Comment comment) {
        commentsAPI.deleteComment((comment.getVideoId()), new MutableLiveData<>());
    }
}
