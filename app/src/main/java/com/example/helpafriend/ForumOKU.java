
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

        postContainer = findViewById(R.id.postContainer);
        createPostButton = findViewById(R.id.createPostButton);
        Button readAloudButton = findViewById(R.id.readAloudButton);

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

        readAloudButton.setOnClickListener(view -> {
            if (isReadingAloud) {
                stopTTS();
            } else {
                readAloudForumContent();
            }
            isReadingAloud = !isReadingAloud;
        });

        fetchPosts();

        setupBottomNavigation();
    }

    private void readAloudForumContent() {
        if (tts == null) {
            Log.e("TTS", "TTS is not initialized");
            return;
        }

        StringBuilder forumContent = new StringBuilder("Welcome to the Forum Page. ");

        // Loop through visible forum posts and text
        for (int i = 0; i < postContainer.getChildCount(); i++) {
            View postView = postContainer.getChildAt(i);
            TextView titleView = postView.findViewById(R.id.postTitle);
            TextView contentView = postView.findViewById(R.id.postContent);

            if (titleView != null && contentView != null) {
                forumContent.append("Title: ").append(titleView.getText().toString()).append(". ");
                forumContent.append("Content: ").append(contentView.getText().toString()).append(". ");
            }
        }

        tts.speak(forumContent.toString(), TextToSpeech.QUEUE_FLUSH, null, null);
    }

    private void stopTTS() {
        if (tts != null && tts.isSpeaking()) {
            tts.stop();
            isReadingAloud = false;
            Log.d("TTS", "Text-to-Speech stopped");
        }
    }

    private void fetchPosts() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            StringBuilder result = new StringBuilder();

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
                        if (!loadedPostIds.contains(postId)) {
                            String title = post.optString("title", "No Title");
                            String content = post.optString("content", "No Content");
                            String createdAt = post.optString("created_at", "Unknown Date");
                            int likeCount = post.optInt("like_count", 0);
                            // Add post to UI
                            addPostToContainer(postId, title, content, createdAt, likeCount);
                            loadedPostIds.add(postId); // Track loaded post ID
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

        TextView titleView = postView.findViewById(R.id.postTitle);
        TextView contentView = postView.findViewById(R.id.postContent);
        TextView dateView = postView.findViewById(R.id.postDate);
        TextView likeCountView = postView.findViewById(R.id.likeCount);
        ImageButton loveButton = postView.findViewById(R.id.loveButton);
        ImageButton commentButton = postView.findViewById(R.id.commentButton);

        titleView.setText(title);
        contentView.setText(content);
        dateView.setText(createdAt);
        likeCountView.setText(String.format("%d Likes", likeCount));

        commentButton.setOnClickListener(view -> {
            Intent intent = new Intent(ForumOKU.this, CommentActivity.class);
            intent.putExtra("postId", postId);
            startActivity(intent);
        });

        // Handle love button functionality
        final boolean[] isLoved = {false};
        loveButton.setOnClickListener(view -> {
            if (!isLoved[0]) {
                loveButton.setImageResource(R.drawable.icon_heart_filled);
                isLoved[0] = true;
            } else {
                loveButton.setImageResource(R.drawable.icon_heart_unfilled);
                isLoved[0] = false;
            }
        });

        postContainer.addView(postView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        reloadPosts();
    }

    private void reloadPosts() {
        postContainer.removeAllViews();
        loadedPostIds.clear();
        fetchPosts();
    }

    @Override
    protected int getSelectedNavItemId(String role) {
        if ("volunteer".equals(role)) {
            return R.id.volunteer_forum;
        } else {
            return R.id.nav_forum;
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