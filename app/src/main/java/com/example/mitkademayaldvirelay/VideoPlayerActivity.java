package com.example.mitkademayaldvirelay;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mitkademayaldvirelay.classes.Video;
import com.example.mitkademayaldvirelay.classes.VideoManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.List;

public class VideoPlayerActivity extends AppCompatActivity {

    private Video video;
    private CommentsAdapter commentsAdapter;
    private List<String> commentsList;
    private ImageButton likeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        int videoId = getIntent().getIntExtra("videoId", -1);
        video = VideoManager.getVideoManager().getVideoById(videoId);
        if (video == null) {
            finish();
            return;
        }

        String videoPath = "android.resource://" + getPackageName() + "/raw/" + video.getMp4file();
        commentsList = video.getComments();

        if (commentsList == null) {
            commentsList = new ArrayList<>();
        }

        VideoView videoView = findViewById(R.id.videoView);
        videoView.setVideoURI(Uri.parse(videoPath));

        MediaController mediaController = new MediaController(this);
        videoView.setMediaController(mediaController);
        mediaController.setAnchorView(videoView);

        // Start the video
        videoView.start();

        // Set the data to views
        TextView titleView = findViewById(R.id.tvTitle);
        TextView channelNameView = findViewById(R.id.tvChannelName);
        TextView viewsView = findViewById(R.id.tvViews);
        TextView likesView = findViewById(R.id.tvLikes);


        titleView.setText(video.getTitle());
        channelNameView.setText(video.getChannel());
        viewsView.setText("Views: " + video.getViews());
        likesView.setText("Likes: " + video.getLikes());

        ImageButton shareBtn = findViewById(R.id.IBShare);
        likeButton = findViewById(R.id.IBLike);
        updateLikeButton(likeButton);

        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Share button
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Check out this video!");
                shareIntent.putExtra(Intent.EXTRA_TEXT, videoPath);
                startActivity(Intent.createChooser(shareIntent, "Share via"));
            }
        });

        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                video.setLiked(!video.isLiked());
                if (video.isLiked()) {
                    video.incrementLikes();
                } else {
                    video.decrementLikes();
                }
                likesView.setText("Likes: " + video.getLikes());
                updateLikeButton(likeButton);
            }
        });


        // Initialize comments section
        ListView commentsListView = findViewById(R.id.commentsListView);
        commentsAdapter = new CommentsAdapter(this, commentsList);
        commentsListView.setAdapter(commentsAdapter);

        findViewById(R.id.btnAddComment).setOnClickListener(view -> {
            EditText commentInput = findViewById(R.id.etComment);
            String newComment = commentInput.getText().toString();
            if (!newComment.isEmpty()) {
                video.addComment(newComment);
                commentsAdapter.notifyDataSetChanged();
                commentInput.setText("");
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setBackgroundColor(getResources().getColor(R.color.dark_grey));
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.nav_home) {
                    Intent intent = new Intent(VideoPlayerActivity.this, MainActivity.class);
                    startActivity(intent);
                }
                else if (id == R.id.nav_add_video) {
                    // Intent intent = new Intent(this, LoginActivity.class);
                    // startActivity(intent);
                }else if (id == R.id.nav_login) {
                    // Implement login activity
                    // Intent intent = new Intent(this, LoginActivity.class);
                    // startActivity(intent);
                }
                return false;
            }
        });
        // Ensure no item is selected by default
        bottomNavigationView.getMenu().setGroupCheckable(0, false, true);
    }

    @Override
    public void onBackPressed() {
        Intent resultIntent = new Intent();
        setResult(-1, resultIntent);
        super.onBackPressed();
    }

    private void updateLikeButton(ImageButton likeButton) {
        if (video.isLiked()) {
            likeButton.setImageResource(R.drawable.liked);
        } else {
            likeButton.setImageResource(R.drawable.unliked);
        }
    }
    private class CommentsAdapter extends ArrayAdapter<String> {

        private Context context;
        private List<String> comments;

        public CommentsAdapter(Context context, List<String> comments) {
            super(context, 0, comments);
            this.context = context;
            this.comments = comments;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.comment_item, parent, false);
            }

            EditText commentEdit = convertView.findViewById(R.id.etComment);
            ImageButton saveButton = convertView.findViewById(R.id.btnSave);
            ImageButton editButton = convertView.findViewById(R.id.btnEdit);
            ImageButton deleteButton = convertView.findViewById(R.id.btnDelete);

            String comment = getItem(position);
            commentEdit.setText(comment);
            commentEdit.setEnabled(false); // Disable editing

            editButton.setOnClickListener(v -> {
                commentEdit.setEnabled(true); // Enable editing
                saveButton.setVisibility(View.VISIBLE); // Show save button
            });

            saveButton.setOnClickListener(v -> {
                String editedComment = commentEdit.getText().toString();
                if (!editedComment.isEmpty()) {
                    comments.set(position, editedComment);
                    notifyDataSetChanged();
                    commentEdit.setEnabled(false); // Disable editing after saving
                    saveButton.setVisibility(View.GONE); // Hide save button
                }
            });

            deleteButton.setOnClickListener(v -> {
                comments.remove(position);
                notifyDataSetChanged();
            });

            return convertView;
        }
    }
}
