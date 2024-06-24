package com.example.myyoutube.managers;

import com.example.myyoutube.classes.User;
import com.example.myyoutube.classes.Video;

import java.util.ArrayList;
import java.util.List;

public class UserManager {
    private static final List<User> users = new ArrayList<>();

    public static List<User> getUsers() {
        return users;
    }

    public static void addUser(User user) {
        users.add(user);
    }

    public static User getUserByEmail(String email) {
        for (User user : users) {
            if (user.getEmail().equalsIgnoreCase(email)) {
                return user;
            }
        }
        return null;
    }

    public static boolean isEmailExist(String email) {
        for (User user : users) {
            if (user.getEmail().equalsIgnoreCase(email)) {
                return true;
            }
        }
        return false;
    }
    public static void updateUserEmail(String oldEmail, String newEmail) {
        for (User user : users) {
            if (user.getEmail().equalsIgnoreCase(oldEmail)) {
                user.setEmail(newEmail);
                updateUserVideosChannelEmail(oldEmail, newEmail);
                VideoManager.updateCommentsEmail(oldEmail, newEmail);
                break;
            }
        }
    }
    private static void updateUserVideosChannelEmail(String oldEmail, String newEmail) {
        for (Video video : VideoManager.getVideoManager().getVideos()) {
            if (video.getChannelEmail().equalsIgnoreCase(oldEmail)) {
                video.setChannelEmail(newEmail);
            }
        }
    }

    // UserManager.java
    public static void removeUser(String email) {
        User user = getUserByEmail(email);

    }

    public static void removeUser(User user) {
        if (user != null) {
            users.remove(user);
            VideoManager.removeVideosByUser(user.getEmail());
        }
    }
}
