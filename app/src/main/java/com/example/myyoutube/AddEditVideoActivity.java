package com.example.myyoutube;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.myyoutube.classes.Video;
import com.example.myyoutube.classes.VideoManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class AddEditVideoActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PICK_VIDEO_REQUEST = 2;
    private static final int REQUEST_CAMERA = 3;
    private static final int PERMISSION_REQUEST_CODE = 4;

    private EditText etTitle, etDescription, etChannel;
    private ImageView ivThumbnail;
    private Uri imageUri, videoUri;
    private Video video;
    private boolean isEditMode = false;
    private boolean isVideoSelected = false
            , isImageSelected = false;

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

        checkAndRequestPermissions();

        int videoId = getIntent().getIntExtra("videoId", -1);
        if (videoId != -1) {
            isEditMode = true;
            video = VideoManager.getVideoManager().getVideoById(videoId);
            if (video != null) {
                etTitle.setText(video.getTitle());
                etDescription.setText(video.getDescription());
                etChannel.setText(video.getChannelEmail());
                imageUri = Uri.parse(video.getThumbnail());
                videoUri = Uri.parse(video.getMp4file());
                ivThumbnail.setImageURI(imageUri);
                ivThumbnail.setVisibility(View.VISIBLE);
            }
        } else {
            video = new Video();
        }

        btnSelectImage.setOnClickListener(v -> openImageGallery());

        btnSelectVideo.setOnClickListener(v -> openVideoGallery());

        btnSave.setOnClickListener(v -> saveVideo());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null && data.getData() != null) {
            if (requestCode == PICK_IMAGE_REQUEST) {
                imageUri = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                    ivThumbnail.setImageBitmap(bitmap);
                    ivThumbnail.setVisibility(View.VISIBLE);
                    isImageSelected = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (requestCode == PICK_VIDEO_REQUEST) {
                videoUri = data.getData();
                isVideoSelected = true;
            }
        }
    }

    private void saveVideo() {
        if(isImageSelected && isVideoSelected){
            video.setTitle(Objects.requireNonNull(etTitle.getText()).toString());
            video.setDescription(Objects.requireNonNull(etDescription.getText()).toString());
            video.setChannelEmail(Objects.requireNonNull(etChannel.getText()).toString());
            Bitmap bitmap = ((BitmapDrawable) ivThumbnail.getDrawable()).getBitmap();
            String encodedImage = encodeImage(bitmap);
            video.setThumbnail(encodedImage);

            String encodedVideo = encodeVideo(videoUri);
            video.setMp4file(encodedVideo != null ? encodedVideo : "");

            if (isEditMode) {
                VideoManager.getVideoManager().updateVideo(video);
            } else {
                VideoManager.getVideoManager().addVideo(video);
            }

            Intent resultIntent = new Intent();
            resultIntent.putExtra("updatedVideoId", video.getId());
            setResult(RESULT_OK, resultIntent);
            finish();
        } else if (!isImageSelected)
            Toast.makeText(this, "Please select an image before saving.", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, "Please select a video before saving.", Toast.LENGTH_SHORT).show();
    }

    private void openImageGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void openVideoGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        intent.setType("video/*");
        startActivityForResult(intent, PICK_VIDEO_REQUEST);
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    private void checkAndRequestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    private String encodeVideo(Uri videoUri) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            InputStream inputStream = getContentResolver().openInputStream(videoUri);
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }
            inputStream.close();
            return Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String encodeImage(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        return Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
    }
    @Override
    public void onBackPressed() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("updatedVideoId", video.getId());
        setResult(RESULT_OK, resultIntent);
        super.onBackPressed();
    }
}
