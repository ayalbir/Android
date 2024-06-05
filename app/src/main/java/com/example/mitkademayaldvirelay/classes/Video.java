package com.example.mitkademayaldvirelay.classes;

import com.example.mitkademayaldvirelay.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Video implements Serializable {

    private static int idCounter = 0;
    private final int id;
    private String channel;
    private int likes;
    private int views;
    private String title;
    private String description;
    private int duration;
    private String thumbnail;
    private String mp4file;
    private List<String> comments;

    public List<String> getComments() {
        return comments;
    }

    public void setComments(List<String> comments) {
        this.comments = comments;
    }

    public Video() {
        this.id = ++idCounter;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public Video(String channel, String title, String description, int duration, String thumbnail, String mp4file,List<String> comments) {
        this.id = ++idCounter;
        this.channel = channel;
        likes = 0;
        views = 0;
        this.title = title;
        this.description = description;
        this.duration = duration;
        this.thumbnail = thumbnail;
        this.mp4file = mp4file;
        if(comments != null)
            this.comments = comments;
        else
            new ArrayList<>();
    }

    public Video(String channel, String title, String description, int duration,int likes, int views, String thumbnail, String mp4file,List<String> comments) {
        this.id = ++idCounter;
        this.channel = channel;
        this.likes = likes;
        this.views = views;
        this.title = title;
        this.description = description;
        this.duration = duration;
        this.thumbnail = thumbnail;
        this.mp4file = mp4file;
        if(comments != null)
            this.comments = comments;
        else
            new ArrayList<>();
    }


    public int getId() {
        return id;
    }

    public int getLikes() {
        return likes;
    }
    public void addComment(String comment) {
        comments.add(comment);
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
    public void incrementViews() {
        views++;
    }
    public void incrementLikes() {
        likes++;
    }

    public String getMp4file() {
        return mp4file;
    }

    public void setMp4file(String mp4file) {
        this.mp4file = mp4file;
    }

    public void decrementLikes() {
        if(getLikes() > 0)
            likes--;
    }
}
