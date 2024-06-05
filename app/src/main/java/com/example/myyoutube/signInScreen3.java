package com.example.myyoutube;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.time.LocalDateTime;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.time.LocalDateTime;
import java.util.Date;

public class signInScreen3 extends AppCompatActivity {
    private EditText password;
    private EditText confirm_password ;
    private Button btn_next;
    private CheckBox showPasswordCheckBox;
    private TextView errorMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_screen3);

        // Initialize views
        password = findViewById(R.id.et_password);
        confirm_password = findViewById(R.id.et_confirm_password);
        btn_next = findViewById(R.id.btn_next);
        showPasswordCheckBox = findViewById(R.id.cb_show_password);
        errorMsg = findViewById((R.id.tvErrorMsg));

        btn_next.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                errorMsg.setText("");
                EditText passwordEditText = findViewById(R.id.et_password);
                EditText confirmPasswordEditText = findViewById(R.id.et_confirm_password);
                String password = passwordEditText.getText().toString();
                String confirmPassword = confirmPasswordEditText.getText().toString();

                if (password.equals(confirmPassword) && password.length() >= 8) {
                    errorMsg.setText("");
                    Intent intent = new Intent(signInScreen3.this, signInScreen3.class);
                    startActivity(intent);
                } else {
                    if (!password.equals(confirmPassword)) {
                        errorMsg.setText("Passwords do not match");
                        Toast.makeText(signInScreen3.this, "Passwords do not match", Toast.LENGTH_SHORT).show();


                    }
                    if (password.length() < 8) {
                        errorMsg.setText("Password must be at least 8 characters long");
                        Toast.makeText(signInScreen3.this, "Password must be at least 8 characters long", Toast.LENGTH_SHORT).show();


                    }
                }

            }


        });
        showPasswordCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                confirm_password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            } else {
                password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                confirm_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
           password.setSelection(password.getText().length());
            confirm_password.setSelection(confirm_password.getText().length());
        });


    }
}