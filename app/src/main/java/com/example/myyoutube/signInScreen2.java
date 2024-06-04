package com.example.myyoutube;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import java.time.LocalDateTime;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.time.LocalDateTime;
import java.util.Date;

public class signInScreen2 extends AppCompatActivity {
    private Spinner monthSpinner, genderSpinner;
    private EditText dayEditText, yearEditText;
    private Button nextButton;
    private TextView errorMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_in_screen2);
        monthSpinner = findViewById(R.id.spinner_month);
        genderSpinner = findViewById(R.id.spinner_gender);
        dayEditText = findViewById(R.id.edittext_day);
        yearEditText = findViewById(R.id.edittext_year);
        nextButton = findViewById(R.id.button_next);
        errorMsg = findViewById(R.id.tvErrorMsg);



        ArrayAdapter<CharSequence> monthAdapter = ArrayAdapter.createFromResource(this,
                R.array.months_array, android.R.layout.simple_spinner_item);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthSpinner.setAdapter(monthAdapter);

        ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(this,
                R.array.gender_array, android.R.layout.simple_spinner_item);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(genderAdapter);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                errorMsg.setText("");
                int month = monthSpinner.getSelectedItemPosition();
                if(yearEditText.getText().length() == 0 || dayEditText.getText().length() == 0 ||
                monthSpinner.getSelectedItemPosition()==0){
                    errorMsg.setText("Please fill in a complete birthday");
                    return;
                }
                if(genderSpinner.getSelectedItemPosition()==0){
                    errorMsg.setText("Please select your gender");
                    return;
                }
                int year = Integer.parseInt(yearEditText.getText().toString());
                int day = Integer.parseInt(dayEditText.getText().toString());
                int daysInMonth = 0;
                switch (month) {
                    case 1:
                    case 3:
                    case 5:
                    case 7:
                    case 8:
                    case 10:
                    case 12:
                        daysInMonth = 31;
                        break;
                    case 4:
                    case 6:
                    case 9:
                    case 11:
                        daysInMonth = 30;
                        break;
                    case 2:
                        if (year % 4 == 0 && year % 100 != 0 || (year % 400 == 0)) {
                            daysInMonth = 29;
                        } else {
                            daysInMonth = 28;
                        }
                        break;
                }
                boolean error = false;
                if (day < 1 || day > daysInMonth) {
                    errorMsg.setText("Please enter a valid date.");
                    error = true;
                }
                Date birthDay = new Date(year-1900, month-1, day);
                Date now = new Date();

                if (birthDay.after(now)) {
                    errorMsg.setText("You cannot create a Google Account because" +
                            " you do not meet the minimum age requirement. ");
                    error = true;
                }
                if (year < now.getYear() -130) {
                    errorMsg.setText("Please check your date of birth again.");
                    error = true;
                }

                if(!error){
                    errorMsg.setText("");
                    Intent intent = new Intent(signInScreen2.this, signInScreen3.class);
                    startActivity(intent);
                }




            }




        });


    }
}