package com.example.myyoutube;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.myyoutube.classes.Video;

@Dao
public interface VideoDao {

    @Query("SELECT * FROM video")
    List<Video> getAllVideos();

    @Query("SELECT * FROM video WHERE id = :id")
    Video getVideoById(int id);

    @Query("SELECT * FROM video WHERE channelEmail = :email")
    List<Video> getVideosByUserEmail(String email);

    @Insert
    void insert(Video... videos);

    @Update
    void update(Video... videos);

    @Delete
    void delete(Video... videos);

    @Query("DELETE FROM video WHERE channelEmail = :email")
    void deleteVideosByUserEmail(String email);
}
