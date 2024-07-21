// UserVideosActivity.java
package com.example.myyoutube.screens;

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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myyoutube.R;
import com.example.myyoutube.adapters.VideoListAdapter;
import com.example.myyoutube.entities.User;
import com.example.myyoutube.entities.Video;
import com.example.myyoutube.viewmodels.UserManager;
import com.example.myyoutube.viewmodels.VideosViewModel;

import java.util.List;

public class UserVideosActivity extends AppCompatActivity {

    private ImageView ivChannelHeaderPic;
    private TextView tvChannelHeaderName;
    private TextView tvChannelHeaderEmail;
    private SearchView searchView;
    private RecyclerView recyclerView;
    private VideoListAdapter adapter;
    private VideosViewModel videosViewModel;

    private UserManager userManager;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_videos);
        videosViewModel = new ViewModelProvider(this).get(VideosViewModel.class);
        userManager = UserManager.getInstance();

        // Return to main activity on button click
        findViewById(R.id.btnBackFromChannel).setOnClickListener(view -> {
            Intent intent = new Intent(UserVideosActivity.this, MainActivity.class);
            startActivity(intent);
        });
        initViews();

        String userEmail = getIntent().getStringExtra("userEmail");
        User user = userManager.getUserByEmail(userEmail);
        if (user != null) {
            setChannelDetails(user);
            getUserVideos(userEmail);
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
        tvChannelHeaderName.setText(user.getFirstName());
        tvChannelHeaderEmail.setText(user.getEmail());
    }

    private void getUserVideos(String email) {
        List<Video> userVideos = videosViewModel.getVideosByUserEmail(email);
        setupRecyclerView(userVideos);
    }

    private void setupRecyclerView(List<Video> userVideos) {
        recyclerView.setFocusable(false);
        adapter = new VideoListAdapter(this, videosViewModel);
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
