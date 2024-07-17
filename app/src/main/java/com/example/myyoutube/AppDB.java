package com.example.myyoutube;

import androidx.room.Database;
import androidx.room.RoomDatabase;


import com.example.myyoutube.dao.VideoDao;
import com.example.myyoutube.entities.Video;

@Database(entities = {Video.class}, version = 5)
public abstract class AppDB extends RoomDatabase {
    public abstract VideoDao videoDao();
}

