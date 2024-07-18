package com.example.myyoutube;

import android.content.Context;
import android.os.Environment;
import android.util.Base64;

import androidx.room.TypeConverter;

import com.example.myyoutube.entities.Comment;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.List;

public class Converters {
    @TypeConverter
    public static List<Comment> fromStringToComments(String value) {
        Type listType = new TypeToken<List<Comment>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromCommentsToString(List<Comment> list) {
        Gson gson = new Gson();
        return gson.toJson(list);
    }

    @TypeConverter
    public static List<String> fromStringToStringList(String value) {
        Type listType = new TypeToken<List<String>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromStringListToString(List<String> list) {
        Gson gson = new Gson();
        return gson.toJson(list);
    }

    // File handling methods
    public static int deleteFileFromStorage(String filePath) {
        if (filePath != null) {
            File file = new File(filePath);
            if (file.exists() && file.delete()) {
                return 1;
            }
        }
        return 0;
    }

    public static String base64ToFilePath(String base64Data, String fileType) {
        Context context = Helper.context;
        byte[] bytesData = Base64.decode(base64Data, Base64.DEFAULT);
        if (bytesData == null) {
            return null;
        }

        String fileName = fileType + "_" + System.currentTimeMillis() + (fileType.equals("image") ? ".jpg" : ".mp4");
        return saveBytesToInternalStorage(context, bytesData, fileName, fileType);
    }

    private static String saveBytesToInternalStorage(Context context, byte[] bytes, String fileName, String fileType) {
        if (bytes == null) {
            return null;
        }

        File directory = context.getExternalFilesDir(fileType.equals("image") ? Environment.DIRECTORY_PICTURES : Environment.DIRECTORY_MOVIES);
        File file = new File(directory, fileName);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(bytes);
            fos.flush();
            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void deleteAllFiles() {
        deleteAllFilesInDirectory(Helper.context.getExternalFilesDir(Environment.DIRECTORY_PICTURES));
        deleteAllFilesInDirectory(Helper.context.getExternalFilesDir(Environment.DIRECTORY_MOVIES));
    }

    @TypeConverter
    public static LocalDate fromString(String value) {
        return value == null ? null : LocalDate.parse(value);
    }

    @TypeConverter
    public static String fromDate(LocalDate date) {
        return date == null ? null : date.toString();
    }
    private static void deleteAllFilesInDirectory(File directory) {
        if (directory != null && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    file.delete();
                }
            }
        }
    }
}