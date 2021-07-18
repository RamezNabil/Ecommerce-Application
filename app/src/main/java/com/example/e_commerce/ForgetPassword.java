package com.example.e_commerce;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ForgetPassword extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        // Declarations and Assignments
        EditText email = (EditText)findViewById(R.id.forget_pass_email_textbox);
        Button send_btn = (Button)findViewById(R.id.send_button);
        EcommerceDatabaseHelper db = new EcommerceDatabaseHelper(this);

        // Send Button Clicked
        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Check if email is in database
                String email_str = email.getText().toString();
                String pass = db.forgetPass(email_str);
                if(!pass.equals(""))
                {
                    // Sending email
                    String subject = "Ecommerce Password Recovery";
                    String msg = "Your password is: " + pass;
                    JavaMailAPI javaMailAPI = new JavaMailAPI(ForgetPassword.this,email_str,subject,msg);
                    javaMailAPI.execute();
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Email is not registered, Sign up instead",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}