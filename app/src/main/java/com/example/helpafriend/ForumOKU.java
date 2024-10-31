package com.example.helpafriend;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ForumOKU extends AppCompatActivity {

    private LinearLayout postContainer;
    private Button createPostButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum_oku);

        postContainer = findViewById(R.id.postContainer);
        createPostButton = findViewById(R.id.createPostButton);

        createPostButton.setOnClickListener(view -> {
            Intent intent = new Intent(ForumOKU.this, ForumCreatePostOKU.class);
            startActivity(intent);
        });

        fetchPosts();
    }

    private void fetchPosts() {
        new FetchPostsTask().execute(Db_Contract.urlGetPost);
    }

    private class FetchPostsTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            StringBuilder result = new StringBuilder();
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                reader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONArray jsonArray = new JSONArray(result);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject post = jsonArray.getJSONObject(i);
                    addPostToContainer(post.getString("title"), post.getString("content"), post.getString("created_at"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void addPostToContainer(String title, String content, String createdAt) {
        LinearLayout postBox = new LinearLayout(this);
        postBox.setOrientation(LinearLayout.VERTICAL);
        postBox.setPadding(16, 16, 16, 16);
        postBox.setBackgroundResource(R.drawable.post_background); // Set the new background drawable

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, 16); // Set bottom margin (16dp) for spacing
        postBox.setLayoutParams(params);

        TextView titleView = new TextView(this);
        titleView.setText(title);
        titleView.setTextSize(18);

        TextView contentView = new TextView(this);
        contentView.setText(content);
        contentView.setTextSize(14);

        TextView dateView = new TextView(this);
        dateView.setText(createdAt);
        dateView.setTextSize(12);

        postBox.addView(titleView);
        postBox.addView(contentView);
        postBox.addView(dateView);

        postContainer.addView(postBox);
    }

}
