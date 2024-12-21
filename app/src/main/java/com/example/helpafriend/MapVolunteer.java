package com.example.helpafriend;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MapVolunteer extends BaseActivity {

    private SupportMapFragment supportMapFragment;
    private Button showListButton;
    private GoogleMap googleMap;
    private ArrayList<String> okuRequests;
    private ArrayList<JSONObject> okuRequestData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_volunteer);

        // Initialize map fragment, button, and lists
        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);
        showListButton = findViewById(R.id.show_list_button);
        okuRequests = new ArrayList<>();
        okuRequestData = new ArrayList<>();

        // Fetch OKU locations and add markers
        getOKULocations();

        // Set up button to display list of pending requests
        showListButton.setOnClickListener(v -> showPendingRequestsDialog());
    }

    private void getOKULocations() {
        String url = Db_Contract.urlGetNearbyOKU;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url,
                response -> {
                    okuRequests.clear();
                    okuRequestData.clear();

                    if (response.length() > 0) {
                        supportMapFragment.getMapAsync(map -> {
                            googleMap = map;

                            // Add volunteer marker (hardcoded location)
                            LatLng volunteerLocation = new LatLng(3.2219267, 101.6569933);
                            googleMap.addMarker(new MarkerOptions().position(volunteerLocation).title("Volunteer Location"));

                            for (int i = 0; i < response.length(); i++) {
                                try {
                                    JSONObject oku = response.getJSONObject(i);
                                    okuRequestData.add(oku);

                                    // Extract data
                                    double latitude = oku.getDouble("latitude");
                                    double longitude = oku.getDouble("longitude");
                                    int idLocation = oku.getInt("id_location");
                                    okuRequests.add("Request ID: " + idLocation + " (Lat: " + latitude + ", Lng: " + longitude + ")");

                                    // Add marker to the map
                                    LatLng okuLocation = new LatLng(latitude, longitude);
                                    googleMap.addMarker(new MarkerOptions()
                                            .position(okuLocation)
                                            .title("Pending Request ID: " + idLocation));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            // Move camera to first OKU location
                            if (response.length() > 0) {
                                try {
                                    JSONObject firstOKU = response.getJSONObject(0);
                                    LatLng firstLocation = new LatLng(
                                            firstOKU.getDouble("latitude"),
                                            firstOKU.getDouble("longitude")
                                    );
                                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(firstLocation, 15));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                },
                error -> Toast.makeText(MapVolunteer.this, "Failed to fetch OKU locations", Toast.LENGTH_SHORT).show());

        Volley.newRequestQueue(this).add(jsonArrayRequest);
    }

    private void showPendingRequestsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pending Requests");

        RequestListAdapter adapter = new RequestListAdapter(this, okuRequestData);
        ListView listView = new ListView(this);
        listView.setAdapter(adapter);

        builder.setView(listView);
        builder.setPositiveButton("Close", (dialog, id) -> dialog.dismiss());
        builder.create().show();
    }

    private void showConfirmationDialog(JSONObject okuRequest) throws JSONException {
        double latitude = okuRequest.getDouble("latitude");
        double longitude = okuRequest.getDouble("longitude");
        int idLocation = okuRequest.getInt("id_location");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to help this OKU?")
                .setPositiveButton("Yes", (dialog, id) -> {
                    updateRequestStatus(idLocation, "accepted");
                    Intent intent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("google.navigation:q=" + latitude + "," + longitude));
                    startActivity(intent);
                })
                .setNegativeButton("No", (dialog, id) -> dialog.dismiss())
                .create().show();
    }

    private void updateRequestStatus(int idLocation, String status) {
        String url = Db_Contract.urlUpdateStatus;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> Toast.makeText(this, "Request accepted", Toast.LENGTH_SHORT).show(),
                error -> Toast.makeText(this, "Failed to update status", Toast.LENGTH_SHORT).show()) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id_location", String.valueOf(idLocation));
                params.put("status", status);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(stringRequest);
    }
}
