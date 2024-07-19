package com.example.myyoutube.viewmodels;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import com.example.myyoutube.TokenService;
import com.example.myyoutube.entities.User;
import com.example.myyoutube.entities.Video;
import com.example.myyoutube.repositories.UserRepository;

import java.io.StringReader;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public class UserManager {
    private static UserManager instance;
    private final UserRepository userRepository;
    private LiveData<List<User>> usersLiveData;
    public static User connectedUser;
    private VideosViewModel videosViewModel;
    public static LocalDate tempDate;

    public UserManager() {
        this.userRepository = new UserRepository();
        this.connectedUser = null;
        videosViewModel = VideosViewModel.getInstance();
    }
    public static UserManager getInstance() {
        if (instance == null) {
            instance = new UserManager();
        }
        return instance;
    }

    public static User getConnectedUser() {
        return connectedUser;
    }

    public static void setConnectedUser(User user) {
        connectedUser = user;
    }

    public void clearConnectedUser() {
        connectedUser = null;
    }

    public void signIn(String email, String password) {
        userRepository.signIn(email, password);
    }

    public void createUser(User user) {
        userRepository.createUser(user);
    }

    public User getUserByEmail(String email) {
        return userRepository.getUserByEmail(email);
    }

    public void updateUser(String email, User user) {
        userRepository.updateUser(email, user, TokenService.getInstance().getToken());
    }

    public void deleteUser(String email) {
        userRepository.deleteUser(email, TokenService.getInstance().getToken());
        videosViewModel.removeVideosByUser(email);
    }

    public void getAllUsers() {
        userRepository.getAllUsers();
    }

    public static LocalDate getTempDate() {
        return tempDate;
    }

    public static void setTempDate(LocalDate tempDate) {
        UserManager.tempDate = tempDate;
    }
}
