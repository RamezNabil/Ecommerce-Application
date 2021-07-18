package com.example.e_commerce;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class Cart extends AppCompatActivity {

    // Holding cart products info for recycler view
    List<String> product_names;
    List<Float> product_prices;
    List<Integer> product_quantities;
    List<Integer> product_ids;

    String[] customer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // Retriving Customer Details
        customer = getIntent().getStringArrayExtra("customer");

        TextView total_price_text = (TextView) findViewById(R.id.total_price);
        Button checkout_proceed = (Button)findViewById(R.id.proceed_checkout_btn);
        float total_price = 0;

        // Initializing the lists
        product_names = new ArrayList<>();
        product_prices = new ArrayList<>();
        product_quantities = new ArrayList<>();
        product_ids = new ArrayList<>();

        // Retriving cart products info and filling lists
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        EcommerceDatabaseHelper db = new EcommerceDatabaseHelper(this);
        int cart_items = db.getMaxProductID();
        for(int i=1; i<=cart_items; i++)
        {
            int quantity = sharedPreferences.getInt(String.valueOf(i),0);
            // Found in shared pref
            if(quantity!=0)
            {
                Cursor cursor = db.searchProductsID(i);
                while (!cursor.isAfterLast())
                {
                    product_names.add(cursor.getString(0));
                    float prod_price = cursor.getFloat(1);
                    product_prices.add(prod_price);
                    product_quantities.add(quantity);
                    product_ids.add(i);
                    total_price += prod_price * quantity;
                    cursor.moveToNext();
                }
            }
        }

        // Setting Total Price text
        DecimalFormat df = new DecimalFormat("0.00");
        total_price_text.setText(df.format(total_price) + " Egp");

        // Passing cart products info to adapter
        RecyclerView cart_recycler = (RecyclerView)findViewById(R.id.cart_recycler_view);
        CartRecyclerAdapter cart_adapter = new CartRecyclerAdapter(this,product_names,product_prices,product_quantities,product_ids,db,total_price_text);
        cart_recycler.setAdapter(cart_adapter);
        cart_recycler.setLayoutManager(new LinearLayoutManager(this));

        // Proceeding to select address location
        checkout_proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // No items in cart to proceed
                if(product_ids.size() == 0)
                {
                    Toast.makeText(getApplicationContext(),"Cart is Empty, Add items to cart before proceeding to checkout",Toast.LENGTH_LONG).show();
                    return;
                }

                Intent intent = new Intent(Cart.this,MapsActivity.class);
                intent.putExtra("customer",customer);

                // Converting Lists to array to pass in intent
                int[] product_ids_arr = new int[product_ids.size()];
                int[] product_quantities_arr = new int[product_quantities.size()];
                String[] product_names_arr = new String[product_names.size()];
                float[] product_prices_arr = new float[product_prices.size()];

                for(int i=0; i<product_ids.size(); i++)
                {
                    product_ids_arr[i] = product_ids.get(i);
                    product_quantities_arr[i] = product_quantities.get(i);
                    product_names_arr[i] = product_names.get(i);
                    product_prices_arr[i] = product_prices.get(i);
                }

                intent.putExtra("product_ids",product_ids_arr);
                intent.putExtra("product_quantities",product_quantities_arr);
                intent.putExtra("product_names",product_names_arr);
                intent.putExtra("product_prices",product_prices_arr);
                startActivity(intent);
            }
        });
    }
}