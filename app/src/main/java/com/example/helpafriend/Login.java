package com.example.helpafriend;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class Login extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private Button btnLogin, btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.tvSignUp);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Register.class));
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();

                if (!(username.isEmpty() || password.isEmpty())) {

                    RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

                    String url = Db_Contract.urlLogin + "?username=" + username + "&password=" + password;

                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String status = response.getString("status");

                                if (status.equals("success")) {
                                    String role = response.getString("role");

                                    // Save username and role in SharedPreferences
                                    SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("username", username);
                                    editor.putString("role", role);
                                    editor.putString("password", password);
                                    editor.apply();

                                    Toast.makeText(getApplicationContext(), "Successfully logged in", Toast.LENGTH_SHORT).show();

                                    // Redirect based on role
                                    if ("volunteer".equals(role)) {
                                        startActivity(new Intent(getApplicationContext(), VolunteerMainPage.class));
                                    } else {
                                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                    }
                                } else {
                                    Toast.makeText(getApplicationContext(), "Login failed", Toast.LENGTH_SHORT).show();
                                }

                            } catch (JSONException e) {
                                Toast.makeText(getApplicationContext(), "Error parsing response", Toast.LENGTH_SHORT).show();
                            }
                        }

                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });

                    requestQueue.add(jsonObjectRequest);

                } else {
                    Toast.makeText(getApplicationContext(), "Please enter username and password", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
