package com.example.mitkadenayaldvirelay;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mitkadenayaldvirelay.adapters.VideoListAdapter;
import com.example.mitkadenayaldvirelay.classes.Video;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView videoList = findViewById(R.id.videoList);
        final VideoListAdapter adapter = new VideoListAdapter(this);
        videoList.setAdapter(adapter);
        videoList.setLayoutManager(new LinearLayoutManager(this));


        List<Video> videos = new ArrayList<>();
        videos.add(new Video("One", "Football", "Hapoel win", 4, R.drawable.login));
        videos.add(new Video("Two", "Tennis", "Hapoel draw", 9, R.drawable.login));
        videos.add(new Video("Three", "Basket", "Hapoel lose", 7, R.drawable.login));
        adapter.setVideos(videos);

    }
}