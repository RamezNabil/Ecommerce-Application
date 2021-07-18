package com.example.e_commerce;

import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    EditText address_text;
    LocationManager location_manager;
    MyLocationListener location_listener;
    Button find_location_btn, confirm_order_btn;
    EcommerceDatabaseHelper db;
    private static DecimalFormat df = new DecimalFormat("0.00");

    String[] customer;
    int[] product_ids;
    int[] product_quantities;
    String[] product_names;
    float[] product_prices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Retriving Customer and Order Details
        customer = getIntent().getStringArrayExtra("customer");
        product_ids = getIntent().getIntArrayExtra("product_ids");
        product_quantities = getIntent().getIntArrayExtra("product_quantities");
        product_names = getIntent().getStringArrayExtra("product_names");
        product_prices = getIntent().getFloatArrayExtra("product_prices");


        // Initializations
        address_text = (EditText)findViewById(R.id.location_text);
        find_location_btn = (Button)findViewById(R.id.find_current_location_btn);
        confirm_order_btn = (Button)findViewById(R.id.confirm_order_btn);
        location_listener = new MyLocationListener(getApplicationContext());
        location_manager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        db = new EcommerceDatabaseHelper(this);

        // Checking GPS Access and updating location
        try {
            location_manager.requestLocationUpdates(LocationManager.GPS_PROVIDER,5000,0,location_listener);
        }
        catch (SecurityException ex)
        {
            Toast.makeText(getApplicationContext(),"You are not allowed to access current location",Toast.LENGTH_SHORT).show();
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        // Calls onMapReady
        mapFragment.getMapAsync(this);

        // Confirm Order Button
        confirm_order_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Getting current date
                Date c = Calendar.getInstance().getTime();
                SimpleDateFormat df2 = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
                String order_date = df2.format(c);

                // Adding Order to Database
                db.makeOrder(order_date,Integer.parseInt(customer[0]),address_text.getText().toString());

                // Getting last order id
                int order_id = db.getLastOrderID();

                // Adding Order Details to Database
                for(int i=0; i<product_ids.length; i++)
                {
                    db.makeOrderDetails(order_id,product_ids[i],product_quantities[i]);
                }

                // Updating cart shared preferences
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = sharedPreferences.edit();
                for(int i=0; i<product_ids.length; i++)
                {
                    editor.putInt(String.valueOf(product_ids[i]),0);
                }
                editor.apply();

                // Confirming Order Message
                Toast.makeText(getApplicationContext(),"Order Confirmed",Toast.LENGTH_LONG).show();

                // Sending Email to Customer with Order Details
                String subject = "Confirmed Order Details";
                String email = customer[2];
                String msg = "Hi " + customer[1] +
                        "\nYour Order is confirmed and is on your way to you"+
                        "\n\nOrder Details:" +
                        "\nAddress: " + address_text.getText().toString() +
                        "\nOrder ID: " + order_id +
                        "\nOrder Date: " + order_date;

                // Getting Order Details in email msg
                float total_price = 0;
                for (int i=0; i<product_ids.length; i++)
                {
                    msg += "\n\nProduct Name: " + product_names[i] +
                            "\n" + "Product Quantity: " + product_quantities[i] +
                            "\n" + "Price: " + df.format(product_prices[i]*product_quantities[i]) + " Egp";
                    total_price += product_prices[i]*product_quantities[i];
                }

                // Adding total price to msg
                msg += "\n\nTotal Price: " + df.format(total_price) + " Egp";

                // Sending Email
                JavaMailAPI javaMailAPI = new JavaMailAPI(MapsActivity.this,email,subject,msg);
                javaMailAPI.execute();

                // Redirecting to Main Menu
                Intent intent = new Intent(MapsActivity.this,MainMenu.class);
                intent.putExtra("customer",customer);
                startActivity(intent);
            }
        });

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Moving Map Camera to Cairo
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(30.04441960,31.235711600),8));

        // Find Location button
        find_location_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Clear initial markers
                mMap.clear();

                // Turns LatLong to string address
                Geocoder coder = new Geocoder(getApplicationContext());
                List<Address> addressList;
                Location loc = null;

                // Getting last known location
                try
                {
                    loc = location_manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                }
                catch (SecurityException ex)
                {
                    Toast.makeText(getApplicationContext(),"Location not detected",Toast.LENGTH_SHORT).show();
                }

                // Location Detected
                if(loc != null)
                {
                    // Getting Latitude and longitude of location
                    LatLng pos = new LatLng(loc.getLatitude(),loc.getLongitude());
                    try
                    {
                        // LatLong -> List of address
                        addressList = coder.getFromLocation(pos.latitude,pos.longitude,1);
                        if(!addressList.isEmpty())
                        {
                            String address = "";
                            // Gets lines of the first possible address from list in address string
                            for(int i=0; i<=addressList.get(0).getMaxAddressLineIndex(); i++)
                            {
                                address += addressList.get(0).getAddressLine(i) + ", ";
                            }
                            // Add draggable marker to location
                            mMap.addMarker(new MarkerOptions().position(pos).title("My location")
                                    .snippet(address)).setDraggable(true);
                            // put address in edittext
                            address_text.setText(address);
                        }
                    }
                    catch (IOException ex)
                    {
                        mMap.addMarker(new MarkerOptions().position(pos).title("My Location"));
                    }
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos,15));
                }
                // Location not detected yet
                else
                {
                    Toast.makeText(getApplicationContext(),"Please wait until your location is detected",Toast.LENGTH_SHORT).show();
                }
                
            }
        });

        // Add location to edittext on marker dragging
        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                // Turns LatLong to string address
                Geocoder coder = new Geocoder(getApplicationContext());
                List<Address> addressList;
                try
                {
                    // LatLong of marker -> List of address
                    addressList = coder.getFromLocation(marker.getPosition().latitude,
                            marker.getPosition().longitude,1);

                    // address found
                    if(!addressList.isEmpty())
                    {
                        String address = "";
                        // Gets lines of the first possible address from list in address string
                        for(int i=0; i<=addressList.get(0).getMaxAddressLineIndex(); i++)
                        {
                            address += addressList.get(0).getAddressLine(i) + ", ";
                        }
                        // put address in edittext
                        address_text.setText(address);
                    }
                    // No address found
                    else
                    {
                        Toast.makeText(getApplicationContext(),"No address for this location",Toast.LENGTH_SHORT).show();
                        address_text.getText().clear();
                    }
                }
                catch (IOException ex)
                {
                    Toast.makeText(getApplicationContext(),"Can't get the address",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}