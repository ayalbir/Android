package com.example.myyoutube.managers;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;

import androidx.room.Room;

import com.example.myyoutube.AppDB;
import com.example.myyoutube.VideoDao;
import com.example.myyoutube.classes.Comment;
import com.example.myyoutube.classes.Video;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class VideoManager {
    private static VideoManager videoManager;
    private static List<Video> videos;
    private AppDB db;
    private VideoDao videoDao;

    private VideoManager(Context context) {
        db = Room.databaseBuilder(context.getApplicationContext(),
                        AppDB.class, "VideoDB")
                .allowMainThreadQueries()
                .build();
        videoDao = db.videoDao();
        videos = new ArrayList<>();
        videos.addAll(videoDao.getAllVideos());
    }

    public static VideoManager getInstance(Context context) {
        if (videoManager == null) {
            videoManager = new VideoManager(context);
        }
        return videoManager;
    }

    public List<Video> getVideos() {
        return videos;
    }

    public void setVideos(List<Video> videos) {
        this.videos = videos;
    }

    public Video getVideoById(int id) {
        return videoDao.getVideoById(id);
    }

    public List<Video> getVideosByUserEmail(String userEmail) {
        return videoDao.getVideosByUserEmail(userEmail);
    }

    public void addVideo(Video video) {
        videoDao.insert(video);
        videos.add(video);
    }

    public void updateVideo(Video updatedVideo) {
        videoDao.update(updatedVideo);
        for (int i = 0; i < videos.size(); i++) {
            if (videos.get(i).getId() == updatedVideo.getId()) {
                videos.set(i, updatedVideo);
                return;
            }
        }
    }

    public static void updateCommentsEmail(String oldEmail, String newEmail) {
        for (Video video : videos) {
            for (Comment comment : video.getComments()) {
                if (comment.getCommentPublisher().equalsIgnoreCase(oldEmail)) {
                    comment.setCommentPublisher(newEmail);
                }
            }
        }
    }

    public void removeVideosByUser(String email) {
        Iterator<Video> iterator = videos.iterator();
        while (iterator.hasNext()) {
            Video video = iterator.next();
            if (video.getChannelEmail().equalsIgnoreCase(email)) {

                videoDao.delete(video);
                iterator.remove();
            } else {
                video.getComments().removeIf(comment -> comment.getCommentPublisher().equalsIgnoreCase(email));
            }
        }
    }

    public void removeVideo(Video video) {
        videoDao.delete(video);
        videos.remove(video);
    }

    public static List<Video> parseVideosFromJSON(String jsonData) {
        Gson gson = new Gson();
        JsonObject jsonObject = JsonParser.parseString(jsonData).getAsJsonObject();
        Type videoListType = new TypeToken<ArrayList<Video>>() {}.getType();
        List<Video> videoList = gson.fromJson(jsonObject.get("Videos"), videoListType);
        for (Video video : videoList) {
            if (video.getComments() == null) {
                video.setComments(new ArrayList<>());
            }
        }
        return videoList;
    }

    public static Bitmap decodeImage(String encodedImage) {
        if (encodedImage != null) {
            byte[] decodedBytes = Base64.decode(encodedImage, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        }
        return null;
    }
    public static String loadJSONFromAsset(Context context) {
        String json = null;
        try {
            InputStream is = context.getResources().getAssets().open("data.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return json;
    }

}
