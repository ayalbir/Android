package com.example.myyoutube.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myyoutube.R;
import com.example.myyoutube.entities.User;
import com.example.myyoutube.viewmodels.UserViewModel;

public class signInScreen4 extends AppCompatActivity {

    private EditText emailInput;
    private Button nextButton;
    private TextView getGmail;
    private TextView errorMsg;
    private UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_screen4);
        userViewModel = UserViewModel.getInstance();
        emailInput = findViewById(R.id.email_input);
        nextButton = findViewById(R.id.next_button);
        getGmail = findViewById(R.id.get_gmail);
        errorMsg = findViewById(R.id.tvErrorMsg);
        String name = getIntent().getStringExtra("name");
        String lastName = getIntent().getStringExtra("lastName");
        String gender = getIntent().getStringExtra("gender");


        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorMsg.setText("");
                String email = emailInput.getText().toString().trim();
                if (isValidEmail(email)) {
                    String password = getIntent().getStringExtra("password");
                    User user = userViewModel.getUserByEmail(email);
                    if (user != null) {
                        errorMsg.setText("Email already exists");
                        return;
                    }
                    Intent intent = new Intent(signInScreen4.this, signInScreen5.class);
                    intent.putExtra("name", name);
                    intent.putExtra("email", email);
                    intent.putExtra("password", password);
                    intent.putExtra("lastName", lastName);
                    intent.putExtra("gender", gender);
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