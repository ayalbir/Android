package com.example.myyoutube.repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Room;

import com.example.myyoutube.AppDB;
import com.example.myyoutube.Helper;
import com.example.myyoutube.api.UserAPI;
import com.example.myyoutube.dao.UserDao;
import com.example.myyoutube.entities.User;
import com.example.myyoutube.entities.Video;
import com.example.myyoutube.viewmodels.UserManager;

import java.util.List;

public class UserRepository {

    private final UserAPI userAPI;
    private UserDao userDao;
    private MutableLiveData<List<User>> usersLiveData;
    private MutableLiveData<User> userLiveData;
    private MutableLiveData<String> messageLiveData;
    private AppDB db;

    public UserRepository() {
        new Thread(() -> {
            db = Room.databaseBuilder(Helper.context, AppDB.class, "FootubeDB")
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build();
            userDao = db.userDao();
        }).start();

        usersLiveData = new MutableLiveData<>();
        userAPI = new UserAPI(userDao);
        userLiveData = new MutableLiveData<>();
        messageLiveData = new MutableLiveData<>();
    }

    public void signIn(String email, String password) {
        userAPI.signIn(email, password);
    }

    public void createUser(User user) {
        userAPI.createUser(user, messageLiveData);
        userDao.insert(UserManager.getConnectedUser());
    }

    public void getAllUsers() {
        userAPI.getAllUsers();
    }
    public User getUserByEmail(String email) {
        User user = userDao.getUserByEmail(email);
        return user;
    }

    public void updateUser(String email, User user, String token) {
        userAPI.updateUser(email, user, token, messageLiveData);
        userDao.update(UserManager.getConnectedUser());
    }

    public void deleteUser(String email, String token) {
        userAPI.deleteUser(email, token, messageLiveData);
        userDao.delete(UserManager.getConnectedUser());
    }

}
