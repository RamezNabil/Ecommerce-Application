package com.example.e_commerce;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Declarations and Assignments
        TextView sign_up = (TextView)findViewById(R.id.signup_link);
        TextView forget_pass = (TextView)findViewById(R.id.forgetpass_link);
        Button sign_in_btn = (Button)findViewById(R.id.signin_button);
        CheckBox remember_me = (CheckBox)findViewById(R.id.remember_me_checkbox);
        EditText email = (EditText)findViewById(R.id.email_textbox);
        EditText pass = (EditText)findViewById(R.id.password_textbox);


        EcommerceDatabaseHelper db = new EcommerceDatabaseHelper(this);
        String shared_pref_file = "com.example.e_commerce";
        SharedPreferences shared_preferences = getSharedPreferences(shared_pref_file,MODE_PRIVATE);

        // Getting data from shared preferences on create
        email.setText(shared_preferences.getString("email",""));
        pass.setText(shared_preferences.getString("password",""));
        remember_me.setChecked(shared_preferences.getBoolean("remember_me",false));

        // Sign Up Link
        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Going to sign up page
                Intent intent = new Intent(MainActivity.this, SignUp.class);
                startActivity(intent);
            }
        });

        // Sign in Button
        sign_in_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Getting input for sign in
                String email_str = email.getText().toString();
                String pass_str = pass.getText().toString();

                // Admin Login
                if(email_str.equals("admin") && pass_str.equals("admin"))
                {
                    Intent intent = new Intent(MainActivity.this, Admin.class);
                    startActivity(intent);
                }

                // User Login
                else
                {
                    // Sign in Verification
                    Cursor cursor = db.signIn(email_str, pass_str);
                    if (!cursor.isAfterLast())
                    {
                        // Checking for remember me
                        if (remember_me.isChecked())
                        {
                            // Saving data in shared preferences
                            SharedPreferences.Editor preferences_editor = shared_preferences.edit();
                            preferences_editor.putString("email", email_str);
                            preferences_editor.putString("password", pass_str);
                            preferences_editor.putBoolean("remember_me", remember_me.isChecked());
                            preferences_editor.apply();
                        }
                        else
                        {
                            // Clearing data from shared preferences
                            SharedPreferences.Editor preferences_editor = shared_preferences.edit();
                            preferences_editor.clear();
                            preferences_editor.apply();
                        }

                        // Signing in and sending customer info to next page
                        Intent intent = new Intent(MainActivity.this, MainMenu.class);
                        String[] customer = {cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3),
                                cursor.getString(4), cursor.getString(5), cursor.getString(6)};
                        intent.putExtra("customer", customer);
                        startActivity(intent);
                    }
                    else
                    {
                        // Email and password combination not found
                        Toast.makeText(getApplicationContext(), "Email or password is incorrect", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        // forget password link
        forget_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ForgetPassword.class);
                startActivity(intent);
            }
        });

        // Setting up the database
        // Inserting Categories
        db.insertCat("Electronics");
        db.insertCat("Fashion");
        db.insertCat("PC & Accessories");
        db.insertCat("Mobiles & Tablets");
        db.insertCat("Health & Beauty");
        db.insertCat("Kitchen Utensils");
        db.insertCat("Toys");

        // Inserting Products
        db.insertProduct("Samsung GE614ST/EGY Microwave",4399.50f,3,"9421021461303",1);
        db.insertProduct("Toshiba GR-EFV45-S No Frost Refrigerator with 3 Doors, 395 Liters, Silver",6999.00f,0,"0701197207776",1);
        db.insertProduct("Mienta TO21409B Toaster 800 W, Black",399,5,"0610696092267",1);

        db.insertProduct("Town Team Crew-Neck Embroidered Letters Patterned Sweatshirt for Men - Navy, XL",249,4,"0610696092250",2);
        db.insertProduct("Trousers-scratched jeans-dark blue",249,3,"0610696092243",2);
        db.insertProduct("Rimini Black Fashion Sneakers For Men",127,5,"0610696088673",2);

        db.insertProduct("WD Green SSD WDS240 G2G0 A 240GB",625,9,"0609722921473",3);
        db.insertProduct("VGA MSI NVIDIA GEFORCE RTX 2080 SUPER VENTUS XS OC",15900,1,"0609722923095",3);
        db.insertProduct("Logitech Wireless Mk220 Keyboard And Mouse Combo (black)",425,3,"0609722923279",3);

        db.insertProduct("Nokia C1, Dual Sim, 5.45 Inch, 16 GB, 1 GB RAM, 3G - Charcoal",930,3,"0609722923316",4);
        db.insertProduct("Xiaomi POCO X3 Dual SIM Mobile, 6.67 Inches, 128 GB, 6 GB RAM, 4G LTE - Cobalt Blue",4225,1,"0609722923330",4);
        db.insertProduct("Lenovo Tab TB-7305I - 1GB RAM - 16GB - Onyx Black",1550,6,"0609722923101",4);

        db.insertProduct("Head & Shoulders Smooth and Silky Anti-Dandruff Shampoo 400ml",45,15,"0609722923941",5);
        db.insertProduct("Kemei KM-1622 4 in 1 Multifunctional Electric Hair Clipper for Men",217,3,"0793573465900",5);
        db.insertProduct("St. Ives Facial Moisturizer Renewing Skin Collagen & Elastin,283 g",126.99f,5,"0609722923088",5);

        db.insertProduct("1Pcs Vegetable Carrot Blade Potato Crinkle Wavy Cutter Slicer",30,12,"0799439693777",6);
        db.insertProduct("Neoflam Tily EK-Tl-F26 Aluminum Frying Pan - Cyan, 26 cm",535,7,"0799439653054",6);
        db.insertProduct("Stainless Steel Lemon Squeezer - Silver",19,20,"0799439653061",6);

        db.insertProduct("Darts Board Game with 4 Darts",60,8,"0799439653078",7);
        db.insertProduct("Bingo Zoba Ride-On Car for Kids - Red and Orange",699,2,"0799439653085",7);
        db.insertProduct("Keel SF0957 Animotsu Panda Plush Toy, 15 cm - Multi Color",135,7,"0799439653092",7);

        // Testing updates
        //db.editProductQuantity(1,3);
        //db.editProductQuantity(3,5);
    }
}