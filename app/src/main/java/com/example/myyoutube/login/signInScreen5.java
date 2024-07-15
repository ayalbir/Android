package com.example.myyoutube.login;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.myyoutube.Helper;
import com.example.myyoutube.R;
import com.example.myyoutube.entities.User;
import com.example.myyoutube.viewmodels.UserManager;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import okhttp3.internal.http2.Header;

public class signInScreen5 extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int REQUEST_CAMERA = 2;
    private ImageView selectedImageView;
    private Uri imageUri;
    private boolean isImageSelected = false;
    private TextView errorMsg;
    private UserManager userManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_screen5);
        userManager = UserManager.getInstance();
        selectedImageView = findViewById(R.id.selectedImageView);
        Button btnSelectImage = findViewById(R.id.btnSelectImage);
        Button btnLogin = findViewById(R.id.btnLogin);
        errorMsg = findViewById(R.id.tvErrorMsg);

        checkAndRequestPermissions();

        btnSelectImage.setOnClickListener(v -> {
            errorMsg.setText("");
            showImagePickerOptions();
        });


        btnLogin.setOnClickListener(v -> {
            if (isImageSelected) {
                Toast.makeText(signInScreen5.this, "Logging in...", Toast.LENGTH_SHORT).show();

                String email = getIntent().getStringExtra("email");
                String name = getIntent().getStringExtra("name");
                String password = getIntent().getStringExtra("password");
                String lastName = getIntent().getStringExtra("lastName");
                String gender = getIntent().getStringExtra("gender");

                Bitmap bitmap = ((BitmapDrawable) selectedImageView.getDrawable()).getBitmap();
                String encodedImage = encodeImage(bitmap);
                assert email != null;
                userManager.createUser(new User(email, password, name, lastName, UserManager.getTempDate(),gender, encodedImage));
                Intent intent = new Intent(signInScreen5.this, logInScreen1.class);
                intent.putExtra("email", email);
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
                        isImageSelected = true;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else if (requestCode == REQUEST_CAMERA) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                selectedImageView.setImageBitmap(photo);
                isImageSelected = true;
            }
        } else {
            errorMsg.setText("You haven't picked an image");
        }
    }

    private String encodeImage(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        return Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
    }
}
