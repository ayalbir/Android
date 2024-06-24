package com.example.myyoutube;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myyoutube.classes.User;
import com.example.myyoutube.login.logInScreen1;
import com.example.myyoutube.managers.UserManager;
import com.example.myyoutube.managers.VideoManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
// UpdateUserActivity.java

public class UpdateDeleteUserActivity extends AppCompatActivity {
    private EditText etUserName;
    private EditText etUserEmail;
    private EditText etPassword;
    private ImageView ivProfileImage;
    private User currentUser;
    private String originalEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_delete_user);

        etUserName = findViewById(R.id.etUserName);
        etUserEmail = findViewById(R.id.etUserEmail);
        etPassword = findViewById(R.id.etPassword);
        ivProfileImage = findViewById(R.id.ivProfileImage);

        // Get the current user and populate fields
        Intent intent = getIntent();
        originalEmail = intent.getStringExtra("userEmail");
        currentUser = UserManager.getUserByEmail(originalEmail);

        if (currentUser != null) {
            etUserName.setText(currentUser.getUserName());
            etUserEmail.setText(currentUser.getEmail());
            etPassword.setText(currentUser.getPassword());
            // Decode and set the profile image
            String profileImageBase64 = currentUser.getProfileImage();
            byte[] decodedString = Base64.decode(profileImageBase64, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            ivProfileImage.setImageBitmap(decodedByte);
        }

        findViewById(R.id.btnUpdateUser).setOnClickListener(v -> updateUser());
        findViewById(R.id.btnDeleteUser).setOnClickListener(v -> deleteUser());
    }

    private void updateUser() {
        String newUserName = etUserName.getText().toString();
        String newUserEmail = etUserEmail.getText().toString();
        String newPassword = etPassword.getText().toString();

        if (!newUserEmail.equalsIgnoreCase(originalEmail)) {
            UserManager.updateUserEmail(originalEmail, newUserEmail);
        }

        currentUser.setUserName(newUserName);
        currentUser.setPassword(newPassword);

        Toast.makeText(this, "User updated successfully", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("userEmail", currentUser.getEmail());
        startActivity(intent);
    }

    private void deleteUser() {
        UserManager.removeUser(UserManager.getUserByEmail(currentUser.getEmail()));
        Toast.makeText(this, "User deleted successfully", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, logInScreen1.class);
        intent.putExtra("userEmail", "");
        startActivity(intent);
    }
}
