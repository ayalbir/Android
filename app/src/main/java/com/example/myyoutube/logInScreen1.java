package com.example.myyoutube;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class logInScreen1 extends AppCompatActivity {
    private EditText password;
    private EditText email;
    private Button btn_login;
    private Button btn_create;
    private CheckBox showPasswordCheckBox;
    private TextView errorMsg;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in_screen1);

        // Initialize views
        password = findViewById(R.id.et_password);
        email = findViewById(R.id.email_or_phone);
        btn_login = findViewById(R.id.btn_login);
        btn_create = findViewById(R.id.btn_create); // Initialize btn_create
        showPasswordCheckBox = findViewById(R.id.cb_show_password);
        errorMsg = findViewById(R.id.tvErrorMsg);

        // Set click listener for login button
        btn_login.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = getApplicationContext().getSharedPreferences("shared", Context.MODE_PRIVATE);
                String currentEmail = email.getText().toString().trim();
                String savedpass = prefs.getString(currentEmail, "");
                if (!savedpass.equals("")
                        && savedpass.equals(password.getText().toString())) {
                    errorMsg.setText("");
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("currentEmail", currentEmail);
                    Intent intent = new Intent(logInScreen1.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    errorMsg.setText("could not find your Foo Tube account.");
                }
            }
        });

        // Set click listener for create account button
        btn_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(logInScreen1.this, signInScreen1.class);
                startActivity(intent);
            }
        });

        // Show or hide password based on checkbox state
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