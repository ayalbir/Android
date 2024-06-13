package com.example.myyoutube.classes;

import java.util.ArrayList;
import java.util.List;

public class Video{
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
    private static List<Comment> comments;
    private boolean liked;

    public boolean isLiked() {
        return this.liked;
    }

    public void setLiked(boolean isLiked1) {
        this.liked= isLiked1;
    }

    public Video() {
        this.id = ++idCounter;
        this.comments = new ArrayList<>();
    }

    public Video(String channel, String title, String description, int duration, String thumbnail, String mp4file, List<Comment> comments) {
        this.id = ++idCounter;
        this.channel = channel;
        this.title = title;
        this.description = description;
        this.duration = duration;
        this.thumbnail = thumbnail;
        this.mp4file = mp4file;
        this.liked = false;
        if (comments != null) {
            this.comments = comments;
        }
    }

    public Video(String channel, String title, String description, int duration, int likes, int views, String thumbnail, String mp4file, List<Comment> comments) {
        this(channel, title, description, duration, thumbnail, mp4file, comments);
        this.likes = likes;
        this.views = views;
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

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getMp4file() {
        return mp4file;
    }

    public void setMp4file(String mp4file) {
        this.mp4file = mp4file;
    }


    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public void addComment(Comment comment) {
        this.comments.add(comment);
    }
    public void incrementViews() {
        this.views++;
    }

    public void incrementLikes() {
        this.likes++;
    }

    public void decrementLikes() {
        if (this.likes > 0) {
            this.likes--;
        }
    }
}
