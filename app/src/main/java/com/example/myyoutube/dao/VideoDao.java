package com.example.myyoutube.dao;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.myyoutube.entities.Video;

@Dao
public interface VideoDao {

    @Query("SELECT * FROM videos")
    List<Video> getAllVideos();

    @Query("SELECT * FROM videos WHERE _id = :id")
    Video getVideoById(String id);

    @Query("SELECT * FROM videos WHERE email = :email")
    List<Video> getVideosByUserEmail(String email);

    @Insert
    void insert(Video... videos);

    @Update
    void update(Video... videos);

    @Delete
    void delete(Video... videos);

    @Query("DELETE FROM videos WHERE email = :email")
    void deleteVideosByUserEmail(String email);
}
