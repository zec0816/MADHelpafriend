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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ForumOKU extends BaseActivity {

    private LinearLayout postContainer;
    private Button createPostButton;

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
            startActivity(intent); // Open the create post activity when the button is clicked
        });

        // Fetch posts from the server
        fetchPosts();

        // Setup bottom navigation
        setupBottomNavigation();
    }

    private void fetchPosts() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            StringBuilder result = new StringBuilder();

            // Log the API URL being used
            Log.d("ForumOKU", "API URL: " + Db_Contract.urlGetPost);

            try {
                URL url = new URL(Db_Contract.urlGetPost);
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
                    Log.e("ForumOKU", "Server returned non-OK status: " + conn.getResponseCode());
                }
            } catch (Exception e) {
                Log.e("ForumOKU", "Error fetching posts", e);
            }

            handler.post(() -> {
                try {
                    Log.d("ForumOKU", "Raw JSON Response: " + result.toString());

                    JSONArray jsonArray = new JSONArray(result.toString());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject post = jsonArray.getJSONObject(i);

                        // Updated JSON parsing logic
                        try {
                            String postId = post.optString("postID", "Unknown"); // Fallback if key is missing
                            String title = post.getString("title");
                            String content = post.getString("content");
                            String createdAt = post.getString("created_at");
                            int likeCount = post.optInt("like_count", 0);

                            // Add post to UI
                            addPostToContainer(postId, title, content, createdAt, likeCount);
                        } catch (JSONException e) {
                            Log.e("ForumOKU", "Error parsing individual post JSON", e);
                        }
                    }
                } catch (JSONException e) {
                    Log.e("ForumOKU", "Error parsing JSON response", e);
                }
            });
        });
    }


    private void addPostToContainer(String postId, String title, String content, String createdAt, int likeCount) {
        View postView = getLayoutInflater().inflate(R.layout.activity_post_layout, postContainer, false);

        // Bind views from the layout
        TextView titleView = postView.findViewById(R.id.postTitle);
        TextView contentView = postView.findViewById(R.id.postContent);
        TextView dateView = postView.findViewById(R.id.postDate);
        TextView likeCountView = postView.findViewById(R.id.likeCount);
        ImageButton loveButton = postView.findViewById(R.id.loveButton);
        ImageButton commentButton = postView.findViewById(R.id.commentButton);

        // Set post details
        titleView.setText(title);
        contentView.setText(content);
        dateView.setText(createdAt); // Set createdAt correctly
        likeCountView.setText(String.format("%d Likes", likeCount));

        // Set up comment button
        commentButton.setOnClickListener(view -> {
            Intent intent = new Intent(ForumOKU.this, CommentActivity.class);
            intent.putExtra("postId", postId); // Pass post ID to CommentActivity
            startActivity(intent);
        });

        // Handle love button functionality
        final boolean[] isLoved = {false}; // Track love state for this post
        loveButton.setOnClickListener(view -> {
            if (!isLoved[0]) {
                loveButton.setImageResource(R.drawable.icon_heart_filled);
                isLoved[0] = true;
            } else {
                loveButton.setImageResource(R.drawable.icon_heart_unfilled);
                isLoved[0] = false;
            }
        });



        // Add the post view to the container
        postContainer.addView(postView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("ForumOKU", "onResume triggered");
        if (postContainer != null) {
            postContainer.removeAllViews(); // Clear existing posts to avoid duplicates
        }
        fetchPosts(); // Reload posts
    }


    @Override
    protected int getSelectedNavItemId() {
        return R.id.nav_forum;
    }
}
