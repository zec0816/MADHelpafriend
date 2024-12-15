package com.example.helpafriend;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CommentActivity extends AppCompatActivity {

    private LinearLayout commentsContainer;
    private EditText commentInput;
    private Button submitCommentButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        // Get the post title from the intent
        String postTitle = getIntent().getStringExtra("postTitle");
        Log.d("CommentActivity", "Received post title: " + postTitle);

        // Initialize UI elements
        TextView titleView = findViewById(R.id.commentPostTitle);
        commentsContainer = findViewById(R.id.commentsContainer);
        commentInput = findViewById(R.id.commentInput);
        ImageButton submitCommentButton = (ImageButton) findViewById(R.id.submitCommentButton);
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(view -> finish());
        // Set the post title (fallback to "Unknown Post" if null)
        titleView.setText(postTitle != null ? postTitle : "Unknown Post");

        // Fetch existing comments
        fetchComments(postTitle);

        // Handle comment submission
        submitCommentButton.setOnClickListener(view -> {
            String comment = commentInput.getText().toString().trim();
            if (!comment.isEmpty()) {
                submitComment(postTitle, comment);
                commentInput.setText(""); // Clear input after submission
            }
        });
    }

    private void fetchComments(String postTitle) {
        String url = "https://yourserver.com/api/getComments?title=" + postTitle; // Replace with your endpoint

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        JSONArray comments = response.getJSONArray("comments");
                        for (int i = 0; i < comments.length(); i++) {
                            JSONObject comment = comments.getJSONObject(i);
                            addCommentToContainer(comment.getString("author"), comment.getString("content"));
                        }
                    } catch (JSONException e) {
                        Log.e("CommentActivity", "Error parsing comments JSON", e);
                    }
                },
                error -> Log.e("CommentActivity", "Failed to fetch comments", error)
        );

        Volley.newRequestQueue(this).add(request);
    }

    private void submitComment(String postTitle, String comment) {
        String url = "https://yourserver.com/api/addComment"; // Replace with your endpoint

        // Create JSON payload
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("title", postTitle);
            jsonBody.put("comment", comment);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                jsonBody,
                response -> addCommentToContainer("You", comment), // Add the comment to the UI
                error -> Log.e("CommentActivity", "Failed to submit comment", error)
        );

        Volley.newRequestQueue(this).add(request);
    }

    private void addCommentToContainer(String author, String content) {
        // Create a new TextView for the comment
        TextView commentView = new TextView(this);
        commentView.setText(author + ": " + content);
        commentView.setTextSize(14);
        commentView.setPadding(8, 8, 8, 8);

        // Add the TextView to the comments container
        commentsContainer.addView(commentView);
    }
}