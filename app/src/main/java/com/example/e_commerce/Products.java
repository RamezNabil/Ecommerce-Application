package com.example.e_commerce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class Products extends AppCompatActivity {

    // Holding products info for recycler view
    List<String> product_names;
    List<Float> product_prices;
    List<Integer> product_quantities;
    List<Integer> product_ids;

    String[] customer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);

        // Retriving Customer Details
        customer = getIntent().getStringArrayExtra("customer");

        // Initializing the lists
        product_names = new ArrayList<>();
        product_prices = new ArrayList<>();
        product_quantities = new ArrayList<>();
        product_ids = new ArrayList<>();

        // Retriving products info from database and filling lists
        EcommerceDatabaseHelper db = new EcommerceDatabaseHelper(this);
        int cat_id = getIntent().getIntExtra("cat_id",1);
        Cursor cursor = db.fetchProducts(cat_id);
        while (!cursor.isAfterLast())
        {
            product_names.add(cursor.getString(0));
            product_prices.add(cursor.getFloat(1));
            product_quantities.add(cursor.getInt(2));
            product_ids.add(cursor.getInt(3));
            cursor.moveToNext();
        }

        // Passing products info to adapter
        RecyclerView products_recycler = (RecyclerView)findViewById(R.id.products_recycler);
        ProductsRecyclerAdapter pro_adapter = new ProductsRecyclerAdapter(this,product_names,product_prices,product_quantities,product_ids,db);
        products_recycler.setAdapter(pro_adapter);
        products_recycler.setLayoutManager(new LinearLayoutManager(this));
    }

    // Inflating home menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu,menu);
        return true;
    }

    // When selecting from home menu
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.browse_menu_item)
        {
            Intent intent = new Intent(Products.this, Categories.class);
            intent.putExtra("customer",customer);
            startActivity(intent);
            return true;
        }
        else if(id == R.id.cart_menu_item)
        {
            Intent intent = new Intent(Products.this, Cart.class);
            intent.putExtra("customer",customer);
            startActivity(intent);
            return true;
        }
        return false;
    }

}