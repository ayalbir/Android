package com.example.myyoutube.screens;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myyoutube.Helper;
import com.example.myyoutube.R;
import com.example.myyoutube.adapters.VideoListAdapter;
import com.example.myyoutube.entities.Comment;
import com.example.myyoutube.entities.User;
import com.example.myyoutube.entities.Video;
import com.example.myyoutube.login.logInScreen1;
import com.example.myyoutube.viewmodels.CommentViewModel;
import com.example.myyoutube.viewmodels.UserViewModel;
import com.example.myyoutube.viewmodels.VideosViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class VideoPlayerActivity extends AppCompatActivity {

    private final UserViewModel userViewModel = UserViewModel.getInstance();
    private final List<Comment> commentsList = new ArrayList<>();
    private Video video;
    private CommentsAdapter commentsAdapter;
    private VideoListAdapter videoListAdapter;
    private List<Video> otherVideos;
    private ImageButton likeButton, dislikeButton;
    private RecyclerView rvOtherVideos;
    private User currentUser;
    private VideosViewModel videosViewModel;
    private CommentViewModel commentViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        videosViewModel = new ViewModelProvider(this).get(VideosViewModel.class);
        commentViewModel = new ViewModelProvider(this).get(CommentViewModel.class);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        MenuItem profilePictureItem = bottomNavigationView.getMenu().findItem(R.id.nav_login);
        profilePictureItem.setIcon(R.drawable.login);
        profilePictureItem.setTitle("Login");

        currentUser = Helper.getConnectedUser();
        if (currentUser != null) {
            profilePictureItem.setIcon(R.drawable.settings);
            profilePictureItem.setTitle("Account");
        }

        String videoId = getIntent().getStringExtra("videoId");
        if (videoId == null) {
            finish();
            return;
        }

        commentViewModel.init(videoId);

        this.video = videosViewModel.getVideoById(videoId);
        if (video == null) {
            finish();
            return;
        }

        Uri videoUri;
        VideoView videoView = findViewById(R.id.videoView);
        if (isFileInRaw(video.getUrl())) {
            String videoPath = "android.resource://" + getPackageName() + "/raw/" + video.getUrl();
            videoUri = Uri.parse(videoPath);
        } else {
            try {
                videoUri = decodeBase64ToVideoFile(video.getUrl());
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to load video", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        videoView.setVideoURI(videoUri);

        MediaController mediaController = new MediaController(this);
        videoView.setMediaController(mediaController);
        mediaController.setAnchorView(videoView);
        videoView.start();

        TextView titleView = findViewById(R.id.tvTitle);
        TextView channelNameView = findViewById(R.id.tvChannelName);
        TextView viewsView = findViewById(R.id.tvViews);
        TextView likesView = findViewById(R.id.tvLikes);
        TextView timeAgoView = findViewById(R.id.tvTimeAgo);
        ImageView IVChannelPic = findViewById(R.id.IVChannelPic);
        Bitmap channelPicBitmap = decodeImage(userViewModel.getUserByEmail(video.getEmail()).getProfileImage());
        IVChannelPic.setImageBitmap(channelPicBitmap);

        timeAgoView.setText(video.getTimeAgo());

        titleView.setText(video.getTitle());
        channelNameView.setText(Objects.requireNonNull(userViewModel.getUserByEmail(video.getEmail())).getFirstName());
        viewsView.setText("Views: " + video.getViews());
        likesView.setText("" + video.getLikes());

        ImageButton shareBtn = findViewById(R.id.IBShare);
        likeButton = findViewById(R.id.IBLike);
        dislikeButton = findViewById(R.id.IBDisLike);
        View channelLayout = findViewById(R.id.ChannelLayoutVideoPlayerScreen);
        channelLayout.setOnClickListener(v -> {
            Intent intent = new Intent(this, UserVideosActivity.class);
            intent.putExtra("userEmail", video.getEmail());
            startActivity(intent);
        });

        updateLikeDislikeButtons();

        shareBtn.setOnClickListener(v -> {
            // Sharing
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Check out this video!");
            shareIntent.putExtra(Intent.EXTRA_TEXT, videoUri.toString());
            startActivity(Intent.createChooser(shareIntent, "Share via"));
        });

        likeButton.setOnClickListener(v -> {
            if (currentUser != null) {
                videosViewModel.likeVideo(video);
                if (video.getLikedBy().contains(currentUser.getEmail())) {
                    video.decrementLikes();
                    video.getLikedBy().remove(currentUser.getEmail());
                } else {
                    video.incrementLikes();
                    video.decrementDislikes();
                    video.getLikedBy().add(currentUser.getEmail());
                    video.getDislikedBy().remove(currentUser.getEmail());
                }
                likesView.setText("" + video.getLikes());
                updateLikeDislikeButtons();
            } else {
                Toast.makeText(VideoPlayerActivity.this, "User not connected", Toast.LENGTH_SHORT).show();
            }
        });

        dislikeButton.setOnClickListener(v -> {
            if (currentUser != null) {
                videosViewModel.disLikeVideo(video);
                if (video.getDislikedBy().contains(currentUser.getEmail())) {
                    video.decrementDislikes();
                    video.getDislikedBy().remove(currentUser.getEmail());
                } else {
                    video.incrementDislikes();
                    video.decrementLikes();
                    video.getDislikedBy().add(currentUser.getEmail());
                    video.getLikedBy().remove(currentUser.getEmail());
                }
                likesView.setText("" + video.getLikes());
                updateLikeDislikeButtons();
            } else {
                Toast.makeText(VideoPlayerActivity.this, "User not connected", Toast.LENGTH_SHORT).show();
            }
        });

        ListView commentsListView = findViewById(R.id.commentsListView);
        commentsAdapter = new CommentsAdapter(this, commentsList);
        commentsListView.setAdapter(commentsAdapter);

        commentViewModel.get().observe(this, new Observer<List<Comment>>() {
            @Override
            public void onChanged(List<Comment> comments) {
                commentsList.clear();
                commentsList.addAll(comments);
                commentsAdapter.notifyDataSetChanged();
            }
        });
        commentViewModel.getCommentsForVideo();

        findViewById(R.id.btnAddComment).setOnClickListener(view -> {
            if (currentUser != null) {
                EditText commentInput = findViewById(R.id.etComment);
                String newComment = commentInput.getText().toString();
                if (!newComment.isEmpty()) {
                    String profileImageBase64 = currentUser.getProfileImage();
                    Comment comment = new Comment(videoId, "", newComment, profileImageBase64, currentUser.getEmail());
                    commentViewModel.addComment(comment);
                    commentInput.setText("");
                }
            } else {
                Toast.makeText(VideoPlayerActivity.this, "User not connected", Toast.LENGTH_SHORT).show();
            }
        });

        rvOtherVideos = findViewById(R.id.rvOtherVideos);
        rvOtherVideos.setLayoutManager(new LinearLayoutManager(this));

        // Fetch and display recommended videos
        fetchRecommendedVideos();

        bottomNavigationView.setBackgroundColor(getResources().getColor(R.color.custom_red));
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                Intent homeIntent = new Intent(VideoPlayerActivity.this, MainActivity.class);
                startActivity(homeIntent);
                return true;
            } else if (id == R.id.nav_add_video) {
                Intent intent;
                if (currentUser != null) {
                    intent = new Intent(VideoPlayerActivity.this, AddEditVideoActivity.class);
                } else {
                    intent = new Intent(VideoPlayerActivity.this, logInScreen1.class);
                }
                startActivity(intent);
                return true;
            } else if (id == R.id.nav_login) {
                Intent intent;
                if (currentUser != null) {
                    intent = new Intent(VideoPlayerActivity.this, UpdateDeleteUserActivity.class);
                    intent.putExtra("userEmail", currentUser.getEmail());
                } else {
                    intent = new Intent(VideoPlayerActivity.this, logInScreen1.class);
                }
                startActivity(intent);
            }
            return false;
        });

        bottomNavigationView.getMenu().setGroupCheckable(0, false, true);
    }

    private void fetchRecommendedVideos() {
        videosViewModel.getSuggestedVideos().observe(this, videos -> {
            if (videos != null && video != null) {
                otherVideos = new ArrayList<>(videos);
                otherVideos.remove(video);  // Remove the current video from the list of recommended videos
                videoListAdapter = new VideoListAdapter(this, videosViewModel);
                videoListAdapter.setVideos(otherVideos);
                rvOtherVideos.setAdapter(videoListAdapter);
            }
        });
    }


    private Bitmap decodeImage(String encodedImage) {
        if (encodedImage != null) {
            byte[] decodedBytes = Base64.decode(encodedImage, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        }
        return null;
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

    private void updateLikeDislikeButtons() {
        if (currentUser != null) {
            if (video.getLikedBy().contains(currentUser.getEmail())) {
                likeButton.setImageResource(R.drawable.liked);
            } else {
                likeButton.setImageResource(R.drawable.unliked);
            }
            if (video.getDislikedBy().contains(currentUser.getEmail())) {
                dislikeButton.setImageResource(R.drawable.disliked);
            } else {
                dislikeButton.setImageResource(R.drawable.undisliked);
            }
        } else {
            likeButton.setImageResource(R.drawable.unliked);
            dislikeButton.setImageResource(R.drawable.undisliked);
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

    private class CommentsAdapter extends ArrayAdapter<Comment> {
        private final Context context;
        private final List<Comment> comments;

        public CommentsAdapter(Context context, List<Comment> comments) {
            super(context, 0, comments);
            this.context = context;
            this.comments = comments;
        }

        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.comment_item, parent, false);
            }
            EditText commentEdit = convertView.findViewById(R.id.etComment);
            ImageButton btnMenu = convertView.findViewById(R.id.btnMenu);
            ImageView ivProfilePic = convertView.findViewById(R.id.ivProfilePic);

            Comment comment = getItem(position);
            assert comment != null;
            commentEdit.setText(comment.getText());
            commentEdit.setEnabled(false);

            // Decode and set the profile image
            String profileImageBase64 = comment.getProfilePicture();
            if (profileImageBase64 != null && !profileImageBase64.isEmpty()) {
                byte[] decodedString = Base64.decode(profileImageBase64, Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                ivProfilePic.setImageBitmap(decodedByte);
            } else {
                ivProfilePic.setImageResource(R.drawable.login);
            }

            btnMenu.setOnClickListener(v -> {
                if (currentUser != null) {
                    if (currentUser.getEmail().equalsIgnoreCase(comment.getEmail())) {
                        showEditDeleteDialog(comment);
                    } else {
                        Toast.makeText(getContext(), "You can only edit or delete your own comments", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "User not connected", Toast.LENGTH_SHORT).show();
                }
            });
            return convertView;
        }

        private void showEditDeleteDialog(Comment comment) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Choose an option")
                    .setItems(new CharSequence[]{"Edit", "Delete"}, (dialog, which) -> {
                        if (which == 0) {
                            showEditCommentDialog(comment);
                        } else if (which == 1) {
                            commentViewModel.deleteComment(comment);
                        }
                    })
                    .show();
        }

        private void showEditCommentDialog(Comment comment) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Edit Comment");

            View viewInflated = LayoutInflater.from(context).inflate(R.layout.dialog_edit_comment,
                    ((Activity) context).findViewById(android.R.id.content), false);
            EditText input = viewInflated.findViewById(R.id.input);
            input.setText(comment.getText());

            builder.setView(viewInflated);

            builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
                dialog.dismiss();
                String editedComment = input.getText().toString();
                if (!editedComment.isEmpty()) {
                    comment.setText(editedComment);
                    commentViewModel.updateComment(comment);
                    notifyDataSetChanged();
                }
            });
            builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.cancel());

            builder.show();
        }
    }
}
