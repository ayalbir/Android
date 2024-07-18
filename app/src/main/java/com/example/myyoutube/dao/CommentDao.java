package com.example.myyoutube.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.myyoutube.entities.Comment;

import java.util.List;

@Dao
public interface CommentDao {

    @Insert
    void insert(Comment comment);

    @Update
    void update(Comment comment);

    @Query("DELETE FROM Comments WHERE _id = :commentId")
    void deleteById(String commentId);

    @Query("SELECT * FROM comments WHERE videoId = :videoId")
    List<Comment> getCommentsByVideoId(String videoId);

    @Query("SELECT * FROM comments")
    List<Comment> getAllComments();
}
