package com.example.helpafriend;


import android.content.SharedPreferences;

import android.net.Uri;
import android.os.Bundle;

import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

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

    private TextToSpeech tts;

    private SharedPreferences sharedPreferences;
    private ImageView profileImageView;
    private String username;


    private static final String TAG = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        username = sharedPreferences.getString("username", null);

        // Initialize UI components
        profileImageView = findViewById(R.id.profileImage);
        recentPostsRecyclerView = findViewById(R.id.recentPostsRecyclerView);
        welcomeText = findViewById(R.id.welcomeText); // Get Welcome TextView
        emptyStateText = findViewById(R.id.emptyStateText);

        // Load the profile image
        loadProfileImage();

        // Set up RecyclerView
        recentPostsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        postList = new ArrayList<>();
        postAdapter = new RecentPostsAdapter(postList);
        recentPostsRecyclerView.setAdapter(postAdapter);
        Button readAloudButton = findViewById(R.id.readAloudButton);

        // Initialize Text-to-Speech
        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int result = tts.setLanguage(Locale.US);
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TTS", "Language not supported or missing data.");
                }
            } else {
                Log.e("TTS", "Initialization failed.");
            }
        });

        // Set up Read Aloud Button click event
        readAloudButton.setOnClickListener(view -> readAloudForumContent());

        // Retrieve username from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "User"); // Default is "User" if not found

        // Display the username
        welcomeText.setText("WELCOME, " + username);

        // Fetch and display the two most recent posts
        fetchRecentPosts();

        // Set up the bottom navigation
        setupBottomNavigation();
    }

    private void loadProfileImage() {
        // Retrieve the URI from SharedPreferences
        String profileImageUri = sharedPreferences.getString("profile_image_uri_" + username, null);

        if (profileImageUri != null) {
            try {
                Uri imageUri = Uri.parse(profileImageUri);
                profileImageView.setImageURI(imageUri);
            } catch (Exception e) {
                e.printStackTrace();
                setDefaultProfileImage();
            }
        } else {
            setDefaultProfileImage();
        }
    }

    private void setDefaultProfileImage() {
        profileImageView.setImageResource(R.drawable.ic_profile); // Set a default placeholder image
    }


    private void readAloudForumContent() {
        String forumContent = "Welcome to the Main Page."; // Replace with actual content
        if (tts != null) {
            tts.speak(forumContent, TextToSpeech.QUEUE_FLUSH, null, null);
        } else {
            Log.e("TTS", "TTS is not initialized.");
        }
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

    @Override
    protected void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

}