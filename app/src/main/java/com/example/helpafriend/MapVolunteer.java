package com.example.helpafriend;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;

public class MapVolunteer extends BaseActivity {

    private SupportMapFragment supportMapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_volunteer);

        // Initialize SupportMapFragment
        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);

        // Get the locations from the server (you'll need to replace this with your own network request)
        getOKULocations();
//        setupBottomNavigation();
    }

    private void getOKULocations() {
        String url = Db_Contract.urlGetNearbyOKU;  // Assuming this is your correct URL

        // Sending a GET request to fetch OKU locations
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url,
                response -> {
                    Log.d("MapVolunteer", "Raw Response: " + response.toString());

                    // Check if the response has data
                    if (response.length() > 0) {
                        supportMapFragment.getMapAsync(googleMap -> {
                            // Set the volunteer location (hardcoded)
                            LatLng volunteerLocation = new LatLng(3.2219267, 101.6569933);
                            googleMap.addMarker(new MarkerOptions().position(volunteerLocation).title("Volunteer Location"));

                            // Iterate over the response and add markers for each OKU location
                            for (int i = 0; i < response.length(); i++) {
                                double latitude = 0;
                                try {
                                    latitude = Double.parseDouble(response.getJSONObject(i).getString("latitude"));
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
                                double longitude = 0;
                                try {
                                    longitude = Double.parseDouble(response.getJSONObject(i).getString("longitude"));
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }

                                LatLng okuLocation = new LatLng(latitude, longitude);
                                googleMap.addMarker(new MarkerOptions().position(okuLocation).title("OKU Location"));
                            }

                            // Optionally move the camera to the first OKU location (if available)
                            if (response.length() > 0) {
                                LatLng firstOKULocation = null;
                                try {
                                    firstOKULocation = new LatLng(
                                            Double.parseDouble(response.getJSONObject(0).getString("latitude")),
                                            Double.parseDouble(response.getJSONObject(0).getString("longitude"))
                                    );
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
                                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(firstOKULocation, 15));
                            }
                        });
                    } else {
                        Toast.makeText(MapVolunteer.this, "No OKU locations found.", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("MapVolunteer", "Error fetching data: " + error.getMessage());
                    Toast.makeText(MapVolunteer.this, "Failed to fetch OKU locations", Toast.LENGTH_SHORT).show();
                });

        // Add the request to the request queue
        Volley.newRequestQueue(this).add(jsonArrayRequest);
    }
    @Override
    protected int getSelectedNavItemId(String role) {
        if ("volunteer".equals(role)) {
            return R.id.volunteer_activity; // Default item for volunteer role
        } else {
            return R.id.nav_profile; // Correct ID for forum in OKU role
        }
    }
}
