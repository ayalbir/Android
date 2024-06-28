package com.example.myyoutube.classes;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class Video {
    private static int idCounter = 0;
    private final int id;
    private String channelEmail;
    private int likes;
    private int dislikes;
    private int views;
    private String title;
    private String description;
    private final Date date;
    private int duration;
    private String thumbnail;
    private String mp4file;
    private List<Comment> comments;
    private boolean liked;
    private boolean disliked;

    public boolean isLiked() {
        return this.liked;
    }

    public void setLiked(boolean isLiked) {
        this.liked = isLiked;
    }

    public boolean isDisliked() {
        return this.disliked;
    }

    public void setDisliked(boolean isDisliked) {
        this.disliked = isDisliked;
    }

    public Video() {
        this.id = ++idCounter;
        this.comments = new ArrayList<>();
        this.date = new Date();
    }

    public Video(String channelEmail, String title, String description, int duration, String thumbnail, String mp4file, List<Comment> comments) {
        this.id = ++idCounter;
        this.channelEmail = channelEmail;
        this.title = title;
        this.description = description;
        this.duration = duration;
        this.thumbnail = thumbnail;
        this.mp4file = mp4file;
        this.liked = false;
        this.disliked = false;
        this.date = new Date();
        if (comments != null) {
            this.comments = new ArrayList<>(comments);
        } else {
            this.comments = new ArrayList<>();
        }
    }

    public Video(String channelEmail, String title, String description, int duration, int likes, int dislikes, int views, String thumbnail, String mp4file, List<Comment> comments) {
        this(channelEmail, title, description, duration, thumbnail, mp4file, comments);
        this.likes = likes;
        this.dislikes = dislikes;
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

    public int getDislikes() {
        return dislikes;
    }

    public void setDislikes(int dislikes) {
        this.dislikes = dislikes;
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

    public String getChannelEmail() {
        return channelEmail;
    }

    public void setChannelEmail(String channelEmail) {
        this.channelEmail = channelEmail;
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

    public void incrementDislikes() {
        this.dislikes++;
    }

    public void decrementDislikes() {
        if (this.dislikes > 0) {
            this.dislikes--;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Video video = (Video) o;
        return id == video.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public String getTimeAgo() {
        long duration = System.currentTimeMillis() - date.getTime();
        long seconds = TimeUnit.MILLISECONDS.toSeconds(duration);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(duration);
        long hours = TimeUnit.MILLISECONDS.toHours(duration);
        long days = TimeUnit.MILLISECONDS.toDays(duration);

        if (seconds < 60) {
            return seconds + " seconds ago";
        } else if (minutes < 60) {
            return minutes + " minutes ago";
        } else if (hours < 24) {
            return hours + " hours ago";
        } else if (days < 7) {
            return days + " days ago";
        } else if (days < 30) {
            return (days / 7) + " weeks ago";
        } else if (days < 365) {
            return (days / 30) + " months ago";
        } else {
            return (days / 365) + " years ago";
        }
    }

    public Date getDate() {
        return date;
    }
}
