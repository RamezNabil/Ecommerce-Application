package com.example.e_commerce;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.util.Calendar;

public class SignUp extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    // Global Variables and Views
    EditText name,email,password,job;
    TextView birthdate_text;
    Button birthdate_calender_btn, sign_up_btn;
    RadioButton male_radio, female_radio;
    EcommerceDatabaseHelper db;

    // Override for DialogFragment
    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {

        // Setting Calender to be viewed
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR,i);
        calendar.set(Calendar.MONTH,i1);
        calendar.set(Calendar.DAY_OF_MONTH,i2);

        // Getting Selected date and assigning it to textview
        String currentDate = DateFormat.getDateInstance().format(calendar.getTime());
        birthdate_text.setText(currentDate);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Assigning Views and Variables
        name = (EditText) findViewById(R.id.name_signup_textbox);
        email = (EditText) findViewById(R.id.email_signup_textbox);
        password = (EditText) findViewById(R.id.password_signup_textbox);
        birthdate_text = (TextView) findViewById(R.id.birthdate_text);
        birthdate_calender_btn = (Button) findViewById(R.id.date_pick_button);
        male_radio = (RadioButton) findViewById(R.id.male_radio);
        female_radio = (RadioButton) findViewById(R.id.female_radio);
        job = (EditText)findViewById(R.id.job_signup_textbox);
        sign_up_btn = (Button) findViewById(R.id.signup_button);
        db = new EcommerceDatabaseHelper(this);

        // Calender Dialog
        birthdate_calender_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment birthdate_picker = new DatePickerFragment();
                birthdate_picker.show(getSupportFragmentManager(),"birthdate picker");
            }
        });

        // Signing up
        sign_up_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Verification
                boolean verified = true;
                if(name.length() == 0)
                {
                    name.setError("Name is required");
                    verified = false;
                }
                if(email.length() == 0 || !email.getText().toString().contains("@") || !email.getText().toString().contains("."))
                {
                    email.setError("Please enter a valid email address");
                    verified = false;
                }
                if(password.length() < 8)
                {
                    password.setError("Password should at least be 8 characters");
                    verified = false;
                }
                if(birthdate_text.length() == 0)
                {
                    Toast.makeText(getApplicationContext(),"Please select your birthdate from calender button",Toast.LENGTH_LONG).show();
                    verified = false;
                }
                if(!male_radio.isChecked() && !female_radio.isChecked())
                {
                    Toast.makeText(getApplicationContext(),"Please select your Gender",Toast.LENGTH_LONG).show();
                    verified = false;
                }
                if(job.length() == 0)
                {
                    job.setError("Job is required");
                    verified = false;
                }

                // Making a new account
                if(verified)
                {
                    // Collecting input for creating account
                    String name_str = name.getText().toString();
                    String email_str = email.getText().toString();
                    String pass_str = password.getText().toString();
                    String gender_str;
                    if(male_radio.isChecked())
                        gender_str = "Male";
                    else
                        gender_str = "Female";
                    String birthdate_str = birthdate_text.getText().toString();
                    String job_str = job.getText().toString();

                    // Creating the account and feedback toast message
                    boolean success = db.createAccount(name_str,email_str,pass_str,gender_str,birthdate_str,job_str);
                    if(success)
                        Toast.makeText(getApplicationContext(),"Account created Successfully",Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(getApplicationContext(),"Email already exists, Sign in instead",Toast.LENGTH_SHORT).show();

                    // Returning to login page after creating account
                    Intent intent = new Intent(SignUp.this,MainActivity.class);
                    startActivity(intent);
                }
            }
        });
    }
}