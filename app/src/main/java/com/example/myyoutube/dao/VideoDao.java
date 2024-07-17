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

    @Query("SELECT * FROM video")
    List<Video> getAllVideos();

    @Query("SELECT * FROM video WHERE _id = :id")
    LiveData<Video> getVideoById(int id);

    @Query("SELECT * FROM video WHERE email = :email")
    List<Video> getVideosByUserEmail(String email);

    @Insert
    void insert(Video... videos);

    @Update
    void update(Video... videos);

    @Delete
    void delete(Video... videos);

    @Query("DELETE FROM video WHERE email = :email")
    void deleteVideosByUserEmail(String email);
}
