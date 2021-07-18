package com.example.e_commerce;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class EcommerceDatabaseHelper extends SQLiteOpenHelper
{

    private static String dbname = "ecommerceDB";
    SQLiteDatabase ecommerceDB;
    public EcommerceDatabaseHelper(Context context)
    {
        super(context, dbname,null,5);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        // Customers Table
        db.execSQL("create table Customers(" +
                "CustID integer primary key," +
                "CustName text not null," +
                "Email text not null unique," +
                "Password text not null," +
                "Gender text not null," +
                "Birthdate text not null," +
                "Job text not null);");

        // Orders Table
        db.execSQL("create table Orders(" +
                "OrdID integer primary key," +
                "OrdDate text not null," +
                "CustID integer not null," +
                "Address text not null," +
                "Foreign key(CustID) references Customers(CustID));");

        // Categories Table
        db.execSQL("create table Categories(" +
                "CatID integer primary key," +
                "CatName text unique not null);");

        // Products Table
        db.execSQL("create table Products(" +
                "ProID integer primary key," +
                "ProName text not null," +
                "Price real not null," +
                "Quantity integer not null," +
                "ProBarcode text unique not null," +
                "CatID integer not null," +
                "Foreign key(CatID) references Categories(CatID));");

        // Order Details Table
        db.execSQL("create table OrderDetails(" +
                "OrdID integer," +
                "ProID integer," +
                "Quantity integer not null," +
                "Primary key(OrdId, ProID)," +
                "Foreign key(OrdID) references Orders(OrdID)," +
                "Foreign key(ProID) references Products(ProID));");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1)
    {
        db.execSQL("drop table if exists Customers");
        db.execSQL("drop table if exists Orders");
        db.execSQL("drop table if exists Categories");
        db.execSQL("drop table if exists Products");
        db.execSQL("drop table if exists OrderDetails");
        onCreate(db);
    }

    // Create new customer account
    public boolean createAccount(String name, String email, String pass, String gender, String birthdate, String job)
    {
        // Getting row
        ContentValues row = new ContentValues();
        row.put("CustName",name);
        row.put("Email",email);
        row.put("Password",pass);
        row.put("Gender",gender);
        row.put("Birthdate",birthdate);
        row.put("Job",job);

        // Inserting into database
        ecommerceDB = getWritableDatabase();
        long res = ecommerceDB.insert("Customers",null,row);
        ecommerceDB.close();

        // Returning true if inserted successfully
        if(res == -1)
            return false;
        else
            return true;
    }

    // Sign in
    public Cursor signIn(String email, String pass)
    {
        ecommerceDB = getReadableDatabase();
        String[] args = {email,pass};
        Cursor cursor = ecommerceDB.rawQuery("Select * from Customers where Email = ? and Password = ?",args);
        if(cursor!=null)
            cursor.moveToFirst();
        ecommerceDB.close();
        return cursor;
    }

    // Forget password
    public String forgetPass(String email)
    {
        String pass = "";
        ecommerceDB = getReadableDatabase();
        String[] args = {email};
        Cursor cursor = ecommerceDB.rawQuery("Select Password from Customers where Email = ?",args);
        if(cursor!=null)
            cursor.moveToFirst();
        if(!cursor.isAfterLast())
            pass = cursor.getString(0);
        ecommerceDB.close();
        return pass;
    }

    // Insert Category
    public void insertCat(String name)
    {
        // Getting row
        ContentValues row = new ContentValues();
        row.put("CatName",name);

        // Inserting into database
        ecommerceDB = getWritableDatabase();
        ecommerceDB.insert("Categories",null,row);
        ecommerceDB.close();
    }

    // Select all categories
    public Cursor fetchCategories() {
        ecommerceDB = getReadableDatabase();
        String[] row = {"CatName","CatID"};
        Cursor cursor = ecommerceDB.query("Categories", row, null, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        ecommerceDB.close();
        return cursor;
    }

    // Insert Category
    public void insertProduct(String name, float price, int quantity, String barcode, int catID)
    {
        // Getting row
        ContentValues row = new ContentValues();
        row.put("ProName",name);
        row.put("Price",price);
        row.put("Quantity",quantity);
        row.put("ProBarCode",barcode);
        row.put("CatID",catID);

        // Inserting into database
        ecommerceDB = getWritableDatabase();
        ecommerceDB.insert("Products",null,row);
        ecommerceDB.close();
    }

    // Select all products for a category
    public Cursor fetchProducts(int catID)
    {
        ecommerceDB = getReadableDatabase();
        String[] args = {String.valueOf(catID)};
        Cursor cursor = ecommerceDB.rawQuery("Select ProName,Price,Quantity,ProID from Products where CatID = ?",args);
        if(cursor!=null)
            cursor.moveToFirst();
        ecommerceDB.close();
        return cursor;
    }

    // Select products from search
    public Cursor searchProducts(String name)
    {
        ecommerceDB = getReadableDatabase();
        String[] args = {'%'+name+'%'};
        Cursor cursor = ecommerceDB.rawQuery("Select ProName,Price,Quantity,ProID from Products where ProName like ?",args);
        if(cursor!=null)
            cursor.moveToFirst();
        ecommerceDB.close();
        return cursor;
    }

    // Select product by barcode
    public Cursor searchProductsBarcode(String barcode)
    {
        ecommerceDB = getReadableDatabase();
        String[] args = {barcode};
        Cursor cursor = ecommerceDB.rawQuery("Select ProName,Price,Quantity,ProID from Products where ProBarcode = ?",args);
        if(cursor!=null)
            cursor.moveToFirst();
        ecommerceDB.close();
        return cursor;
    }

    // Select product by id
    public Cursor searchProductsID(int id)
    {
        ecommerceDB = getReadableDatabase();
        String[] args = {String.valueOf(id)};
        Cursor cursor = ecommerceDB.rawQuery("Select ProName,Price,Quantity,ProID from Products where ProID = ?",args);
        if(cursor!=null)
            cursor.moveToFirst();
        ecommerceDB.close();
        return cursor;
    }

    // Edit Quantity of products
    public void editProductQuantity(int id, int quantity)
    {
        ecommerceDB = getWritableDatabase();
        ecommerceDB.execSQL("Update Products set Quantity = ? where ProID = ?",
                new String[]{String.valueOf(quantity),String.valueOf(id)});
        ecommerceDB.close();
    }

    // Select MAX product id
    public int getMaxProductID()
    {
        ecommerceDB = getReadableDatabase();
        Cursor cursor = ecommerceDB.rawQuery("Select MAX(ProID) from products",null);
        if(cursor!=null)
            cursor.moveToFirst();
        int id = cursor.getInt(0);
        ecommerceDB.close();
        return id;
    }

    // Make Order
    public void makeOrder(String ord_date, int cust_ID, String address)
    {
        // Getting row
        ContentValues row = new ContentValues();
        row.put("OrdDate",ord_date);
        row.put("CustID",cust_ID);
        row.put("Address",address);

        // Inserting into database
        ecommerceDB = getWritableDatabase();
        ecommerceDB.insert("Orders",null,row);
        ecommerceDB.close();
    }

    // Retriving last order id
    public int getLastOrderID()
    {
        ecommerceDB = getReadableDatabase();
        Cursor cursor = ecommerceDB.rawQuery("Select MAX(OrdID) from Orders",null);
        if(cursor!=null)
            cursor.moveToFirst();
        int id = cursor.getInt(0);
        ecommerceDB.close();
        return id;
    }

    // Make Order Details
    public void makeOrderDetails(int ord_id, int pro_id, int quantity)
    {
        // Getting row
        ContentValues row = new ContentValues();
        row.put("OrdID",ord_id);
        row.put("ProID",pro_id);
        row.put("Quantity",quantity);

        // Inserting into database
        ecommerceDB = getWritableDatabase();
        ecommerceDB.insert("OrderDetails",null,row);
        ecommerceDB.close();
    }
}
