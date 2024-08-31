package com.example.myyoutube;

import android.app.Application;
import android.content.Context;

import com.example.myyoutube.entities.User;

public class Helper extends Application {
    public static Context context;
    public static User connectedUser;

    public static User getConnectedUser() {
        return connectedUser;
    }

    public static void setConnectedUser(User currentUser) {
        Helper.connectedUser = currentUser;
    }

    public static void clearConnectedUser() {
        connectedUser = null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }
}
