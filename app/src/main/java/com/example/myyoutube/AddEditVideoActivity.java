package com.example.myyoutube;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.myyoutube.classes.Video;
import com.example.myyoutube.classes.VideoManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;

public class AddEditVideoActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int REQUEST_CAMERA = 2;
    private static final int PERMISSION_REQUEST_CODE = 3;

    private EditText etTitle, etDescription, etChannel;
    private ImageView ivThumbnail;
    private boolean isImageSelected = false;
    private Uri imageUri, videoUri;
    private SharedPreferences sharedPreferences;
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
        sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);


        checkAndRequestPermissions();

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
            showImagePickerOptions();
        });

        btnSelectVideo.setOnClickListener(v -> {
            showImagePickerOptions();
        });

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
                    saveImageToPreferences(bitmap);
                    isImageSelected = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ivThumbnail.setImageURI(imageUri);
            } else if (requestCode == REQUEST_CAMERA) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                ivThumbnail.setImageBitmap(photo);
                saveImageToPreferences(photo);
                isImageSelected = true;
                //videoUri = data.getData();
                Toast.makeText(this, "Video selected: " + videoUri.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void saveVideo() {
        if (isImageSelected) {
            Intent intent = new Intent(AddEditVideoActivity.this, MainActivity.class);
            startActivity(intent);
        }
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

        Intent resultIntent = new Intent();
        resultIntent.putExtra("updatedVideoId", video.getId());
        setResult(RESULT_OK, resultIntent);
        finish();
    }
    private void showImagePickerOptions() {
        String[] options = {"Take Photo", "Choose from Gallery"};
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Choose an option");
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                openCamera();
            } else if (which == 1) {
                openGallery();
            }
        });
        builder.show();
    }
    private void saveImageToPreferences(Bitmap bitmap) {
        Intent recvIntent = getIntent();
        String email = recvIntent.getStringExtra("email");
        SharedPreferences.Editor editor = sharedPreferences.edit();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        String encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
        editor.putString("profile_image_" + email, encodedImage);  // Use unique key for each user
        editor.apply();
    }
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }
    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }
    private void checkAndRequestPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    0);
        }
    }
    @Override
    public void onBackPressed() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("updatedVideoId", video.getId());
        setResult(RESULT_OK, resultIntent);
        super.onBackPressed();
    }
}