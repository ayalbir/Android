package com.example.myyoutube.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.myyoutube.Converters;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Entity
@TypeConverters({Converters.class})
public class Video {
    @PrimaryKey()
    @NonNull
    private String _id;
    private String email;
    private int likes;
    private int dislikes;
    private int views;
    private String title;
    private String description;
    private String createdAt;
    private String pic;
    private String url;
    private List<Comment> comments;
    private List<String> likedBy;
    private List<String> dislikedBy;

    public Video(@NonNull String _id , String email, String title, String description, String pic, String url, List<Comment> comments) {
        this._id = _id;
        this.email = email;
        this.title = title;
        this.description = description;
        this.pic = pic;
        this.url = url;
        this.createdAt = new Date().toString();
        if (comments != null) {
            this.comments = new ArrayList<>(comments);
        } else {
            this.comments = new ArrayList<>();
        }
        this.likedBy = new ArrayList<>();
        this.dislikedBy = new ArrayList<>();
    }

    public String getId() {
        return _id;
    }
    public void setId(String id) {
        this._id = id;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(String createdAt){
        this.createdAt = createdAt;
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
        return _id.equals(video._id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_id);
    }

    @Override
    public String toString() {
        return "Video{" +
                "id=" + _id +
                ", email='" + email + '\'' +
                ", likes=" + likes +
                ", dislikes=" + dislikes +
                ", views=" + views +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", pic='" + pic + '\'' +
                ", url='" + url + '\'' +
                ", comments=" + comments +
                ", likedBy=" + likedBy +
                ", dislikedBy=" + dislikedBy +
                '}';
    }

    public String getTimeAgo() {
        long duration = System.currentTimeMillis() - new Date(createdAt).getTime();
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
