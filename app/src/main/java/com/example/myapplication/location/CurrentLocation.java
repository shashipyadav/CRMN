package com.example.myapplication.location;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.example.myapplication.user_interface.forms.view.FormFragment;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;
import java.util.Locale;

public class CurrentLocation {

    private FusedLocationProviderClient fusedLocationClient;
    private Context context;
    private int position;
    private String type;

    public CurrentLocation (Context context){
        this.context = context;
    }

    public void getLocation() {

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);

        // check permission
        if (ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            // reuqest for permission
            Toast.makeText(context,"location permission denied. Please grant Location Permission in Simbiosis App Settings",Toast.LENGTH_LONG).show();

        } else {
            // already permission granted

            fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if(location != null) {

                        if(getType().equals("location")){
                            setLocationInField(location);
                        }else{
                            getAddressFromLatLng(location);
                        }


                        Log.e("CurrentLocation","lat and long = " + location.getLatitude() + "---- " + location.getLongitude());
                    }else{
                        Log.e("CurrentLocation","location is null");
                    }

                }
            });
        }
    }

    public void setLocationInField(Location location){
        if(FormFragment.fieldsList != null){
            String latlang = location.getLatitude() + " , "+ location.getLongitude();
            FormFragment.fieldsList.get(position).setValue(latlang);
            FormFragment.adapter.notifyAdapter(position);
        }
    }

    public void setAddressInField(String address){
        if(FormFragment.fieldsList != null){
            FormFragment.fieldsList.get(position).setValue(address);
            FormFragment.adapter.notifyAdapter(position);
        }
    }

    public void getAddressFromLatLng( Location location) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(context, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            String address =  addresses.get(0).getAddressLine(0);

            setAddressInField(address);
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
