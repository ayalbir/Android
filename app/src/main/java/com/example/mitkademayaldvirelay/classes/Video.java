package com.example.mitkademayaldvirelay.classes;

import com.example.mitkademayaldvirelay.R;

public class Video {

    private static int idCounter = 0;
    private final int id;
    private String channel;
    private int likes;
    private int views;
    private String title;
    private String description;
    private int duration;
    private int thumbnail;

    public Video() {
        this.id = ++idCounter;
        this.thumbnail = R.drawable.login;
    }

    public int getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(int thumbnail) {
        this.thumbnail = thumbnail;
    }



    public Video(String channel, String title, String description, int duration, int thumbnail) {
        this.id = ++idCounter;
        this.channel = channel;
        this.likes = 0;
        this.views = 0;
        this.title = title;
        this.description = description;
        this.duration = duration;
        this.thumbnail = thumbnail;
    }

    public int getId() {
        return id;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getChannel() {
        return channel;
    }
}
