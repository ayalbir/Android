package com.example.myyoutube.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.myyoutube.Converters;
import com.example.myyoutube.viewmodels.UserManager;

import java.time.LocalDate;

@Entity(tableName = "comments")
@TypeConverters({Converters.class})
public class Comment {

    private String videoId;

    @PrimaryKey
    @NonNull
    private String _id;
    private String text;
    private String profilePicture;
    private String email;
    private LocalDate createdAt;

    public Comment(String videoId, String _id,String text, String profilePicture, String email) {
        this.createdAt = UserManager.getTempDate();
        this.videoId = videoId;
        this._id = _id;
        this.text = text;
        this.profilePicture = profilePicture;
        this.email = email;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public String getVideoId() {
        return videoId;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
