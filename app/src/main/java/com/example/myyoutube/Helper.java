package com.example.myyoutube;
import android.app.Application;
import android.content.Context;

public class Helper extends Application {
    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }
}
