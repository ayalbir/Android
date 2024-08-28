package com.example.myyoutube.viewmodels;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.myyoutube.TokenService;
import com.example.myyoutube.entities.User;
import com.example.myyoutube.repositories.UserRepository;

import java.time.LocalDate;
import java.util.List;

public class UserViewModel extends ViewModel {
    public static User connectedUser;
    public static LocalDate tempDate;
    private static UserViewModel instance;
    private final UserRepository userRepository;
    private final VideosViewModel videosViewModel;
    public boolean firstTime;

    public UserViewModel() {
        this.userRepository = new UserRepository();
        connectedUser = null;
        videosViewModel = VideosViewModel.getInstance();
        firstTime = true;
        get();
    }

    public static UserViewModel getInstance() {
        if (instance == null) {
            instance = new UserViewModel();
        }
        return instance;
    }

    public static User getConnectedUser() {
        return connectedUser;
    }

    public static void setConnectedUser(User user) {
        connectedUser = user;
    }

    public static LocalDate getTempDate() {
        return tempDate;
    }

    public static void setTempDate(LocalDate tempDate) {
        UserViewModel.tempDate = tempDate;
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

    public boolean isFirstTime() {
        return firstTime;
    }

    public void getUsersFromDao() {
        userRepository.getUsersFromDao();
    }

    public LiveData<List<User>> get() {
        firstTime = false;
        return userRepository.get();
    }
}
