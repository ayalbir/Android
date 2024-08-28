package com.example.myyoutube.login;


import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myyoutube.R;
import com.example.myyoutube.entities.User;
import com.example.myyoutube.screens.MainActivity;
import com.example.myyoutube.viewmodels.UserViewModel;

public class logInScreen1 extends AppCompatActivity {
    private EditText password;
    private EditText email;
    private Button btn_login;
    private Button btn_create;
    private CheckBox showPasswordCheckBox;
    private UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in_screen1);

        password = findViewById(R.id.et_password);
        email = findViewById(R.id.email_or_phone);
        btn_login = findViewById(R.id.btn_login);
        btn_create = findViewById(R.id.btn_create);
        showPasswordCheckBox = findViewById(R.id.cb_show_password);
        userViewModel = UserViewModel.getInstance();
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentEmail = email.getText().toString().trim();
                String currentPassword = password.getText().toString().trim();
                User user = userViewModel.getUserByEmail(currentEmail);

                if (user != null && (password.getText().toString().trim().equals(user.getPassword()))) {
                    Intent intent = new Intent(logInScreen1.this, MainActivity.class);
                    UserViewModel.setConnectedUser(user);
                    userViewModel.signIn(currentEmail, currentPassword);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(logInScreen1.this, "Incorrect credentials", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(logInScreen1.this, signInScreen1.class);
                startActivity(intent);
            }
        });

        showPasswordCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            } else {
                password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
            password.setSelection(password.getText().length());
        });
    }
}