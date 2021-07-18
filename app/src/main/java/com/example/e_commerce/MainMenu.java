package com.example.e_commerce;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;

public class MainMenu extends AppCompatActivity {

    // Lists to hold products info for recycler view
    List<String> product_names;
    List<Float> product_prices;
    List<Integer> product_quantities;
    List<Integer> product_ids;

    RecyclerView recycler;
    ProductsRecyclerAdapter pro_adapter;
    EcommerceDatabaseHelper db;
    String[] customer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        // Retriving Customer Details
        customer = getIntent().getStringArrayExtra("customer");

        recycler = (RecyclerView)findViewById(R.id.products_search_recycler);
        db = new EcommerceDatabaseHelper(this);

        // Searching by text
        EditText search_bar = (EditText)findViewById(R.id.search_bar);
        Button search_btn = (Button)findViewById(R.id.search_button);
        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Initializing the lists
                initializeLists();

                // Filling lists from database
                String search_text = search_bar.getText().toString();
                Cursor cursor = db.searchProducts(search_text);
                while (!cursor.isAfterLast())
                {
                    product_names.add(cursor.getString(0));
                    product_prices.add(cursor.getFloat(1));
                    product_quantities.add(cursor.getInt(2));
                    product_ids.add(cursor.getInt(3));
                    cursor.moveToNext();
                }

                // Passing Lists into recycler view adapter
                fillingAdapter(pro_adapter);
            }
        });

        // Searching by voice
        Button voice_btn = (Button)findViewById(R.id.voice_button);
        voice_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                startActivityForResult(intent,1);
            }
        });

        // Searching by Barcode
        Button barcode_btn = (Button)findViewById(R.id.barcode_button);
        barcode_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Scanning Barcode
                IntentIntegrator integrator = new IntentIntegrator(MainMenu.this);
                integrator.setCaptureActivity(Barcode.class);
                integrator.setOrientationLocked(false);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.PRODUCT_CODE_TYPES);
                integrator.setPrompt("Scanning");
                integrator.initiateScan();
            }
        });
    }

    // Initializing Lists
    private void initializeLists()
    {
        product_names = new ArrayList<>();
        product_prices = new ArrayList<>();
        product_quantities = new ArrayList<>();
        product_ids = new ArrayList<>();
    }

    // Passing Lists into recycler view adapter
    private void fillingAdapter(ProductsRecyclerAdapter pro_adapter)
    {
        pro_adapter = new ProductsRecyclerAdapter(MainMenu.this,product_names,product_prices,product_quantities,product_ids,db);
        recycler.setAdapter(pro_adapter);
        recycler.setLayoutManager(new LinearLayoutManager(MainMenu.this));
    }

    // Getting Voice and Scan Results
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // Initializing lists;
        initializeLists();

        // 1 for camera else barcode
        if(requestCode == 1)
        {
            ArrayList<String> text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

            // Filling lists from database
            Cursor cursor = db.searchProducts(text.get(0));
            while (!cursor.isAfterLast()) {
                product_names.add(cursor.getString(0));
                product_prices.add(cursor.getFloat(1));
                product_quantities.add(cursor.getInt(2));
                product_ids.add(cursor.getInt(3));
                cursor.moveToNext();
            }

            // Filling adapter for recycler view
            fillingAdapter(pro_adapter);
        }

        // barcode
        else {
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (result != null) {
                if (result.getContents() != null) {

                    // Adjusting barcode with leading zeroes
                    String barcode = result.getContents();
                    for (int i = 0; i < 13 - barcode.length(); i++) {
                        barcode = '0' + barcode;
                    }

                    // Filling lists from database
                    Toast.makeText(getApplicationContext(), barcode, Toast.LENGTH_LONG).show();
                    Cursor cursor = db.searchProductsBarcode(barcode);
                    while (!cursor.isAfterLast()) {
                        product_names.add(cursor.getString(0));
                        product_prices.add(cursor.getFloat(1));
                        product_quantities.add(cursor.getInt(2));
                        product_ids.add(cursor.getInt(3));
                        cursor.moveToNext();
                    }

                    // Filling adapter for recycler view
                    fillingAdapter(pro_adapter);
                }
            } else
                super.onActivityResult(requestCode, resultCode, data);
        }
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
            Intent intent = new Intent(MainMenu.this, Categories.class);
            intent.putExtra("customer",customer);
            startActivity(intent);
            return true;
        }
        else if(id == R.id.cart_menu_item)
        {
            Intent intent = new Intent(MainMenu.this, Cart.class);
            intent.putExtra("customer",customer);
            startActivity(intent);
            return true;
        }
        return false;
    }

}