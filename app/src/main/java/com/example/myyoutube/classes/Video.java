package com.example.myyoutube.classes;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.myyoutube.Converters;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Entity
public class Video {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String channelEmail;
    private int likes;
    private int dislikes;
    private int views;
    private String title;
    private String description;

    private String date;
    private String pic;
    private String url;
    @TypeConverters(Converters.class)
    private List<Comment> comments;
    private boolean liked;
    private boolean disliked;
    @TypeConverters(Converters.class)
    private List<String> likedBy;
    @TypeConverters(Converters.class)
    private List<String> dislikedBy;

    public Video() {
        this.comments = new ArrayList<>();
        this.date = new Date().toString();
        this.likedBy = new ArrayList<>();
        this.dislikedBy = new ArrayList<>();
    }

    @Ignore
    public Video(String channelEmail, String title, String description, String pic, String url, List<Comment> comments) {
        this.channelEmail = channelEmail;
        this.title = title;
        this.description = description;
        this.pic = pic;
        this.url = url;
        this.liked = false;
        this.disliked = false;
        this.date = new Date().toString();
        if (comments != null) {
            this.comments = new ArrayList<>(comments);
        } else {
            this.comments = new ArrayList<>();
        }
        this.likedBy = new ArrayList<>();
        this.dislikedBy = new ArrayList<>();
    }

    @Ignore
    public Video(String channelEmail, String title, String description, int likes, int dislikes, int views, String pic, String url, List<Comment> comments) {
        this(channelEmail, title, description, pic, url, comments);
        this.likes = likes;
        this.dislikes = dislikes;
        this.views = views;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
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

    public String getChannelEmail() {
        return channelEmail;
    }

    public void setChannelEmail(String channelEmail) {
        this.channelEmail = channelEmail;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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

    public String getDate() {
        return date;
    }
    public void setDate(String date){
        this.date = date;
    }
    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    public boolean isDisliked() {
        return disliked;
    }

    public void setDisliked(boolean disliked) {
        this.disliked = disliked;
    }

    public List<String> getLikedBy() {
        return likedBy;
    }

    public void setLikedBy(List<String> likedBy) {
        this.likedBy = likedBy;
    }

    public List<String> getDislikedBy() {
        return dislikedBy;
    }

    public void setDislikedBy(List<String> dislikedBy) {
        this.dislikedBy = dislikedBy;
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

    @Override
    public String toString() {
        return "Video{" +
                "id=" + id +
                ", channelEmail='" + channelEmail + '\'' +
                ", likes=" + likes +
                ", dislikes=" + dislikes +
                ", views=" + views +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", date='" + date + '\'' +
                ", pic='" + pic + '\'' +
                ", url='" + url + '\'' +
                ", comments=" + comments +
                ", liked=" + liked +
                ", disliked=" + disliked +
                ", likedBy=" + likedBy +
                ", dislikedBy=" + dislikedBy +
                '}';
    }

    public String getTimeAgo() {
        long duration = System.currentTimeMillis() - new Date(date).getTime();
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
}
