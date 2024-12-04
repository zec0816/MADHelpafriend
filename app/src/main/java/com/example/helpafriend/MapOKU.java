package com.example.helpafriend;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.Map;

public class MapOKU extends AppCompatActivity {

    SupportMapFragment supportMapFragment;
    FusedLocationProviderClient fusedLocationProviderClient;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_oku);

        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Check if the permission is granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();  // Permission is already granted
        } else {
            // Request permission if not granted
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    // Get the current location of OKU
    @SuppressLint("MissingPermission")
    public void getCurrentLocation() {
        double latitude = 3.1219267;
        double longitude = 101.6569933;
        LatLng okuLocation = new LatLng(latitude, longitude);

        // Send location to the backend
        storeLocationInDatabase(latitude, longitude);

        // Show the location on the map
        supportMapFragment.getMapAsync(googleMap -> {
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(okuLocation)
                    .title("Your Location");
            googleMap.addMarker(markerOptions);
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(okuLocation, 15));
        });
    }


    // Send the current OKU location to the backend for storage
    private void storeLocationInDatabase(double latitude, double longitude) {
        String url = Db_Contract.urlStoreLocation; // Ensure this URL is correct in Db_Contract.java

        // Replace with actual user ID from your app's login session
        String userId = "1"; // Temporarily set to 1, replace with actual user ID

        // Create a POST request to send the data to the server
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    // Log the response to check if it was successful
                    Log.d("LocationResponse", response);
                    Toast.makeText(MapOKU.this, "Location saved successfully!", Toast.LENGTH_SHORT).show();
                },
                error -> {
                    // Log and display errors
                    Log.e("LocationError", error.toString());
                    Toast.makeText(MapOKU.this, "Failed to save location", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                // Send user ID, latitude, and longitude as parameters
                Map<String, String> params = new HashMap<>();
                params.put("id_user", userId); // Pass the actual user ID here
                params.put("latitude", String.valueOf(latitude));
                params.put("longitude", String.valueOf(longitude));
                return params;
            }
        };

        // Add the request to the queue to execute it
        Volley.newRequestQueue(this).add(stringRequest);
    }

    // Handle the result of the permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, fetch the current location
                getCurrentLocation();
            } else {
                // Permission denied, show a message to the user
                Toast.makeText(this, "Permission denied to access location", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
