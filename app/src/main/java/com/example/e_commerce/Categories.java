package com.example.e_commerce;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.*;

public class Categories extends AppCompatActivity {

    List<String> cats_list;
    int images[] = {R.drawable.ic_electronics,R.drawable.ic_fashion,R.drawable.ic_health_and_beauty,R.drawable.ic_kitchen_utensils,
    R.drawable.ic_mobiles_and_tablets,R.drawable.ic_pc,R.drawable.ic_toys};

    String[] customer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        // Retriving Customer Details
        customer = getIntent().getStringArrayExtra("customer");

        // Filling cats_list from database with categories names
        EcommerceDatabaseHelper db = new EcommerceDatabaseHelper(this);
        Cursor cursor = db.fetchCategories();
        cats_list = new ArrayList<>();
        // Dictionary for category name to category id
        Dictionary categories = new Hashtable();
        while (!cursor.isAfterLast())
        {
            cats_list.add(cursor.getString(0));
            categories.put(cursor.getString(0),cursor.getInt(1));
            cursor.moveToNext();
        }
        Collections.sort(cats_list);

        RecyclerView cat_recycler = (RecyclerView)findViewById(R.id.categories_recycler);
        CategoriesRecyclerAdapter cat_adapter = new CategoriesRecyclerAdapter(this,cats_list,images,categories,customer);
        cat_recycler.setAdapter(cat_adapter);
        cat_recycler.setLayoutManager(new LinearLayoutManager(this));
    }
}