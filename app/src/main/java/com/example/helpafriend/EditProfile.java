package com.example.helpafriend;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.view.View;
import android.widget.Toast;
import android.app.AlertDialog;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EditProfile extends AppCompatActivity {

    private TextView currentUsernameTextView;
    private EditText usernameEditText, passwordEditText, confirmPasswordEditText;
    private Button saveButton;
    private ProgressBar progressBar;

    private String oldUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Back button for returning to the previous screen
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(view -> finish());

        currentUsernameTextView = findViewById(R.id.currentUsernameTextView);
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        saveButton = findViewById(R.id.saveButton);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        oldUsername = prefs.getString("username", "UnknownUser");
        currentUsernameTextView.setText("Current Username: " + oldUsername);

        saveButton.setOnClickListener(view -> {
            String newUsername = usernameEditText.getText().toString().trim();
            String newPassword = passwordEditText.getText().toString().trim();
            String confirmPassword = confirmPasswordEditText.getText().toString().trim();

            if (validateInput(newUsername, newPassword, confirmPassword)) {
                progressBar.setVisibility(View.VISIBLE);
                updateProfileOnServer(oldUsername, newUsername, newPassword);
            }
        });
    }

    private boolean validateInput(String newUsername, String newPassword, String confirmPassword) {
        if (newUsername.isEmpty() && newPassword.isEmpty()) {
            Toast.makeText(this, "No changes detected. Please update your username or password.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!newUsername.isEmpty() && newUsername.length() < 3) {
            usernameEditText.setError("Username must be at least 3 characters");
            return false;
        }

        if (!newPassword.isEmpty() && newPassword.length() < 6) {
            passwordEditText.setError("Password must be at least 6 characters");
            return false;
        }

        if (!newPassword.isEmpty() && !newPassword.equals(confirmPassword)) {
            confirmPasswordEditText.setError("Passwords do not match");
            return false;
        }

        return true;
    }

    private void updateProfileOnServer(String oldUsername, String newUsername, String newPassword) {
        String url = Db_Contract.urlUpdateProfile;

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    progressBar.setVisibility(View.GONE); // Hide the progress bar after the response
                    Log.d("UpdateProfile", "Server Response: " + response);


                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean success = jsonResponse.getBoolean("success");
                        String message = jsonResponse.getString("message");

                        if (success) {
                            Toast.makeText(EditProfile.this, message, Toast.LENGTH_SHORT).show();

                            // Update username in SharedPreferences if it was changed
                            if (!newUsername.isEmpty()) {
                                SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                                SharedPreferences.Editor editor = prefs.edit();
                                editor.putString("username", newUsername);
                                editor.remove("password");
                                editor.apply();
                            }

                            showReloginPrompt();
                        } else {
                            Toast.makeText(EditProfile.this, "Failed to update profile. Try again.", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.e("UpdateProfile", "Error parsing response: " + e.toString());
                        Toast.makeText(EditProfile.this, "Error: " + e.toString(), Toast.LENGTH_SHORT).show();
                    }

                },
                error -> {
                    progressBar.setVisibility(View.GONE);
                    Log.e("UpdateProfile", "Error: " + error.toString());
                    Toast.makeText(EditProfile.this, "Error: " + error.toString(), Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("old_username", oldUsername);
                params.put("new_username", newUsername);
                params.put("new_password", newPassword);
                return params;
            }
        };

        requestQueue.add(stringRequest);
    }


    private void showReloginPrompt() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Re-login Required")
                .setMessage("Your profile has been successfully updated. Please log in again.")
                .setPositiveButton("OK", (dialog, which) -> {
                    // Clear SharedPreferences to log the user out
                    SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.clear();
                    editor.apply();

                    // Redirect to the Login activity
                    Intent loginIntent = new Intent(EditProfile.this, Login.class);
                    startActivity(loginIntent);
                    finish();
                })
                .setCancelable(false) // Prevent the dialog from being dismissed when tapped outside
                .show();
    }
}

