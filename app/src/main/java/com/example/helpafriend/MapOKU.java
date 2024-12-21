package com.example.helpafriend;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MapOKU extends AppCompatActivity {

    SupportMapFragment supportMapFragment;
    FusedLocationProviderClient fusedLocationProviderClient;
    private GoogleMap googleMap;
    private Marker currentMarker;
    private Button acceptedRequestsButton;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final LatLng DEFAULT_LOCATION = new LatLng(3.1219267, 101.6569933);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_oku);

        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        acceptedRequestsButton = findViewById(R.id.accepted_requests_button);

        // Set up button click listener
        acceptedRequestsButton.setOnClickListener(view -> showAcceptedRequests());

        // Check if the permission is granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();  // Permission is already granted
        } else {
            // Request permission if not granted
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @SuppressLint("MissingPermission")
    public void getCurrentLocation() {
        supportMapFragment.getMapAsync(map -> {
            googleMap = map;
            googleMap.setMyLocationEnabled(true);

            currentMarker = googleMap.addMarker(new MarkerOptions().position(DEFAULT_LOCATION).title("Current Location"));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, 15));

            googleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                @Override
                public void onMarkerDragStart(Marker marker) {}

                @Override
                public void onMarkerDrag(Marker marker) {}

                @Override
                public void onMarkerDragEnd(Marker marker) {
                    currentMarker = marker;
                    showConfirmationDialog(currentMarker.getPosition());
                }
            });

            googleMap.setOnMapClickListener(latLng -> {
                if (currentMarker != null) {
                    currentMarker.remove();
                }
                currentMarker = googleMap.addMarker(new MarkerOptions().position(latLng).draggable(true).title("Selected Location"));
                showConfirmationDialog(latLng);
            });

            googleMap.setOnMarkerClickListener(marker -> {
                if (marker.equals(currentMarker)) {
                    showConfirmationDialog(marker.getPosition());
                    return true;
                }
                return false;
            });
        });
    }

    private void showConfirmationDialog(LatLng selectedLocation) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to choose this location?")
                .setPositiveButton("Yes", (dialog, id) -> {
                    storeLocationInDatabase(selectedLocation.latitude, selectedLocation.longitude);
                    Toast.makeText(MapOKU.this, "Waiting for volunteer...", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("No", (dialog, id) -> dialog.dismiss())
                .create().show();
    }

    private void storeLocationInDatabase(double latitude, double longitude) {
        String url = Db_Contract.urlStoreLocation;

        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", null);

        if (username != null) {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    response -> {
                        Log.d("LocationResponse", response);
                        Toast.makeText(MapOKU.this, "Location saved successfully!", Toast.LENGTH_SHORT).show();
                    },
                    error -> {
                        Log.e("LocationError", error.toString());
                        Toast.makeText(MapOKU.this, "Failed to save location", Toast.LENGTH_SHORT).show();
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("username", username);
                    params.put("latitude", String.valueOf(latitude));
                    params.put("longitude", String.valueOf(longitude));
                    return params;
                }
            };

            Volley.newRequestQueue(this).add(stringRequest);
        } else {
            Toast.makeText(MapOKU.this, "Username not found", Toast.LENGTH_SHORT).show();
        }
    }

    private void showAcceptedRequests() {
        String url = Db_Contract.urlGetAcceptedRequests;

        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", null);

        if (username == null) {
            Toast.makeText(this, "Username not found", Toast.LENGTH_SHORT).show();
            return;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        ArrayList<Map<String, String>> acceptedRequests = new ArrayList<>();

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject request = jsonArray.getJSONObject(i);
                            Map<String, String> requestMap = new HashMap<>();
                            requestMap.put("latitude", String.valueOf(request.getDouble("latitude")));
                            requestMap.put("longitude", String.valueOf(request.getDouble("longitude")));
                            acceptedRequests.add(requestMap);
                        }

                        if (acceptedRequests.isEmpty()) {
                            Toast.makeText(this, "No accepted requests found", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setTitle("Accepted Requests");

                        ListView listView = new ListView(this);
                        CustomAdapter adapter = new CustomAdapter(acceptedRequests);
                        listView.setAdapter(adapter);

                        builder.setView(listView);
                        builder.setNegativeButton("Close", (dialog, which) -> dialog.dismiss());
                        builder.create().show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error parsing response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("VolleyError", error.toString());
                    Toast.makeText(this, "Failed to fetch accepted requests", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(stringRequest);
    }

    class CustomAdapter extends ArrayAdapter<Map<String, String>> {
        CustomAdapter(ArrayList<Map<String, String>> data) {
            super(MapOKU.this, R.layout.list_item_request, data);
        }

        @NonNull
        @Override
        public View getView(int position, @NonNull View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.list_item_request, parent, false);
            }

            Map<String, String> request = getItem(position);

            TextView tvLatitude = convertView.findViewById(R.id.tv_latitude);
            TextView tvLongitude = convertView.findViewById(R.id.tv_longitude);

            tvLatitude.setText("Latitude: " + request.get("latitude"));
            tvLongitude.setText("Longitude: " + request.get("longitude"));

            return convertView;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                Toast.makeText(this, "Permission denied to access location", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
