package com.example.myyoutube.repositories;


import androidx.lifecycle.MutableLiveData;
import com.example.myyoutube.api.UserAPI;
import com.example.myyoutube.entities.User;


import java.util.List;

public class UserRepository {

    private final UserAPI userAPI;
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
        userAPI.getUserByEmail(email,userLiveData);
        User user = userLiveData.getValue();
        return user;
    }

    public void updateUser(String email, User user, String token) {
        userAPI.updateUser(email, user, token, messageLiveData);
    }

    public void deleteUser(String email, String token) {
        userAPI.deleteUser(email, token, messageLiveData);
    }

    public void getAllUsers(String token) {
        userAPI.getAllUsers(token, usersLiveData);
    }
}
