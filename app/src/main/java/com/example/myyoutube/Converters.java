package com.example.myyoutube;

import androidx.room.TypeConverter;

import com.example.myyoutube.classes.Comment;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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
}
