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

import com.example.myyoutube.R;
import com.example.myyoutube.entities.User;
import com.example.myyoutube.login.logInScreen1;
import com.example.myyoutube.viewmodels.UserManager;
import com.example.myyoutube.viewmodels.VideosViewModel;

import java.util.Objects;


public class UpdateDeleteUserActivity extends AppCompatActivity {
    private EditText etUserName;
    private EditText etUserEmail;
    private EditText etPassword;
    private ImageView ivProfileImage;
    private User currentUser;
    private UserManager userManager = UserManager.getInstance();
    private VideosViewModel videosViewModel;
    String oldEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_delete_user);

        videosViewModel = new ViewModelProvider(this).get(VideosViewModel.class);

        etUserName = findViewById(R.id.etUserName);
        etUserEmail = findViewById(R.id.etUserEmail);
        etPassword = findViewById(R.id.etPassword);
        ivProfileImage = findViewById(R.id.ivProfileImage);
        findViewById(R.id.btnRet).setOnClickListener(v -> {
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
        });

        currentUser = UserManager.getConnectedUser();
        oldEmail = currentUser.getEmail();

        if (currentUser != null) {
            etUserName.setText(currentUser.getFirstName());
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
        findViewById(R.id.btnSighOut).setOnClickListener(v -> signOut());
    }

    private void updateUser() {
        String newUserName = etUserName.getText().toString();
        String newUserEmail = etUserEmail.getText().toString();
        String newPassword = etPassword.getText().toString();
//            videosViewModel.updateCommentsEmail(oldEmail, newUserEmail);

        currentUser.setEmail(newUserEmail);
        currentUser.setFirstName(newUserName);
        currentUser.setPassword(newPassword);

        userManager.updateUser(oldEmail, currentUser);

        Toast.makeText(this, "User updated successfully", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("userEmail", currentUser.getEmail());
        startActivity(intent);
    }

    private void deleteUser() {
        userManager.clearConnectedUser();
        userManager.deleteUser(currentUser.getEmail());
        Toast.makeText(this, "User deleted successfully", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void signOut() {
        userManager.clearConnectedUser();
        Toast.makeText(this, "Signing out", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
