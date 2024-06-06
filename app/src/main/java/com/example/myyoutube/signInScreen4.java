package com.example.myyoutube;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class signInScreen4 extends AppCompatActivity {

    private EditText emailInput;
    private Button nextButton;
    private TextView getGmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_screen4);

        emailInput = findViewById(R.id.email_input);
        nextButton = findViewById(R.id.next_button);
        getGmail = findViewById(R.id.get_gmail);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailInput.getText().toString().trim();
                if (isValidEmail(email)) {
                    Toast.makeText(signInScreen4.this, "Email is valid", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(signInScreen4.this, signInScreen2.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(signInScreen4.this, "Invalid email address", Toast.LENGTH_SHORT).show();
                }
            }
        });

        getGmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the action for getting a Gmail address instead
                Toast.makeText(signInScreen4.this, "Redirecting to get a Gmail address", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isValidEmail(CharSequence email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
