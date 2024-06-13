package com.example.myyoutube;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myyoutube.classes.Comment;
import com.example.myyoutube.classes.User;
import com.example.myyoutube.classes.UserManager;
import com.example.myyoutube.classes.Video;
import com.example.myyoutube.classes.VideoManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class VideoPlayerActivity extends AppCompatActivity {

    private Video video;
    private CommentsAdapter commentsAdapter;
    private List<Comment> commentsList;
    private ImageButton likeButton;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        MenuItem profilePictureItem = bottomNavigationView.getMenu().findItem(R.id.nav_login);
        profilePictureItem.setIcon(R.drawable.login);
        profilePictureItem.setTitle("Login");

        currentUser = MainActivity.getCurrentUser();
        if(currentUser != null) {
            Bitmap bitmap = decodeImage(currentUser.getProfileImage());
            NavigationView navigationView = findViewById(R.id.nav_view);
            profilePictureItem.setIcon(R.drawable.nav_logout);
            profilePictureItem.setTitle("Logout");
        }

        int videoId = getIntent().getIntExtra("videoId", -1);
        video = VideoManager.getVideoManager().getVideoById(videoId);
        if (video == null) {
            finish();
            return;
        }

        Uri videoUri;
        VideoView videoView = findViewById(R.id.videoView);
        if (isFileInRaw(video.getMp4file())) {
            String videoPath = "android.resource://" + getPackageName() + "/raw/" + video.getMp4file();
            videoUri = Uri.parse(videoPath);
        } else {
            try {
                videoUri = decodeBase64ToVideoFile(video.getMp4file());
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to load video", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        videoView.setVideoURI(videoUri);
        commentsList = video.getComments();

        if (commentsList == null) {
            commentsList = new ArrayList<>();
        }

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
        updateLikeButton();

        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Sharing
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Check out this video!");
                shareIntent.putExtra(Intent.EXTRA_TEXT, videoUri.toString());
                startActivity(Intent.createChooser(shareIntent, "Share via"));
            }
        });

        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentUser != null) {
                    if (currentUser.hasLikedVideo(video.getId())) {
                        video.decrementLikes();
                        currentUser.removeLikedVideo(video.getId());
                    } else {
                        video.incrementLikes();
                        currentUser.addLikedVideo(video.getId());
                    }
                    likesView.setText("Likes: " + video.getLikes());
                    updateLikeButton();
                } else {
                    Toast.makeText(VideoPlayerActivity.this, "User not connected", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Initialize comments section
        ListView commentsListView = findViewById(R.id.commentsListView);
        commentsAdapter = new CommentsAdapter(this, commentsList);
        commentsListView.setAdapter(commentsAdapter);

        findViewById(R.id.btnAddComment).setOnClickListener(view -> {
            if(MainActivity.getCurrentUser() != null) {
                EditText commentInput = findViewById(R.id.etComment);
                String newComment = commentInput.getText().toString();
                if (!newComment.isEmpty()) {
                    String profileImageBase64 = MainActivity.getCurrentUser().getProfileImage();
                    byte[] decodedString = Base64.decode(profileImageBase64, Base64.DEFAULT);
                    Bitmap userProfileBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    Uri userProfileUri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), userProfileBitmap, "Title", null));
                    Comment comment = new Comment(newComment, userProfileUri.toString());
                    video.addComment(comment);
                    commentsAdapter.notifyDataSetChanged();
                    commentInput.setText("");
                }
            }
            else {
                Toast.makeText(VideoPlayerActivity.this, "User not connected", Toast.LENGTH_SHORT).show();
            }

        });

        bottomNavigationView.setBackgroundColor(getResources().getColor(R.color.custom_red));
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.nav_home) {
                    Intent homeIntent = new Intent(VideoPlayerActivity.this, MainActivity.class);
                    startActivity(homeIntent);
                    return true;
                }
                else if (id == R.id.nav_add_video) {
                    if(MainActivity.getCurrentUser() != null) {
                        Intent intent = new Intent(VideoPlayerActivity.this, AddEditVideoActivity.class);
                        startActivity(intent);
                    }
                    else {
                        Intent intent = new Intent(VideoPlayerActivity.this, logInScreen1.class);
                        startActivity(intent);
                    }
                    return true;
                }else if (id == R.id.nav_login) {
                     Intent intent = new Intent(VideoPlayerActivity.this, logInScreen1.class);
                     startActivity(intent);
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
        resultIntent.putExtra("updatedVideoId", video.getId());
        setResult(RESULT_OK, resultIntent);
        super.onBackPressed();
    }

    private boolean isFileInRaw(String fileName) {
        Resources res = getResources();
        int resourceId = res.getIdentifier(fileName, "raw", getPackageName());
        return resourceId != 0;
    }
    private void updateLikeButton() {
        if (currentUser != null && currentUser.hasLikedVideo(video.getId())) {
            likeButton.setImageResource(R.drawable.liked);
        } else {
            likeButton.setImageResource(R.drawable.unliked);
        }
    }
    private Uri decodeBase64ToVideoFile(String base64Str) throws IOException {
        byte[] decodedBytes = Base64.decode(base64Str, Base64.DEFAULT);
        File tempFile = File.createTempFile("tempVideo", ".mp4", getCacheDir());
        FileOutputStream fos = new FileOutputStream(tempFile);
        fos.write(decodedBytes);
        fos.close();
        return Uri.fromFile(tempFile);
    }

    private Bitmap decodeImage(String encodedImage) {
        if (encodedImage != null) {
            byte[] decodedBytes = Base64.decode(encodedImage, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        }
        return null;
    }
    private class CommentsAdapter extends ArrayAdapter<Comment> {

        private Context context;
        private List<Comment> comments;

        public CommentsAdapter(Context context, List<Comment> comments) {
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
            ImageButton saveCommentButton = convertView.findViewById(R.id.btnSaveComment);
            ImageButton editButton = convertView.findViewById(R.id.btnEdit);
            ImageButton deleteButton = convertView.findViewById(R.id.btnDelete);

            Comment comment = getItem(position);
            assert comment != null;
            commentEdit.setText(comment.getCommentContent());
            commentEdit.setEnabled(false); // Disable editing

            editButton.setOnClickListener(v -> {
                if(MainActivity.getCurrentUser() != null) {
                    commentEdit.setEnabled(true); // Enable editing
                    saveCommentButton.setVisibility(View.VISIBLE); // Show save button
                }
                else {
                    Toast.makeText(VideoPlayerActivity.this, "User not connected", Toast.LENGTH_SHORT).show();
                }
            });

            saveCommentButton.setOnClickListener(v -> {
                String editedComment = commentEdit.getText().toString();
                if (!editedComment.isEmpty()) {
                    comments.set(position, new Comment(editedComment, comment.getCommentPic()));
                    notifyDataSetChanged();
                    commentEdit.setEnabled(false); // Disable editing after saving
                    saveCommentButton.setVisibility(View.GONE); // Hide save button
                }
            });

            deleteButton.setOnClickListener(v -> {
                if(MainActivity.getCurrentUser() != null){
                    comments.remove(position);
                    notifyDataSetChanged();
                }
            });

            return convertView;
        }
    }
}
