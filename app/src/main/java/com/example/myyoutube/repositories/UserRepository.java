package com.example.myyoutube.repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Room;

import com.example.myyoutube.AppDB;
import com.example.myyoutube.Helper;
import com.example.myyoutube.api.UserAPI;
import com.example.myyoutube.api.VideoAPI;
import com.example.myyoutube.dao.UserDao;
import com.example.myyoutube.dao.VideoDao;
import com.example.myyoutube.entities.User;
import com.example.myyoutube.entities.Video;

import java.util.List;

public class UserRepository {

    private UserAPI userAPI;
    private MutableLiveData<List<User>> usersLiveData;
    private MutableLiveData<User> userLiveData;
    private MutableLiveData<String> messageLiveData;

    public UserRepository() {

        usersLiveData = new MutableLiveData<>();
        userAPI = new UserAPI();
        userLiveData = new MutableLiveData<>();
        messageLiveData = new MutableLiveData<>();
    }

    public void signIn(String email, String password) {
        userAPI.signIn(email, password);
    }

    public void createUser(User user) {
        userAPI.createUser(user, messageLiveData);
    }

    public User getUserByEmail(String email) {
        userAPI.getUserByEmail(email, userLiveData);
        return userLiveData.getValue();
    }

    public void updateUser(int id, User user, String token) {
        userAPI.updateUser(id, user, token, messageLiveData);
    }

    public void deleteUser(int id, String token) {
        userAPI.deleteUser(id, token, messageLiveData);
    }

    public void getAllUsers(String token) {
        userAPI.getAllUsers(token, usersLiveData);
    }
}
