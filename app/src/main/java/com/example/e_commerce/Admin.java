package com.example.e_commerce;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Admin extends AppCompatActivity {

    EditText name,price,quantity,barcode,catID;
    Button insert_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        name = findViewById(R.id.insert_product_name);
        price = findViewById(R.id.insert_product_price);
        quantity = findViewById(R.id.insert_product_quantity);
        barcode = findViewById(R.id.insert_product_barcode);
        catID = findViewById(R.id.insert_product_catID);
        insert_btn = findViewById(R.id.insert_product);

        EcommerceDatabaseHelper db = new EcommerceDatabaseHelper(this);
        insert_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.insertProduct(name.getText().toString(),Float.parseFloat(price.getText().toString()),
                        Integer.parseInt(quantity.getText().toString()),barcode.getText().toString(),
                        Integer.parseInt(catID.getText().toString()));
            }
        });
    }
}