package com.example.helpafriend;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class ForumCreatePostOKU extends BaseActivity {

    private EditText editTextTitle, editTextContent;
    private Button buttonSubmitPost;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum_create_post_oku);

        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        username = sharedPreferences.getString("username", null); // Get the username, default is null if not found

        editTextTitle = findViewById(R.id.editTextTitle);
        editTextContent = findViewById(R.id.editTextContent);
        buttonSubmitPost = findViewById(R.id.buttonSubmitPost);
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(view -> finish());

        buttonSubmitPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = editTextTitle.getText().toString().trim();
                String content = editTextContent.getText().toString().trim();

                if (!title.isEmpty() && !content.isEmpty() && username != null) {
                    new SubmitPostTask().execute(title, content);
                } else {
                    Toast.makeText(ForumCreatePostOKU.this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private class SubmitPostTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String title = params[0];
            String content = params[1];

            try {
                URL url = new URL(Db_Contract.urlSubmitPost);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);

                OutputStream os = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                String postData = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8") // Use the saved username
                        + "&" + URLEncoder.encode("title", "UTF-8") + "=" + URLEncoder.encode(title, "UTF-8")
                        + "&" + URLEncoder.encode("content", "UTF-8") + "=" + URLEncoder.encode(content, "UTF-8");

                writer.write(postData);
                writer.flush();
                writer.close();
                os.close();

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // Read the response from the server
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();
                    return response.toString();
                } else {
                    return "Failed to submit post";
                }

            } catch (Exception e) {
                e.printStackTrace();
                return "Exception: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(ForumCreatePostOKU.this, result, Toast.LENGTH_SHORT).show();
            if (result.equals("Post submitted successfully")) {
                editTextTitle.setText("");
                editTextContent.setText("");
            }
        }
    }

}
