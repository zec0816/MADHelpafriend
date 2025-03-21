package com.example.helpafriend;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class VolunteerMainPage extends BaseActivity {

    private TextView usernameTextView;
    private TextView totalPointsTextView;
    private String username;
    private RecyclerView helpHistoryRecyclerView;
    private HelpHistoryAdapter helpHistoryAdapter;
    private List<HelpHistory> helpHistoryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteer_main_page);

        usernameTextView = findViewById(R.id.usernameTextView);
        totalPointsTextView = findViewById(R.id.pointsTextView);
        helpHistoryRecyclerView = findViewById(R.id.help_history_recyclerview);

        helpHistoryList = new ArrayList<>();

        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        username = sharedPreferences.getString("username", null);

        if (username == null || username.isEmpty()) {
            startActivity(new Intent(VolunteerMainPage.this, Login.class));
            finish();
        }

        usernameTextView.setText("Welcome, " + username);

        fetchNumHelped(username);
        fetchHelpHistory(username);

        setupBottomNavigation();
    }

    private void fetchHelpHistory(String username) {
        String url = Db_Contract.urlFetchHelpHistory + "?username=" + username;

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        Log.d("VolunteerMainPage", "Helped OKU API Response: " + response.toString());

                        // Clear the previous list to avoid adding duplicate data
                        helpHistoryList.clear();

                        // Get the total number of people helped
                        int totalHelped = response.getInt("total_helped");

                        TextView totalHelpedTextView = findViewById(R.id.totalHelpedTextView);
                        totalHelpedTextView.setText("You helped " + totalHelped + " people");

                        JSONArray helpedList = response.getJSONArray("helped_list");

                        if (helpedList.length() == 0) {
                            Toast.makeText(this, "No help history found", Toast.LENGTH_SHORT).show();
                        } else {
                            // Iterate through the response and add OKU names and dates to the list
                            for (int i = 0; i < helpedList.length(); i++) {
                                JSONObject okuData = helpedList.getJSONObject(i);

                                String okuName = okuData.getString("oku_name");
                                String helpDate = okuData.getString("help_date");

                                HelpHistory history = new HelpHistory(okuName, helpDate);
                                helpHistoryList.add(history);
                            }

                            if (helpHistoryAdapter == null) {
                                helpHistoryAdapter = new HelpHistoryAdapter(helpHistoryList);
                                helpHistoryRecyclerView.setLayoutManager(new LinearLayoutManager(this));
                                helpHistoryRecyclerView.setAdapter(helpHistoryAdapter);
                            } else {
                                helpHistoryAdapter.notifyDataSetChanged();
                            }
                        }
                    } catch (Exception e) {
                        Log.e("VolunteerMainPage", "Error parsing OKU data", e);
                        Toast.makeText(this, "Failed to fetch OKU data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("VolunteerMainPage", "Failed to fetch help history", error);
                    Toast.makeText(this, "Failed to fetch help history", Toast.LENGTH_SHORT).show();
                }
        );

        Volley.newRequestQueue(this).add(request);
    }


    private void fetchNumHelped(String username) {
        String url = Db_Contract.urlFetchUserPoints + "?username=" + username;

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        if (response.has("num_helped")) {
                            int numHelped = response.getInt("num_helped");

                            // Calculate total points (e.g., 50 points per person helped)
                            int totalPoints = numHelped * 50;
                            totalPointsTextView.setText("Total Points: " + totalPoints);
                        } else {
                            Toast.makeText(this, "No leaderboard data found", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.e("VolunteerMainPage", "JSON Parsing Error", e);
                    }
                },
                error -> Log.e("VolunteerMainPage", "Failed to fetch total points", error)
        );

        Volley.newRequestQueue(this).add(request);
    }

    @Override
    protected int getSelectedNavItemId(String role) {
        if ("volunteer".equals(role)) {
            return R.id.volunteer_home;
        } else {
            return R.id.nav_home;
        }
    }
}