package com.example.myyoutube;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

import com.example.myyoutube.classes.Video;

@Database(entities = {Video.class}, version = 2)
public abstract class AppDB extends RoomDatabase {

    public abstract VideoDao videoDao();

}
