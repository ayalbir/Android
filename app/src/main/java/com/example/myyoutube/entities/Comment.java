package com.example.myyoutube.entities;

import androidx.room.PrimaryKey;

import java.util.Date;

public class Comment {

    @PrimaryKey(autoGenerate = true)
    private String videoId;
    private String text;
    private String profilePicture;
    private String email;
    private Date createdAt;

    public Comment(String id,String text, String profilePicture, String email) {
        this.videoId = id;
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

    public String getVideoId() {
        return videoId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
