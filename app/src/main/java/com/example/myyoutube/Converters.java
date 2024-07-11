package com.example.myyoutube;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import androidx.room.TypeConverter;

import com.example.myyoutube.classes.Comment;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class Converters {
    @TypeConverter
    public static List<Comment> fromString(String value) {
        Type listType = new TypeToken<List<Comment>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromList(List<Comment> list) {
        Gson gson = new Gson();
        return gson.toJson(list);
    }

    @TypeConverter
    public static List<String> fromStringList(String value) {
        Type listType = new TypeToken<List<String>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromStringList(List<String> list) {
        Gson gson = new Gson();
        return gson.toJson(list);
    }
    @TypeConverter
    public static void deleteImageFromStorage(String filePath) {
        if (filePath != null) {
            File file = new File(filePath);
            if (file.exists()) {
                file.delete();
            }
        }
    }
    @TypeConverter
    public static String base64ToString(String base64String) {
        if (base64String == null || base64String.isEmpty()) {
            return null;
        }
        byte[] decodedBytes = Base64.decode(base64String, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        return saveBitmapToFile(bitmap);
    }
    @TypeConverter
    private static String saveBitmapToFile(Bitmap bitmap) {
        Context context = Helper.context; // Replace with your application's context
        File filesDir = context.getFilesDir();
        File imageFile = new File(filesDir, System.currentTimeMillis() + ".png");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
            return imageFile.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
