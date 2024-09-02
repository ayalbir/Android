package com.example.myyoutube.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.myyoutube.Converters;


@Entity(tableName = "users")
@TypeConverters({Converters.class})
public class User {

    String id;
    @NonNull
    @PrimaryKey
    private String email;
    private String password;
    private String firstName;
    private String familyName;
    private String birthdate;
    private String gender;
    private String profileImage;

    public User(String email, String password, String firstName, String familyName, String birthdate, String gender, String profileImage) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.familyName = familyName;
        this.birthdate = birthdate;
        this.gender = gender;
        this.profileImage = profileImage;
    }

    // Getters and Setters for all fields

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }
}
