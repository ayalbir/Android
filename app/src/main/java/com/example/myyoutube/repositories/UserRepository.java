package com.example.myyoutube.repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Room;

import com.example.myyoutube.AppDB;
import com.example.myyoutube.Helper;
import com.example.myyoutube.api.UserAPI;
import com.example.myyoutube.dao.UserDao;
import com.example.myyoutube.entities.User;

import java.util.List;

public class UserRepository {

    private final UserAPI userAPI;
    private final MutableLiveData<String> messageLiveData;
    private UserDao userDao;
    private final MutableLiveData<List<User>> usersListData;
    private AppDB db;

    public UserRepository() {
        new Thread(() -> {
            db = Room.databaseBuilder(Helper.context, AppDB.class, "Users")
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build();
            userDao = db.userDao();
        }).start();
        usersListData = new MutableLiveData<>();
        userAPI = new UserAPI(userDao);
        messageLiveData = new MutableLiveData<>();
    }

    public void signIn(String email, String password) {
        userAPI.signIn(email, password);
    }

    public void createUser(User user) {
        userAPI.createUser(user, messageLiveData);
        userDao.insert(Helper.getConnectedUser());
    }

    public LiveData<List<User>> get() {
        userAPI.getAllUsers(usersListData);
        return usersListData;
    }

    public void getUsersFromDao() {
        userDao.getAllUsers();
    }

    public User getUserByEmail(String email) {
        return userDao.getUserByEmail(email);
    }

    public void updateUser(String email, User user, String token) {
        userAPI.updateUser(email, user, token, messageLiveData);
        userDao.update(user);
    }

    public void deleteUser(String email, String token) {
        userAPI.deleteUser(email, token, messageLiveData);
        userDao.delete(Helper.getConnectedUser());
    }


}
