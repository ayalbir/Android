package com.example.mitkademayaldvirelay;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.mitkademayaldvirelay.classes.Video;
import com.example.mitkademayaldvirelay.classes.VideoManager;

import java.util.Objects;


public class AddEditVideoActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PICK_VIDEO_REQUEST = 2;
    private static final int PERMISSION_REQUEST_CODE = 3;

    private EditText etTitle, etDescription, etChannel;
    private ImageView ivThumbnail;
    private Uri imageUri, videoUri;
    private Video video;
    private boolean isEditMode = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_video);

        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        etChannel = findViewById(R.id.etChannel);
        Button btnSave = findViewById(R.id.btnSaveEditVideo);
        Button btnSelectImage = findViewById(R.id.btnSelectImage);
        Button btnSelectVideo = findViewById(R.id.btnChooseVid);
        ivThumbnail = findViewById(R.id.ivThumbnail);


        int videoId = getIntent().getIntExtra("videoId", -1);
        if (videoId != -1) {
            isEditMode = true;
            video = VideoManager.getVideoManager().getVideoById(videoId);
            if (video != null) {
                etTitle.setText(video.getTitle());
                etDescription.setText(video.getDescription());
                etChannel.setText(video.getChannel());
                imageUri = Uri.parse(video.getThumbnail());
                videoUri = Uri.parse(video.getMp4file());
                ivThumbnail.setImageURI(imageUri);
            }
        } else {
            video = new Video();
        }


        btnSelectImage.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(AddEditVideoActivity.this, READ_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(AddEditVideoActivity.this, new String[]{READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
            } else {
                openGallery(PICK_IMAGE_REQUEST);
            }
        });

        btnSelectVideo.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(AddEditVideoActivity.this, READ_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(AddEditVideoActivity.this, new String[]{READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
            } else {
                openGallery(PICK_VIDEO_REQUEST);
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveVideo();
            }
        });

        btnSave.setOnClickListener(v -> saveVideo());
    }

    private void openGallery(int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (requestCode == PICK_VIDEO_REQUEST) {
            intent.setType("video/*");
        }
        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null && data.getData() != null) {
            if (requestCode == PICK_IMAGE_REQUEST) {
                imageUri = data.getData();
                ivThumbnail.setImageURI(imageUri);
            } else if (requestCode == PICK_VIDEO_REQUEST) {
                videoUri = data.getData();
                Toast.makeText(this, "Video selected: " + videoUri.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PERMISSION_GRANTED) {
                openGallery(requestCode == PICK_IMAGE_REQUEST ? PICK_IMAGE_REQUEST : PICK_VIDEO_REQUEST);
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void saveVideo() {
        video.setTitle(Objects.requireNonNull(etTitle.getText()).toString());
        video.setDescription(Objects.requireNonNull(etDescription.getText()).toString());
        video.setChannel(Objects.requireNonNull(etChannel.getText()).toString());
        video.setThumbnail(imageUri != null ? imageUri.toString() : "");
        video.setMp4file(videoUri != null ? videoUri.toString() : "");

        if (isEditMode) {
            VideoManager.getVideoManager().updateVideo(video);
        } else {
            VideoManager.getVideoManager().addVideo(video);
        }

        setResult(RESULT_OK, new Intent());
        finish();
    }
}