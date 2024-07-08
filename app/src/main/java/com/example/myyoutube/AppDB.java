package com.example.myyoutube;


import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

import com.example.myyoutube.classes.Video;
import com.example.myyoutube.VideoDao;

@Database(entities = {Video.class}, version = 1)
public abstract class AppDB extends RoomDatabase {

    private static AppDB instance;

    public abstract VideoDao videoDao();

    public static synchronized AppDB getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDB.class, "video_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
