package com.example.myyoutube;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myyoutube.classes.User;
import com.example.myyoutube.classes.UserManager;

public class signInScreen4 extends AppCompatActivity {

    private EditText emailInput;
    private Button nextButton;
    private TextView getGmail;
    private TextView errorMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_screen4);

        emailInput = findViewById(R.id.email_input);
        nextButton = findViewById(R.id.next_button);
        getGmail = findViewById(R.id.get_gmail);
        errorMsg = findViewById(R.id.tvErrorMsg);
        String name = getIntent().getStringExtra("name");

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorMsg.setText("");
                String email = emailInput.getText().toString().trim();
                if (isValidEmail(email)) {
                    String password = getIntent().getStringExtra("password");
                    if (UserManager.isEmailExist(email)) {
                        errorMsg.setText("Email already exists");
                        return;
                    }
                    Intent intent = new Intent(signInScreen4.this, signInScreen5.class);
                    intent.putExtra("name", name);
                    intent.putExtra("email", email);
                    intent.putExtra("password", password);
                    startActivity(intent);
                } else {
                    errorMsg.setText("Invalid email address");
                }
            }
        });

        getGmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorMsg.setText("Redirecting to get a Gmail address");
            }
        });
    }

    private boolean isValidEmail(CharSequence email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}