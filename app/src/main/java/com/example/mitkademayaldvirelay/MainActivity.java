package com.example.mitkademayaldvirelay;

import static com.example.mitkademayaldvirelay.R.layout.activity_main;

import android.content.res.AssetManager;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mitkademayaldvirelay.adapters.VideoListAdapter;
import com.example.mitkademayaldvirelay.classes.Video;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_main);

        RecyclerView videoList = findViewById(R.id.videoList);
        final VideoListAdapter adapter = new VideoListAdapter(this);
        videoList.setAdapter(adapter);
        videoList.setLayoutManager(new LinearLayoutManager(this));

        List<Video> videos = new ArrayList<>();

        try {
            JSONObject obj = new JSONObject(loadJSONFromAsset());
            JSONArray jsonArray = obj.getJSONArray("Videos");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                videos.add(new Video(jsonObject.getString("channel"),
                        jsonObject.getString("title"),
                        jsonObject.getString("description"),
                        jsonObject.getInt("duration"), R.drawable.login));
            }
            adapter.setVideos(videos);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public String loadJSONFromAsset() {
        String json = null;
        try {
            //TODO: check how to write this line below
            InputStream is = getResources().getAssets().open("data.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}