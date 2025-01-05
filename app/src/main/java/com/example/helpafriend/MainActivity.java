package com.example.helpafriend;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends BaseActivity {

    private RecyclerView recentPostsRecyclerView;
    private TextView welcomeText; // Welcome TextView
    private TextView emptyStateText;
    private RecentPostsAdapter postAdapter;
    private List<Post> postList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI components
        recentPostsRecyclerView = findViewById(R.id.recentPostsRecyclerView);
        welcomeText = findViewById(R.id.welcomeText); // Get Welcome TextView
        emptyStateText = findViewById(R.id.emptyStateText);

        // Set up RecyclerView
        recentPostsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        postList = new ArrayList<>();
        postAdapter = new RecentPostsAdapter(postList);
        recentPostsRecyclerView.setAdapter(postAdapter);

        // Retrieve username from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "User"); // Default is "User" if not found

        // Use the string resource for the welcome message
        String welcomeMessage = getString(R.string.welcome) + ", " + username;
        welcomeText.setText(welcomeMessage);

        // Fetch and display the two most recent posts
        fetchRecentPosts();

        // Set up the bottom navigation
        setupBottomNavigation();
        applySelectedLanguage();
    }

    private void applySelectedLanguage() {
        String savedLocale = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                .getString("appLocale", "en");
        Locale locale = new Locale(savedLocale);
        Locale.setDefault(locale);

        Configuration config = new Configuration();
        config.setLocale(locale);
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
    }

    private void fetchRecentPosts() {
        String url = Db_Contract.urlGetPost;

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        postList.clear();
                        for (int i = 0; i < Math.min(2, response.length()); i++) {
                            JSONObject post = response.getJSONObject(i);
                            String username = post.optString("username", "Unknown User");
                            String title = post.optString("title", "No Title");
                            String content = post.optString("content", "No Content");
                            String createdAt = post.optString("created_at", "Unknown Date");
                            postList.add(new Post(title, content, createdAt, username));
                        }
                        updateUI();
                    } catch (JSONException e) {
                        Log.e("MainActivity", "JSON Parsing Error", e);
                    }
                },
                error -> Log.e("MainActivity", "Volley Error", error)
        );

        Volley.newRequestQueue(this).add(request);
    }

    private void updateUI() {
        if (postList.isEmpty()) {
            emptyStateText.setVisibility(View.VISIBLE);
            recentPostsRecyclerView.setVisibility(View.GONE);
        } else {
            emptyStateText.setVisibility(View.GONE);
            recentPostsRecyclerView.setVisibility(View.VISIBLE);
            postAdapter.notifyDataSetChanged();
        }
    }


    @Override
    protected int getSelectedNavItemId(String role) {
        if ("volunteer".equals(role)) {
            return R.id.volunteer_home; // Default item for volunteer role
        } else {
            return R.id.nav_home; // Correct ID for forum in OKU role
        }
    }

}
