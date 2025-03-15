package com.example.helpafriend;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import java.util.Map;

public class ForumCreateComment extends AppCompatActivity {

    private EditText editTextContent;
    private ImageButton buttonSubmitComment;
    private LinearLayout commentsContainer;
    private String username;
    private String postId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        username = sharedPreferences.getString("username", null);

        postId = getIntent().getStringExtra("postId");

        editTextContent = findViewById(R.id.commentInput);
        buttonSubmitComment = findViewById(R.id.submitCommentButton);
        commentsContainer = findViewById(R.id.commentsContainer);
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(view -> {
            onBackPressed();
        });

        buttonSubmitComment.setOnClickListener(v -> {
            String comment = editTextContent.getText().toString().trim();

            if (!comment.isEmpty() && username != null) {
                submitComment(comment);
            } else {
                Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
            }
        });

        fetchComments();
    }

    private void fetchComments() {
        String url = Db_Contract.urlGetComment + "?id_post=" + postId;
        Log.d("fetchComments", "Fetching comments from URL: " + url);

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    Log.d("fetchComments", "API Response: " + response);

                    parseAndDisplayComments(response);
                },
                error -> {
                    Log.e("fetchComments", "Error fetching comments: ", error);
                    Toast.makeText(this, "Failed to load comments", Toast.LENGTH_SHORT).show();
                });

        Volley.newRequestQueue(this).add(request);
    }

    private void submitComment(String comment) {
        String url = Db_Contract.urlSubmitComment;
        Log.d("submitComment", "Submitting comment to URL: " + url);

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    Log.d("submitComment", "API Response: " + response);
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        if (jsonResponse.optString("status").equals("success")) {
                            Toast.makeText(this, "Comment submitted successfully", Toast.LENGTH_SHORT).show();
                            editTextContent.setText("");
                            fetchComments();
                        } else {
                            Toast.makeText(this, "Failed to submit comment: " + jsonResponse.optString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Log.e("submitComment", "Error parsing JSON: ", e);
                    }
                },
                error -> {
                    Log.e("submitComment", "Error submitting comment: ", error);
                    Toast.makeText(this, "Failed to submit comment", Toast.LENGTH_SHORT).show();
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

    private void parseAndDisplayComments(String json) {
        try {
            JSONObject jsonResponse = new JSONObject(json);

            if (!jsonResponse.optString("status").equals("success")) {
                Toast.makeText(this, jsonResponse.optString("message"), Toast.LENGTH_SHORT).show();
                return;
            }

            JSONArray commentsArray = jsonResponse.getJSONArray("comments");
            commentsContainer.removeAllViews(); // Clear old comments

            if (commentsArray.length() == 0) {
                Toast.makeText(this, "No comments available", Toast.LENGTH_SHORT).show();
                return;
            }

            for (int i = 0; i < commentsArray.length(); i++) {
                JSONObject comment = commentsArray.getJSONObject(i);

                // Extract values
                String username = comment.optString("username", "Unknown User");
                String content = comment.optString("comment", "No content");
                String createdAt = comment.optString("created_at", "Unknown Date");

                // Inflate comment view
                View commentView = getLayoutInflater().inflate(R.layout.item_comment, commentsContainer, false);

                TextView usernameView = commentView.findViewById(R.id.commentUsername);
                TextView contentView = commentView.findViewById(R.id.commentContent);
                TextView dateView = commentView.findViewById(R.id.commentDate);

                usernameView.setText(username);
                contentView.setText(content);
                dateView.setText(createdAt);

                commentsContainer.addView(commentView);
            }
        } catch (JSONException e) {
            Log.e("parseAndDisplayComments", "Error parsing JSON: ", e);
            Toast.makeText(this, "Error parsing comments", Toast.LENGTH_SHORT).show();
        }
    }

}
