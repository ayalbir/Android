package com.example.myyoutube.login;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myyoutube.R;
import com.example.myyoutube.viewmodels.UserViewModel;

import java.time.LocalDate;

public class signInScreen2 extends AppCompatActivity {
    //running
    private Spinner monthSpinner, genderSpinner;
    private EditText dayEditText, yearEditText;
    private Button nextButton;
    private TextView errorMsg;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_screen2);

        monthSpinner = findViewById(R.id.spinner_month);
        genderSpinner = findViewById(R.id.spinner_gender);
        dayEditText = findViewById(R.id.edittext_day);
        yearEditText = findViewById(R.id.edittext_year);
        nextButton = findViewById(R.id.button_next);
        errorMsg = findViewById(R.id.tvErrorMsg);

        String name = getIntent().getStringExtra("name");
        String lastName = getIntent().getStringExtra("lastName");
        ArrayAdapter<CharSequence> monthAdapter = ArrayAdapter.createFromResource(this,
                R.array.months_array, android.R.layout.simple_spinner_item);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthSpinner.setAdapter(monthAdapter);

        ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(this,
                R.array.gender_array, android.R.layout.simple_spinner_item);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(genderAdapter);


        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorMsg.setText("");
                if (yearEditText.getText().length() == 0 || dayEditText.getText().length() == 0 ||
                        monthSpinner.getSelectedItemPosition() == 0) {
                    errorMsg.setText("Please fill in a complete birthday");
                    return;
                }
                if (genderSpinner.getSelectedItemPosition() == 0) {
                    errorMsg.setText("Please select your gender");
                    return;
                }

                try {
                    int year = Integer.parseInt(yearEditText.getText().toString());
                    int day = Integer.parseInt(dayEditText.getText().toString());
                    int month = monthSpinner.getSelectedItemPosition();

                    // Check if the createdAt is valid
                    LocalDate birthDate = LocalDate.of(year, month, day);
                    LocalDate now = LocalDate.now();

                    // Check if the createdAt is in the future
                    if (birthDate.isAfter(now)) {
                        errorMsg.setText("You cannot create a Google Account because" + " you do not meet the minimum age requirement.");
                        return;
                    }

                    // Check if the createdAt is more than 130 years ago
                    int d = now.minusYears(130).getYear();
                    if (year < d) {
                        errorMsg.setText("Please check your date of birth again.");
                        return;
                    }

                    // If no errors, proceed to the next screen
                    errorMsg.setText("");
                    Intent intent = new Intent(signInScreen2.this, signInScreen3.class);
                    intent.putExtra("name", name);
                    intent.putExtra("lastName", lastName);
                    intent.putExtra("gender", genderSpinner.getSelectedItem().toString());
                    UserViewModel.setTempDate(birthDate);
                    startActivity(intent);

                } catch (Exception e) {
                    errorMsg.setText("Please enter a valid date.");
                }
            }
        });
    }
}
