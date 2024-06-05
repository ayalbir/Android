package com.example.mitkademayaldvirelay;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mitkademayaldvirelay.classes.Video;

import java.util.ArrayList;
import java.util.List;

public class VideoPlayerActivity extends AppCompatActivity {

    private Video video;
    private CommentsAdapter commentsAdapter;
    private List<String> commentsList;
    private static boolean isLiked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        video = (Video) getIntent().getSerializableExtra("video");
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
        TextView channelNameView = findViewById(R.id.tvChannelName);
        TextView viewsView = findViewById(R.id.tvViews);
        TextView likesView = findViewById(R.id.tvLikes);

        channelNameView.setText(video.getChannel());
        viewsView.setText("Views: " + video.getViews());
        likesView.setText("Likes: " + video.getLikes());

        ImageButton shareBtn = findViewById(R.id.IBShare);
        ImageButton likeButton = findViewById(R.id.IBLike);

        // Initialize the like button image
        if (isLiked) {
            likeButton.setImageResource(R.drawable.liked);
        } else {
            likeButton.setImageResource(R.drawable.unliked);
        }

        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Sharing functionality
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
                if (isLiked) {
                    video.decrementLikes();
                    likeButton.setImageResource(R.drawable.unliked);
                } else {
                    video.incrementLikes();
                    likeButton.setImageResource(R.drawable.liked);
                }
                isLiked = !isLiked; // Toggle the like state
                likesView.setText("Likes: " + video.getLikes());
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
    }

    @Override
    public void onBackPressed() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("updatedVideo", video);
        setResult(RESULT_OK, resultIntent);
        super.onBackPressed();
    }

    private class CommentsAdapter extends ArrayAdapter<String> {

        private Context context;
        private List<String> comments;

        public CommentsAdapter(Context context, List<String> comments) {
            super(context, 0, comments);
            this.context = context;
            this.comments = comments;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.comment_item, parent, false);
            }

            TextView commentView = convertView.findViewById(R.id.tvComment);
            ImageButton editButton = convertView.findViewById(R.id.btnEdit);
            ImageButton deleteButton = convertView.findViewById(R.id.btnDelete);

            String comment = getItem(position);
            commentView.setText(comment);

            editButton.setOnClickListener(v -> showEditCommentDialog(position));
            deleteButton.setOnClickListener(v -> {
                comments.remove(position);
                notifyDataSetChanged();
            });

            return convertView;
        }

        private void showEditCommentDialog(int position) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Edit Comment");

            final EditText input = new EditText(context);
            input.setText(comments.get(position));
            builder.setView(input);

            builder.setPositiveButton("OK", (dialog, which) -> {
                String editedComment = input.getText().toString();
                if (!editedComment.isEmpty()) {
                    comments.set(position, editedComment);
                    notifyDataSetChanged();
                }
            });
            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

            builder.show();
        }
    }
}
