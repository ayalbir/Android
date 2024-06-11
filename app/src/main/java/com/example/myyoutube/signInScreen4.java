package com.example.myyoutube;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class signInScreen4 extends AppCompatActivity {

    private EditText emailInput;
    private Button nextButton;
    private TextView getGmail;
    private TextView errorMsg;  // Error message TextView

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_screen4);

        emailInput = findViewById(R.id.email_input);
        nextButton = findViewById(R.id.next_button);
        getGmail = findViewById(R.id.get_gmail);
        errorMsg = findViewById(R.id.tvErrorMsg);  // Initialize the error message TextView

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorMsg.setText("");  // Clear any previous error messages
                String email = emailInput.getText().toString().trim();
                if (isValidEmail(email)) {
                    Intent recvIntent = getIntent();
                    String password = recvIntent.getStringExtra("password");
                    SharedPreferences prefs = getApplicationContext().getSharedPreferences("shared", Context.MODE_PRIVATE);
                    boolean isEmailExist = !prefs.getString(email, "not found").equals("not found");
                    if (isEmailExist) {
                        errorMsg.setText("Email already exists");
                        return;
                    }
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString(email, password);
                    editor.apply();
                    Intent intent = new Intent(signInScreen4.this, signInScreen5.class);
                    intent.putExtra("email", email);
                    startActivity(intent);
                } else {
                    errorMsg.setText("Invalid email address");
                }
            }
        });

        getGmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the action for getting a Gmail address instead
                errorMsg.setText("Redirecting to get a Gmail address");
            }
        });
    }

    private boolean isValidEmail(CharSequence email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
