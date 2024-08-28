package com.example.myyoutube.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myyoutube.R;

public class signInScreen3 extends AppCompatActivity {
    private EditText password;
    private EditText confirm_password;
    private Button btn_next;
    private CheckBox showPasswordCheckBox;
    private TextView errorMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_screen3);

        password = findViewById(R.id.et_password);
        confirm_password = findViewById(R.id.et_confirm_password);
        btn_next = findViewById(R.id.btn_next);
        showPasswordCheckBox = findViewById(R.id.cb_show_password);
        errorMsg = findViewById((R.id.tvErrorMsg));
        String name = getIntent().getStringExtra("name");
        String lastName = getIntent().getStringExtra("lastName");
        String gender = getIntent().getStringExtra("gender");

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorMsg.setText("");
                String passwordText = password.getText().toString();
                String confirmPasswordText = confirm_password.getText().toString();

                if (passwordText.equals(confirmPasswordText)) {
                    if (passwordText.length() >= 8) {
                        if (hasLettersAndNumbers(passwordText)) {
                            errorMsg.setText("");
                            Intent intent = new Intent(signInScreen3.this, signInScreen4.class);
                            intent.putExtra("name", name);
                            intent.putExtra("password", passwordText);
                            intent.putExtra("lastName", lastName);
                            intent.putExtra("gender", gender);
                            startActivity(intent);
                        } else {
                            errorMsg.setText("Your password has to include letters and numbers");
                        }
                    } else {
                        errorMsg.setText("Password must be at least 8 characters long");
                    }
                } else {
                    errorMsg.setText("Passwords do not match");
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

    private boolean hasLettersAndNumbers(String password) {
        boolean hasLetter = false;
        boolean hasDigit = false;
        for (char c : password.toCharArray()) {
            if (Character.isLetter(c)) {
                hasLetter = true;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
            }
            if (hasLetter && hasDigit) {
                return true;
            }
        }
        return false;
    }
}
