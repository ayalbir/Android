package com.example.myyoutube.classes;

import java.util.ArrayList;
import java.util.List;

public class UserManager {
    private static List<User> users = new ArrayList<>();

    public static List<User> getUsers() {
        return users;
    }

    public static void addUser(User user) {
        users.add(user);
    }

    public static User getUserByEmail(String email) {
        for (User user : users) {
            if (user.getEmail().equals(email)) {
                return user;
            }
        }
        return null;
    }

    public static boolean isEmailExist(String email) {
        for (User user : users) {
            if (user.getEmail().equals(email)) {
                return true;
            }
        }
        return false;
    }
    public static void updateUser(User updatedUser) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getEmail().equals(updatedUser.getEmail())) {
                users.set(i, updatedUser);
                return;
            }
        }
    }

    public static void removeUser(User user) {
        users.remove(user);
    }
}