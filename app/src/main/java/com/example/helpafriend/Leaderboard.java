package com.example.helpafriend;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Leaderboard extends BaseActivity {

    private RecyclerView leaderboardRecyclerView;
    private LeaderboardAdapter leaderboardAdapter;
    private ArrayList<LeaderboardEntry> leaderboardEntries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        setupBottomNavigation();

        // Initialize RecyclerView and list
        leaderboardRecyclerView = findViewById(R.id.leaderboard_recycler_view);
        leaderboardRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        leaderboardEntries = new ArrayList<>();
        leaderboardAdapter = new LeaderboardAdapter(leaderboardEntries);
        leaderboardRecyclerView.setAdapter(leaderboardAdapter);

        // Fetch leaderboard data
        fetchLeaderboard();
    }

    private void fetchLeaderboard() {
        String url = Db_Contract.urlGetLeaderboard;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    leaderboardEntries.clear();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject entry = response.getJSONObject(i);
                            String username = entry.getString("username");
                            int numHelped = entry.getInt("num_helped");
                            leaderboardEntries.add(new LeaderboardEntry(username, numHelped));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    leaderboardAdapter.notifyDataSetChanged();
                },
                error -> Toast.makeText(Leaderboard.this, "Failed to fetch leaderboard", Toast.LENGTH_SHORT).show());

        Volley.newRequestQueue(this).add(jsonArrayRequest);
    }

    @Override
    protected int getSelectedNavItemId(String role) {
        if ("volunteer".equals(role)) {
            return R.id.volunteer_leaderboard; // Default item for volunteer role
        } else {
            return R.id.nav_profile; // Correct ID for Emergency Hotline in OKU role
        }
    }
}
