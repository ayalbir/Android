package com.example.myyoutube.classes;

import java.util.ArrayList;
import java.util.List;

public class User {

    private String email;

    private String userName;
    private String password;
    private String profileImage;

    private List<Integer> likedVideos;

    public User(String email, String userName, String password, String profileImage) {
        this.email = email;
        this.userName = userName;
        this.password = password;
        this.profileImage = profileImage;
        this.likedVideos = new ArrayList<>();
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }
    public List<Integer> getLikedVideos() {
        return likedVideos;
    }

    public void addLikedVideo(int videoId) {
        if (!likedVideos.contains(videoId)) {
            likedVideos.add(videoId);
        }
    }

    public void removeLikedVideo(int videoId) {
        likedVideos.remove(Integer.valueOf(videoId));
    }

    public boolean hasLikedVideo(int videoId) {
        return likedVideos.contains(videoId);
    }
}