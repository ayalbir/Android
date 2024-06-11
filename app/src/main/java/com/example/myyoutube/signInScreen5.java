package com.example.myyoutube;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class signInScreen5 extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int REQUEST_CAMERA = 2;
    private ImageView selectedImageView;
    private Uri imageUri;
    private SharedPreferences sharedPreferences;
    private boolean isImageSelected = false;  // Track if an image has been selected
    private TextView errorMsg;  // Error message TextView

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_screen5);

        selectedImageView = findViewById(R.id.selectedImageView);
        Button btnSelectImage = findViewById(R.id.btnSelectImage);
        Button btnLogin = findViewById(R.id.btnLogin);  // New button for login
        errorMsg = findViewById(R.id.tvErrorMsg);  // Initialize the error message TextView
        sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);

        checkAndRequestPermissions();

        btnSelectImage.setOnClickListener(v -> {
            errorMsg.setText("");  // Clear the error message when selecting a new image
            showImagePickerOptions();
        });

        // New listener for the login button
        btnLogin.setOnClickListener(v -> {
            if (isImageSelected) {
                Intent intent = new Intent(signInScreen5.this, logInScreen1.class);
                startActivity(intent);
            } else {
                errorMsg.setText("Please select an image before proceeding to login.");
            }
        });
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

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE_REQUEST) {
                if (data != null && data.getData() != null) {
                    imageUri = data.getData();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                        selectedImageView.setImageBitmap(bitmap);
                        saveImageToPreferences(bitmap);
                        isImageSelected = true;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else if (requestCode == REQUEST_CAMERA) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                selectedImageView.setImageBitmap(photo);
                saveImageToPreferences(photo);
                isImageSelected = true;
            }
        } else {
            errorMsg.setText("You haven't picked an image");
        }
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

    private void loadSavedImage() {
        // This method is now unused but kept for reference
    }
}
