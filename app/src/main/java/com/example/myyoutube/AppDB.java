package com.example.myyoutube;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;


import com.example.myyoutube.dao.UserDao;
import com.example.myyoutube.dao.VideoDao;
import com.example.myyoutube.entities.Comment;
import com.example.myyoutube.entities.User;
import com.example.myyoutube.entities.Video;

@Database(entities = {Video.class, User.class}, version = 14)
@TypeConverters(Converters.class)
public abstract class AppDB extends RoomDatabase {
    public abstract VideoDao videoDao();
    public abstract UserDao userDao();
}

