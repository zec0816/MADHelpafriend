package com.example.helpafriend;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        String url = Db_Contract.urlLogin;

        // Login request example
        JsonObjectRequest loginRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                null,
                response -> {
                    try {
                        if (response.has("id_user") && response.has("username")) {
                            // Save user details in SharedPreferences
                            SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putInt("id_user", response.getInt("id_user"));
                            editor.putString("username", response.getString("username"));
                            editor.apply();


                            // Navigate to MainActivity
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(this, "Invalid login response", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Log.e("LoginActivity", "JSON Parsing Error", e);
                        Toast.makeText(this, "Error parsing login response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("LoginActivity", "Login Error", error);
                    Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show();
                }
        );

        Volley.newRequestQueue(this).add(loginRequest);
    }
}
