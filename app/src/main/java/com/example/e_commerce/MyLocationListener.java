package com.example.e_commerce;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.widget.Toast;

public class MyLocationListener implements LocationListener {

    private Context context;

    public MyLocationListener(Context context)
    {
        this.context = context;
    }

    @Override
    public void onLocationChanged(Location location) {
//        Toast.makeText(context,location.getLatitude()+", " + location.getLongitude(),
//                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {
        Toast.makeText(context,"GPS Enabled", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderDisabled(String s) {
        Toast.makeText(context,"GPS Disabled",
                Toast.LENGTH_SHORT).show();
    }
}
