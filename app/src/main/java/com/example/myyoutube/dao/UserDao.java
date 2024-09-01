package com.example.myyoutube.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.myyoutube.entities.User;

import java.util.List;

@Dao
public interface UserDao {
    @Insert
    void insert(User user);

    @Update
    void update(User user);

    @Delete
    void delete(User user);

    @Query("SELECT * FROM users WHERE email = :email")
    User getUserByEmail(String email);
    @Query("DELETE FROM users WHERE email = :email")
    void deleteByEmail(String email);

    @Query("SELECT * FROM users")
    List<User> getAllUsers();

    @Query("DELETE FROM users")
    void clear();
}
