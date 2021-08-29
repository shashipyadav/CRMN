package com.example.myapplication.user_interface.upcoming_meeting.view;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.myapplication.R;
import com.example.myapplication.user_interface.upcoming_meeting.controller.JsonUtil;
import com.example.myapplication.user_interface.upcoming_meeting.model.MapModel;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.Locale;


public class MapViewPlacesActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    
    private GoogleMap googleMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location location;
    private int PROXIMITY_RADIUS = 8000000;
    private boolean isFirstTime = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_view);
        setTitle();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }

        if(!isGooglePlayServicesAvailable()){
            Toast.makeText(this,"Google Play Services not available.",Toast.LENGTH_LONG).show();
            finish();
        }else{
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
            
        }
    }
    
    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(this);
        if (result != ConnectionResult.SUCCESS) {
            if (googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(this, result, 0).show();
            }
            return false;
        }
        return true;
    }

    private void setTitle(){
        getSupportActionBar().setTitle(getResources().getString(R.string.map_view));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    
        //Initialize Google Play Services
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                googleMap.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClient();
            googleMap.setMyLocationEnabled(true);
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        mGoogleApiClient.connect();
    }
    
    public void onRestorentFindClick(View view){
        findPlaces("restaurant");
    }
    
    public void onHospitalsFindClick(View view){
        findPlaces("hospital");
    }
    
    public void findPlaces(String placeType){

        Intent intent = getIntent();
        String mapList =  intent.getStringExtra("mapList");

        List<MapModel> stList = (List<MapModel>) JsonUtil.jsonArrayToListObject(mapList,MapModel.class);

        if(location!=null) {
            LatLng india = new LatLng(location.getLatitude(), location.getLongitude());
            googleMap.addMarker(new MarkerOptions().position(india).title("ME"));
           // googleMap.animateCamera(CameraUpdateFactory.zoomTo(12));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(india));

        for (MapModel myModel : stList) {
            setRedius(Double.parseDouble(myModel.getLatitude()), Double.parseDouble(myModel.getLongitude()),JsonUtil.objectToJson(myModel));

        }

        }

    }

    private void setRedius(double lat, double loomg, final String json) {
        try {

          //  googleMap.addMarker(circleOptions);

            LatLng india = new LatLng(lat, loomg);

            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.draggable(false);
            markerOptions.position(india);
            markerOptions.snippet(json);
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker));

            if(googleMap!=null) {
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(india));
               googleMap.animateCamera(CameraUpdateFactory.zoomTo(5));
               googleMap.getUiSettings().setZoomControlsEnabled(true);
                googleMap.addMarker(markerOptions);

                googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        if(marker.getSnippet() != null){
                            openReferralDialog(marker.getSnippet());
                        }
                        return true;

                    }
                });
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void openReferralDialog(String snippet) {
       final MapModel myModel = (MapModel) JsonUtil.jsonToObject(snippet,MapModel.class);

        AlertDialog.Builder builder = new AlertDialog.Builder(MapViewPlacesActivity.this);
        ViewGroup viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(MapViewPlacesActivity.this).inflate(R.layout.dialog_branch_location_info, viewGroup, false);
        builder.setView(dialogView);
        TextView tv_name = (TextView) dialogView.findViewById(R.id.tv_name);
        TextView tv_address = (TextView) dialogView.findViewById(R.id.tv_address);
        TextView phone = (TextView) dialogView.findViewById(R.id.phone);
        TextView tv_eamil = (TextView) dialogView.findViewById(R.id.tv_eamil);
        TextView tv_delaer = (TextView) dialogView.findViewById(R.id.tv_delaer);
        TextView type = (TextView) dialogView.findViewById(R.id.type);
        Button button_dire = (Button) dialogView.findViewById(R.id.button_dire);

       tv_delaer.setText(myModel.getDealer());
      type.setText(myModel.getType());

        tv_name.setText(myModel.getName());
        phone.setText(myModel.getPhone());
        tv_eamil.setText(myModel.getEmail());
        tv_address.setText(myModel.getAddress()+"\n"+myModel.getDistict()+","+myModel.getState()+","+myModel.getCountry());
        button_dire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?q=loc:%f,%f", Double.parseDouble(myModel.getLatitude()),Double.parseDouble(myModel.getLongitude()));
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                startActivity(intent);
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }



    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    
    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            
            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                
                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
                
                
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    
                    // permission was granted. Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        
                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        googleMap.setMyLocationEnabled(true);
                    }
                    
                } else {
                    Toast.makeText(this, "Location Permission has been denied, can not search the places you want.", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }
    
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        startLocationUpdates();

    }
    
    @Override
    public void onConnectionSuspended(int i) {
        
    }
    
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this,"Could not connect google api",Toast.LENGTH_LONG).show();
    }
    
    
    protected void startLocationUpdates() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, (LocationListener) this);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if(location!=null){
            this.location = location;
            if(location!=null && isFirstTime) {
                isFirstTime = false;
                findPlaces("");
            }
/*
            if(!btnHospitalFind.isEnabled()){
                LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));

            }
*/
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
