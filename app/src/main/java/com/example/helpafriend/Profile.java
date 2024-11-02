package com.example.helpafriend;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class Profile extends BaseActivity {
    private TextView tvUsername;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Use the same SharedPreferences name as in Login
        sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);

        // Initialize username TextView
        tvUsername = findViewById(R.id.username);

        // Update username display
        updateUsername();

        // Setup bottom navigation
        setupBottomNavigation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUsername(); // Refresh username every time activity becomes visible
    }

    private void updateUsername() {
        String username = sharedPreferences.getString("username", "");
        if (!username.isEmpty()) {
            tvUsername.setText(username);
        } else {
            // For debugging - remove in production
            Toast.makeText(this, "Username not found in SharedPreferences", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected int getSelectedNavItemId() {
        return R.id.nav_profile;
    }
}