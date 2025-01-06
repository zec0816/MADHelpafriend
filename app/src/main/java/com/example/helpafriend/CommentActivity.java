package com.example.helpafriend;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CommentActivity extends AppCompatActivity {

    private LinearLayout commentsContainer;
    private EditText commentInput;
    private ImageButton submitCommentButton;
    private TextView titleView;
    private String postId;
    private final String TAG = "CommentActivity";

    private TextToSpeech tts;

    private boolean isReadingAloud = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        // Initialize UI components
        commentsContainer = findViewById(R.id.commentsContainer);
        commentInput = findViewById(R.id.commentInput);
        submitCommentButton = findViewById(R.id.submitCommentButton);
        titleView = findViewById(R.id.commentPostTitle);

        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(view -> finish());
            // Initialize Text-to-Speech
        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int result = tts.setLanguage(Locale.US);
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TTS", "Language not supported or missing data.");
                    Toast.makeText(this, "TTS language not supported", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.e("TTS", "Initialization failed.");
                Toast.makeText(this, "TTS initialization failed", Toast.LENGTH_SHORT).show();
            }
        });

        // Get post ID from Intent
        postId = getIntent().getStringExtra("postId");
        Log.d(TAG, "Post ID: " + postId);

        // Read Aloud Button
        ImageButton readAloudButton = findViewById(R.id.readAloud);
        readAloudButton.setOnClickListener(view -> {
            if (isReadingAloud) {
                stopTTS(); // Stop TTS if already speaking
            } else {
                readAloudForumContent(); // Start reading aloud
            }
            isReadingAloud = !isReadingAloud; // Toggle the state
        });

        // Fetch post title and comments
        fetchPostDetailsAndComments();

        // Handle submit comment button
        submitCommentButton.setOnClickListener(view -> {
            String comment = commentInput.getText().toString().trim();
            if (comment.isEmpty()) {
                Toast.makeText(this, "Comment cannot be empty", Toast.LENGTH_SHORT).show();
            } else {
                submitComment(comment);
                submitCommentButton.setEnabled(false);
            }
        });
    }

    private void readAloudForumContent() {
        StringBuilder forumContent = new StringBuilder();

        // Say welcome message
        forumContent.append("Welcome to the Comment Page. ");

        // Add comments to the content
        for (int i = 0; i < commentsContainer.getChildCount(); i++) {
            View commentView = commentsContainer.getChildAt(i);
            TextView usernameView = commentView.findViewById(R.id.commentUsername);
            TextView contentView = commentView.findViewById(R.id.commentContent);

            if (usernameView != null && contentView != null) {
                forumContent.append("User ").append(usernameView.getText().toString()).append(" said ");
                forumContent.append(contentView.getText().toString()).append(". ");
            }
        }

        if (tts != null) {
            if (forumContent.length() > 0) {
                tts.speak(forumContent.toString(), TextToSpeech.QUEUE_FLUSH, null, null);
            } else {
                tts.speak("Welcome to the Comment Page. No comments available to read.", TextToSpeech.QUEUE_FLUSH, null, null);
            }
        }
    }

    private void stopTTS() {
        if (tts != null && tts.isSpeaking()) {
            tts.stop();
            Log.d("TTS", "Text-to-Speech stopped");
            Toast.makeText(this, "Text-to-Speech stopped", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchPostDetailsAndComments() {
        String url = Db_Contract.urlGetComment + "?id_post=" + postId;

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        if (jsonResponse.optString("status").equals("success")) {
                            // Set post title
                            String postTitle = jsonResponse.optString("postTitle", "Unknown Post");
                            titleView.setText(postTitle);

                            // Display comments
                            JSONArray jsonArray = jsonResponse.getJSONArray("comments");
                            commentsContainer.removeAllViews();

                            if (jsonArray.length() == 0) {
                                // No comments case
                                Toast.makeText(this, "No comments available for this post", Toast.LENGTH_SHORT).show();
                            } else {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject commentObj = jsonArray.getJSONObject(i);
                                    String username = commentObj.optString("username", "Unknown User");
                                    String content = commentObj.optString("comment", "No content");
                                    String createdAt = commentObj.optString("created_at", "Unknown Date");
                                    addCommentToContainer(username, content, createdAt);
                                }
                            }
                        } else {
                            Toast.makeText(this, "Failed to fetch post details", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "Error parsing JSON: ", e);
                        Toast.makeText(this, "Error parsing post details", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e(TAG, "Error fetching post details: ", error);
                    Toast.makeText(this, "Error fetching post details", Toast.LENGTH_SHORT).show();
                });

        Volley.newRequestQueue(this).add(request);
    }

    private void submitComment(String comment) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", null);

        if (username == null) {
            Toast.makeText(this, "Error: User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = Db_Contract.urlSubmitComment;

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        if (jsonResponse.optString("status").equals("success")) {
                            Toast.makeText(this, "Comment added successfully", Toast.LENGTH_SHORT).show();
                            fetchPostDetailsAndComments(); // Refresh comments
                            commentInput.setText(""); // Clear input
                        } else {
                            Toast.makeText(this, "Failed to add comment: " + jsonResponse.optString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "Error parsing response: ", e);
                        Toast.makeText(this, "Error submitting comment", Toast.LENGTH_SHORT).show();
                    } finally {
                        submitCommentButton.setEnabled(true);
                    }
                },
                error -> {
                    Log.e(TAG, "Error submitting comment: ", error);
                    Toast.makeText(this, "Error submitting comment", Toast.LENGTH_SHORT).show();
                    submitCommentButton.setEnabled(true);
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id_post", postId);
                params.put("username", username);
                params.put("comment", comment);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    private void addCommentToContainer(String username, String content, String createdAt) {
        View commentView = getLayoutInflater().inflate(R.layout.item_comment, commentsContainer, false);

        TextView usernameView = commentView.findViewById(R.id.commentUsername);
        TextView commentTextView = commentView.findViewById(R.id.commentContent);
        TextView commentDateView = commentView.findViewById(R.id.commentDate);

        usernameView.setText(username);
        commentTextView.setText(content);
        commentDateView.setText(createdAt);

        commentsContainer.addView(commentView);
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
