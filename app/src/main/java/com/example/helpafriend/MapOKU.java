package com.example.helpafriend;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.Map;

public class MapOKU extends AppCompatActivity {

    SupportMapFragment supportMapFragment;
    FusedLocationProviderClient fusedLocationProviderClient;
    private GoogleMap googleMap;
    private Marker currentMarker;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final LatLng DEFAULT_LOCATION = new LatLng(3.1219267, 101.6569933);

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
        supportMapFragment.getMapAsync(map -> {
            googleMap = map;
            googleMap.setMyLocationEnabled(true);

            currentMarker = googleMap.addMarker(new MarkerOptions().position(DEFAULT_LOCATION).title("Current Location"));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, 15));

            // Set a listener to detect when the user moves the marker
            googleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                @Override
                public void onMarkerDragStart(Marker marker) {}

                @Override
                public void onMarkerDrag(Marker marker) {}

                @Override
                public void onMarkerDragEnd(Marker marker) {
                    currentMarker = marker;  // Update the current marker with the new position
                    showConfirmationDialog(currentMarker.getPosition()); // Show the confirmation dialog
                }
            });

            // Set an onMapClickListener to allow the user to select a new location by tapping on the map
            googleMap.setOnMapClickListener(latLng -> {
                if (currentMarker != null) {
                    currentMarker.remove(); // Remove the old marker
                }
                currentMarker = googleMap.addMarker(new MarkerOptions().position(latLng).draggable(true).title("Selected Location"));
                showConfirmationDialog(latLng); // Show the confirmation dialog
            });

            googleMap.setOnMarkerClickListener(marker -> {
                if (marker.equals(currentMarker)) {
                    showConfirmationDialog(marker.getPosition());  // Show the dialog
                    return true;  // Returning true prevents the default behavior of the marker click (i.e., camera zoom)
                }
                return false;
            });
        });
    }

    // Show a dialog to confirm the selected location
    private void showConfirmationDialog(LatLng selectedLocation) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to choose this location?")
                .setPositiveButton("Yes", (dialog, id) -> {
                    storeLocationInDatabase(selectedLocation.latitude, selectedLocation.longitude);
                    Toast.makeText(MapOKU.this, "Waiting for volunteer...", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("No", (dialog, id) -> {
                    dialog.dismiss(); // Dismiss the dialog if "No" is clicked
                })
                .create().show(); // Show the dialog
    }

    // Send the current selected location to the backend for storage
    // Inside the MapOKU.java class, modify the storeLocationInDatabase method

    private void storeLocationInDatabase(double latitude, double longitude) {
        String url = Db_Contract.urlStoreLocation; // Ensure this URL is correct in Db_Contract.java

        // Retrieve the saved username from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", null); // Get the username

        if (username != null) {
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
                    // Send username, latitude, and longitude as parameters
                    Map<String, String> params = new HashMap<>();
                    params.put("username", username); // Send the username
                    params.put("latitude", String.valueOf(latitude));
                    params.put("longitude", String.valueOf(longitude));
                    return params;
                }
            };

            // Add the request to the queue to execute it
            Volley.newRequestQueue(this).add(stringRequest);
        } else {
            Toast.makeText(MapOKU.this, "Username not found", Toast.LENGTH_SHORT).show();
        }
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
