package com.example.myyoutube.screens;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.myyoutube.Helper;
import com.example.myyoutube.R;
import com.example.myyoutube.entities.User;
import com.example.myyoutube.viewmodels.UserViewModel;
import com.example.myyoutube.viewmodels.VideosViewModel;


public class UpdateDeleteUserActivity extends AppCompatActivity {
    private final UserViewModel userViewModel = UserViewModel.getInstance();
    String oldEmail;
    private EditText etUserName;
    private EditText etPassword;
    private ImageView ivProfileImage;
    private User currentUser;
    private VideosViewModel videosViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_delete_user);

        videosViewModel = new ViewModelProvider(this).get(VideosViewModel.class);

        etUserName = findViewById(R.id.etUserName);
        etPassword = findViewById(R.id.etPassword);
        ivProfileImage = findViewById(R.id.ivProfileImage);
        findViewById(R.id.btnRet).setOnClickListener(v -> {
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
        });

        currentUser = Helper.getConnectedUser();

        if (currentUser != null) {
            oldEmail = currentUser.getEmail();
            etUserName.setText(currentUser.getFirstName());
            etPassword.setText(currentUser.getPassword());
            // Decode and set the profile image
            String profileImageBase64 = currentUser.getProfileImage();
            byte[] decodedString = Base64.decode(profileImageBase64, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            ivProfileImage.setImageBitmap(decodedByte);
        }

        findViewById(R.id.btnUpdateUser).setOnClickListener(v -> updateUser());
        findViewById(R.id.btnDeleteUser).setOnClickListener(v -> deleteUser());
        findViewById(R.id.btnSighOut).setOnClickListener(v -> signOut());
    }

    private void updateUser() {
        String newUserName = etUserName.getText().toString();
        String newPassword = etPassword.getText().toString();

        currentUser.setFirstName(newUserName);
        currentUser.setPassword(newPassword);
        Helper.setConnectedUser(currentUser);
        userViewModel.updateUser(oldEmail, currentUser);

        Toast.makeText(this, "User updated successfully", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("userEmail", currentUser.getEmail());
        startActivity(intent);
    }

    private void deleteUser() {
        userViewModel.deleteUser(currentUser.getEmail());
        Helper.clearConnectedUser();
        Toast.makeText(this, "User deleted successfully", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void signOut() {
        Helper.clearConnectedUser();
        Toast.makeText(this, "Signing out", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
