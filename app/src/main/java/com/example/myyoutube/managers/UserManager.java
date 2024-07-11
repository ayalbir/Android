package com.example.myyoutube.managers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.example.myyoutube.Helper;
import com.example.myyoutube.R;
import com.example.myyoutube.classes.User;
import com.example.myyoutube.classes.Video;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class UserManager {
    private static final List<User> users = new ArrayList<>();
    private static VideoManager videoManager;
    public  static User currentUser;

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
        for (Video video : videoManager.getVideos()) {
            if (video.getChannelEmail().equalsIgnoreCase(oldEmail)) {
                video.setChannelEmail(newEmail);
            }
        }
    }

    public static void removeUser(String email) {
        User user = getUserByEmail(email);
        removeUser(user);
    }

    public static void removeUser(User user) {
        if (user != null) {
            users.remove(user);
            videoManager.removeVideosByUser(user.getEmail());
        }
    }
    private static String encodeImageToBase64(int resId) {
        Bitmap bitmap = BitmapFactory.decodeResource(Helper.context.getResources(), resId);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }
    public static void initializeDefaultUsers() {

        if (UserManager.getUsers().isEmpty()) {
            User user1 = new User("Elay@gmail.com", "Elay", "12345678p", encodeImageToBase64(R.drawable.person1));
            User user2 = new User("Ayal@gmail.com", "Ayal", "12345678p", encodeImageToBase64(R.drawable.person2));
            User user3 = new User("Dvir@gmail.com", "Dvir", "12345678p", encodeImageToBase64(R.drawable.person3));

            UserManager.addUser(user1);
            UserManager.addUser(user2);
            UserManager.addUser(user3);
        }

    }
}
