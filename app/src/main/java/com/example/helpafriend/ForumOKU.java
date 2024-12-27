
package com.example.helpafriend;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ForumOKU extends BaseActivity {

    private LinearLayout postContainer;
    private Button createPostButton;
    private Set<String> loadedPostIds = new HashSet<>(); // Track unique post IDs to avoid duplicates

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum_oku);

        // Initialize UI elements
        postContainer = findViewById(R.id.postContainer);
        createPostButton = findViewById(R.id.createPostButton);

        // Set up the create post button
        createPostButton.setOnClickListener(view -> {
            Intent intent = new Intent(ForumOKU.this, ForumCreatePostOKU.class);
            startActivity(intent);
        });

        // Fetch posts for the first time
        fetchPosts();

        // Setup bottom navigation
        setupBottomNavigation();
    }

    private void fetchPosts() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            StringBuilder result = new StringBuilder();

            try {
                // Replace with dynamic user ID
                int userId = 2;
                URL url = new URL(Db_Contract.urlGetPost + "?user_id=" + userId);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                    reader.close();
                } else {
                    Log.e("ForumOKU", "Server Error: " + conn.getResponseCode());
                    return;
                }
            } catch (Exception e) {
                Log.e("ForumOKU", "Error fetching posts", e);
                return;
            }

            handler.post(() -> {
                try {
                    JSONArray jsonArray = new JSONArray(result.toString());
                    postContainer.removeAllViews(); // Clear old posts
                    loadedPostIds.clear();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject post = jsonArray.getJSONObject(i);

                        // Extract post details
                        String postId = post.optString("id_post", "Unknown");
                        if (!loadedPostIds.contains(postId)) { // Avoid duplicates
                            String title = post.optString("title", "No Title");
                            String content = post.optString("content", "No Content");
                            String createdAt = post.optString("created_at", "Unknown Date");
                            int likeCount = post.optInt("like_count", 0);
                            boolean isLiked = post.optBoolean("is_liked", false); // Fetch liked state
                            Log.d("ForumOKU", "Post ID: " + postId + ", isLiked: " + isLiked);
                            // Add post to UI
                            addPostToContainer(postId, title, content, createdAt, likeCount, isLiked);
                            loadedPostIds.add(postId); // Track loaded post ID
                        }
                    }
                } catch (JSONException e) {
                    Log.e("ForumOKU", "Error parsing JSON response", e);
                    Toast.makeText(ForumOKU.this, "Failed to load posts.", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }









    private void addPostToContainer(String postId, String title, String content, String createdAt, int likeCount, boolean isLiked) {
        View postView = getLayoutInflater().inflate(R.layout.activity_post_layout, postContainer, false);

        // Bind views
        TextView titleView = postView.findViewById(R.id.postTitle);
        TextView contentView = postView.findViewById(R.id.postContent);
        TextView dateView = postView.findViewById(R.id.postDate);
        TextView likeCountView = postView.findViewById(R.id.likeCount);
        ImageButton loveButton = postView.findViewById(R.id.loveButton);

        // Populate UI
        titleView.setText(title);
        contentView.setText(content);
        dateView.setText(createdAt);
        likeCountView.setText(String.format("%d Likes", likeCount));

        // Set initial like button state from backend
        loveButton.setImageResource(isLiked ? R.drawable.icon_heart_filled : R.drawable.icon_heart_unfilled);

        // Handle like button click
        loveButton.setOnClickListener(view -> {
            boolean newLikedState = !isLiked; // Toggle state
            updateLikeState(postId, newLikedState, likeCountView, loveButton);

            // Update the UI immediately
            loveButton.setImageResource(newLikedState ? R.drawable.icon_heart_filled : R.drawable.icon_heart_unfilled);
        });

        postContainer.addView(postView);
    }








    private void updateLikeState(String postId, boolean isLiked, TextView likeCountView, ImageButton loveButton) {
        String action = isLiked ? "like" : "unlike"; // Determine the action
        JSONObject requestParams = new JSONObject();
        try {
            requestParams.put("post_id", postId);
            requestParams.put("user_id", "2"); // Replace with the actual logged-in user's ID
            requestParams.put("action", action);

            Log.d("ForumOKU", "Request Params: " + requestParams.toString());
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        JsonObjectRequest jsonRequest = new JsonObjectRequest(
                Request.Method.POST,
                Db_Contract.urlLikes,
                requestParams,
                response -> {
                    try {
                        Log.d("ForumOKU", "Response: " + response.toString());

                        // Update the like count if present in the response
                        if (response.has("like_count")) {
                            int updatedLikeCount = response.getInt("like_count");
                            likeCountView.setText(String.format("%d Likes", updatedLikeCount));
                        }
                    } catch (JSONException e) {
                        Log.e("ForumOKU", "Error parsing response: " + e.getMessage());
                    }
                },
                error -> {
                    // Log and handle the error
                    Log.e("ForumOKU", "Error updating like state: " + error.getMessage());
                    Toast.makeText(ForumOKU.this, "Failed to update like state. Please try again.", Toast.LENGTH_SHORT).show();
                }
        ) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }
        };

        // Add the request to the Volley queue
        Volley.newRequestQueue(this).add(jsonRequest);
    }






    @Override
    protected void onResume() {
        super.onResume();
        reloadPosts();
    }

    private void reloadPosts() {
        postContainer.removeAllViews(); // Clear the existing posts
        loadedPostIds.clear(); // Clear loaded post IDs to allow fresh reload
        fetchPosts(); // Reload posts
    }


    @Override
    protected int getSelectedNavItemId(String role) {
        if ("volunteer".equals(role)) {
            return R.id.volunteer_forum; // Default item for volunteer role
        } else {
            return R.id.nav_forum; // Correct ID for forum in OKU role
        }
    }
}
