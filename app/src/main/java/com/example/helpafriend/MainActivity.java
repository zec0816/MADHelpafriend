package com.example.helpafriend;


import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;

import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
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

    private static final String TAG = "MainActivity";

    private SharedPreferences sharedPreferences;
    private ImageView profileImageView;
    private String username;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI components
        profileImageView = findViewById(R.id.profileImage);
        recentPostsRecyclerView = findViewById(R.id.recentPostsRecyclerView);
        welcomeText = findViewById(R.id.welcomeText); // Get Welcome TextView
        emptyStateText = findViewById(R.id.emptyStateText);

        // Set up RecyclerView
        recentPostsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        postList = new ArrayList<>();
        postAdapter = new RecentPostsAdapter(postList);
        recentPostsRecyclerView.setAdapter(postAdapter);
        ImageButton readAloudButton = findViewById(R.id.readAloud);
        Button requestHelpButton = findViewById(R.id.requestHelpButton);

        // Set up the create post button
        requestHelpButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, MapOKU.class);
            startActivity(intent);
        });


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

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        username = sharedPreferences.getString("username", null);

        // Load the profile image
        loadProfileImage();

        // Use the string resource for the welcome message
        String welcomeMessage = getString(R.string.welcome) + ", " + username;
        welcomeText.setText(welcomeMessage);

        // Fetch and display the two most recent posts
        fetchRecentPosts();

        // Set up the bottom navigation
        setupBottomNavigation();
        applySelectedLanguage();
    }

    private void loadProfileImage() {
        // Get user-specific image file
        File imageFile = getUserProfileImageFile();
        if (imageFile.exists()) {
            try {
                Uri imageUri = Uri.fromFile(imageFile);
                profileImageView.setImageURI(imageUri);
                return;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Fallback: Load from SharedPreferences
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

    private File getUserProfileImageFile() {
        return new File(getFilesDir(), "profile_image_" + username + ".jpg");
    }

    private void setDefaultProfileImage() {
        profileImageView.setImageResource(R.drawable.ic_profile); // Default placeholder
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