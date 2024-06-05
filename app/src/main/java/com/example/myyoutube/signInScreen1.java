package com.example.myyoutube;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.snackbar.Snackbar;

public class signInScreen1 extends AppCompatActivity {
    public EditText firstName, lastName;
    public TextView errorMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_in_screen1);
        this.firstName = findViewById(R.id.etFirstName);
        this.lastName = findViewById(R.id.etLastName);
        this.errorMsg = findViewById(R.id.tvErrorMsg);
        Button button = findViewById(R.id.button);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = firstName.getText().toString();
                boolean isName = name.length()>0 && name.length()<51;
                if(isName){
                    Intent intent = new Intent(signInScreen1.this,signInScreen2.class);
                    startActivity(intent);
                    errorMsg.setText("");
                }
                else if(name.isEmpty()){
                    errorMsg.setText("Enter first name");
                } else if (name.length() > 50) {
                    errorMsg.setText("Are you sure you entered your name correctly?");
                    
                }


            }
        });
    }
    private void onClick() {}
}