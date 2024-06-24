// UserVideosActivity.java
package com.example.myyoutube;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myyoutube.adapters.VideoListAdapter;
import com.example.myyoutube.classes.User;
import com.example.myyoutube.managers.UserManager;
import com.example.myyoutube.classes.Video;
import com.example.myyoutube.managers.VideoManager;

import java.util.ArrayList;
import java.util.List;

public class UserVideosActivity extends AppCompatActivity {

    private ImageView ivChannelHeaderPic;
    private TextView tvChannelHeaderName;
    private TextView tvChannelHeaderEmail;
    private SearchView searchView;
    private RecyclerView recyclerView;
    private VideoListAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_videos);

        // Return to main activity on button click
        findViewById(R.id.imageButton).setOnClickListener(view -> {
            Intent intent = new Intent(UserVideosActivity.this, MainActivity.class);
            startActivity(intent);
        });

        initViews();

        String userEmail = getIntent().getStringExtra("userEmail");
        User user = UserManager.getUserByEmail(userEmail);
        if (user != null) {
            setChannelDetails(user);
            List<Video> userVideos = getUserVideos(userEmail);
            setupRecyclerView(userVideos);
            setupSearchView();
        }

    }

    private void initViews() {
        ivChannelHeaderPic = findViewById(R.id.ivChannelHeaderPic);
        tvChannelHeaderName = findViewById(R.id.tvChannelHeaderName);
        tvChannelHeaderEmail = findViewById(R.id.tvChannelHeaderEmail);
        searchView = findViewById(R.id.searchView);
        recyclerView = findViewById(R.id.recyclerView);
    }

    private void setChannelDetails(User user) {
        byte[] decodedString = Base64.decode(user.getProfileImage(), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        ivChannelHeaderPic.setImageBitmap(decodedByte);
        tvChannelHeaderName.setText(user.getUserName());
        tvChannelHeaderEmail.setText(user.getEmail());
    }

    private List<Video> getUserVideos(String email) {
        List<Video> allVideos = VideoManager.getVideoManager().getVideos();
        List<Video> userVideos = new ArrayList<>();
        for (Video video : allVideos) {
            if (video.getChannelEmail().equals(email)) {
                userVideos.add(video);
            }
        }
        return userVideos;
    }

    private void setupRecyclerView(List<Video> userVideos) {
        recyclerView.setFocusable(false);
        adapter = new VideoListAdapter(this);
        adapter.setVideos(userVideos);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filter(newText);
                return false;
            }
        });
    }
}
