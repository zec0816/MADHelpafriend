
package com.example.helpafriend;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
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
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ForumOKU extends BaseActivity {

    private LinearLayout postContainer;
    private Button createPostButton;
    private Set<String> loadedPostIds = new HashSet<>(); // Track unique post IDs to avoid duplicates
    private TextToSpeech tts;
    private boolean isReadingAloud = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum_oku);

        // Initialize UI elements
        postContainer = findViewById(R.id.postContainer);
        createPostButton = findViewById(R.id.createPostButton);
        ImageButton readAloudButton = findViewById(R.id.readAloud);

        // Set up the create post button
        createPostButton.setOnClickListener(view -> {
            Intent intent = new Intent(ForumOKU.this, ForumCreatePostOKU.class);
            startActivity(intent);
        });

        // Read Aloud Button
        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                tts.setLanguage(Locale.US);
            } else {
                Log.e("TTS", "Initialization failed");
            }
        });

        // Handle Read Aloud Button
        readAloudButton.setOnClickListener(view -> {
            if (isReadingAloud) {
                stopTTS(); // Stop TTS if already speaking
            } else {
                readAloudForumContent(); // Start reading aloud
            }
            isReadingAloud = !isReadingAloud; // Toggle the state
        });

        // Fetch posts for the first time
        fetchPosts();

        // Setup bottom navigation
        setupBottomNavigation();
    }

    private void readAloudForumContent() {
        if (tts == null) {
            Log.e("TTS", "TTS is not initialized");
            return;
        }

        // Fetch the content to read aloud
        StringBuilder forumContent = new StringBuilder("Welcome to the Forum Page. ");

        // Loop through visible forum posts and concatenate text
        for (int i = 0; i < postContainer.getChildCount(); i++) {
            View postView = postContainer.getChildAt(i);
            TextView titleView = postView.findViewById(R.id.postTitle);
            TextView contentView = postView.findViewById(R.id.postContent);

            if (titleView != null && contentView != null) {
                forumContent.append("Title: ").append(titleView.getText().toString()).append(". ");
                forumContent.append("Content: ").append(contentView.getText().toString()).append(". ");
            }
        }

        // Read the content aloud
        tts.speak(forumContent.toString(), TextToSpeech.QUEUE_FLUSH, null, null);
    }

    private void stopTTS() {
        if (tts != null && tts.isSpeaking()) {
            tts.stop();
            isReadingAloud = false; // Ensure state is consistent
            Log.d("TTS", "Text-to-Speech stopped");
        }
    }

    private void fetchPosts() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            StringBuilder result = new StringBuilder();

            try {
                URL url = new URL(Db_Contract.urlGetPost); // Ensure your API URL is correct
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
                }
            } catch (Exception e) {
                Log.e("ForumOKU", "Error fetching posts", e);
            }

            handler.post(() -> {
                try {
                    JSONArray jsonArray = new JSONArray(result.toString());

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject post = jsonArray.getJSONObject(i);

                        // Extract post details
                        String postId = post.optString("id_post", "Unknown");
                        if (!loadedPostIds.contains(postId)) { // Check if post ID is already loaded
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
        ImageButton commentButton = postView.findViewById(R.id.commentButton);

        titleView.setText(title);
        contentView.setText(content);
        dateView.setText(createdAt);
        // Set up comment button
        commentButton.setOnClickListener(view -> {
            Intent intent = new Intent(ForumOKU.this, CommentActivity.class);
            intent.putExtra("postId", postId);
            startActivity(intent);
        });

        likeCountView.setText(String.format("%d Likes", likeCount));
        loveButton.setImageResource(isLiked ? R.drawable.icon_heart_filled : R.drawable.icon_heart_unfilled);

        loveButton.setTag(isLiked); // Store the current state in the button's tag

        loveButton.setOnClickListener(view -> {
            boolean currentState = (boolean) loveButton.getTag(); // Get the current state
            loveButton.setEnabled(false); // Disable the button during the request
            updateLikeState(postId, !currentState, likeCountView, loveButton); // Toggle the state
        });


        postContainer.addView(postView);
    }

    private void updateLikeState(String postId, boolean isLiked, TextView likeCountView, ImageButton loveButton) {
        String action = isLiked ? "like" : "unlike";
        JSONObject requestParams = new JSONObject();
        try {
            requestParams.put("post_id", postId);
            requestParams.put("user_id", "2"); // Replace with the actual logged-in user's ID
            requestParams.put("action", action);

            Log.d("ForumOKU", "Request Params: " + requestParams.toString());
        } catch (JSONException e) {
            e.printStackTrace();
            loveButton.setEnabled(true); // Re-enable the button on error
            return;
        }

        JsonObjectRequest jsonRequest = new JsonObjectRequest(
                Request.Method.POST,
                Db_Contract.urlLikes,
                requestParams,
                response -> {
                    try {
                        if (response.has("status") && (response.getString("status").equals("liked") || response.getString("status").equals("unliked"))) {
                            // Update the like count
                            int updatedLikeCount = response.getInt("like_count");
                            likeCountView.setText(String.format("%d Likes", updatedLikeCount));

                            // Update the icon and tag based on the response
                            loveButton.setImageResource(isLiked ? R.drawable.icon_heart_filled : R.drawable.icon_heart_unfilled);
                            loveButton.setTag(isLiked); // Update the tag to the new state
                        } else {
                            // Show error message if the action is invalid
                            String message = response.has("message") ? response.getString("message") : "Action failed.";
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Log.e("ForumOKU", "Error parsing response: " + e.getMessage());
                    } finally {
                        loveButton.setEnabled(true); // Re-enable the button
                    }
                },
                error -> {
                    // Handle network or server errors
                    Log.e("ForumOKU", "Error updating like state: " + error.getMessage());
                    Toast.makeText(ForumOKU.this, "Failed to update like state. Please try again.", Toast.LENGTH_SHORT).show();
                    loveButton.setEnabled(true); // Re-enable the button
                }
        );

        Volley.newRequestQueue(this).add(jsonRequest);
    }

    @Override
    protected void onResume() {
        super.onResume();
        reloadPosts();
    }

    private void reloadPosts() {
        postContainer.removeAllViews(); // Clear the existing posts
        loadedPostIds.clear(); // Clear the IDs to re-fetch posts properly
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

    @Override
    protected void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

}