package com.example.myyoutube.repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Room;

import com.example.myyoutube.AppDB;
import com.example.myyoutube.Helper;
import com.example.myyoutube.api.UserAPI;
import com.example.myyoutube.dao.UserDao;
import com.example.myyoutube.entities.User;
import com.example.myyoutube.viewmodels.VideosViewModel;

import java.util.List;

public class UserRepository {

    private final UserAPI userAPI;
    private final MutableLiveData<String> messageLiveData;
    private final MutableLiveData<List<User>> usersListData;
    private final UserDao userDao;

    private VideosViewModel videosViewModel;

    public UserRepository() {
        AppDB db = Room.databaseBuilder(Helper.context, AppDB.class, "Users")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();
        userDao = db.userDao();
        usersListData = new MutableLiveData<>();
        userAPI = new UserAPI(userDao);
        messageLiveData = new MutableLiveData<>();
        videosViewModel = VideosViewModel.getInstance();
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
        videosViewModel.clearVideoDao();
        userAPI.deleteUser(email, token, messageLiveData);
        messageLiveData.observeForever(message -> {
            if ("User deleted successfully".equals(message)) {
                videosViewModel.get();
            }
        });
    }

}
