package com.example.helpafriend;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String role = sharedPreferences.getString("role", "OKU"); // Default to OKU if no role found

        // Load the correct menu based on the role
        if (role.equals("volunteer")) {
            bottomNavigationView.inflateMenu(R.menu.bottom_nav_menu_volunteer);
        } else {
            bottomNavigationView.inflateMenu(R.menu.bottom_nav_menu); // Default to OKU
        }

        bottomNavigationView.setSelectedItemId(getSelectedNavItemId(role)); // Set the correct default selected item

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (role.equals("volunteer")) {
                // Handle volunteer-specific navigation
                if (itemId == R.id.volunteer_home) {
                    if (getSelectedNavItemId(role) != R.id.volunteer_home) {
                        startActivity(new Intent(this, MainActivity.class));
                        finish();
                    }
                    return true;
                } else if (itemId == R.id.volunteer_forum) {
                    if (getSelectedNavItemId(role) != R.id.volunteer_forum) {
                        startActivity(new Intent(this, ForumOKU.class));
                        finish();
                    }
                    return true;
                } else if (itemId == R.id.volunteer_activity) {
                    if (getSelectedNavItemId(role) != R.id.volunteer_profile) {
                        startActivity(new Intent(this, MapVolunteer.class));
                        finish();
                    }
                    return true;
                } else if (itemId == R.id.volunteer_profile) {
                    if (getSelectedNavItemId(role) != R.id.volunteer_profile) {
                        startActivity(new Intent(this, Profile.class));
                        finish();
                    }
                    return true;
                }
            } else {
                // Handle OKU-specific navigation
                if (itemId == R.id.nav_home) {
                    if (getSelectedNavItemId(role) != R.id.nav_home) {
                        startActivity(new Intent(this, MainActivity.class));
                        finish();
                    }
                    return true;
                } else if (itemId == R.id.nav_forum) {
                    if (getSelectedNavItemId(role) != R.id.nav_forum) {
                        startActivity(new Intent(this, ForumOKU.class));
                        finish();
                    }
                    return true;
                } else if (itemId == R.id.nav_activity) {
                    if (getSelectedNavItemId(role) != R.id.nav_activity) {
                        startActivity(new Intent(this, MapOKU.class));
                        finish();
                    }
                    return true;
                } else if (itemId == R.id.nav_profile) {
                    if (getSelectedNavItemId(role) != R.id.nav_profile) {
                        startActivity(new Intent(this, Profile.class));
                        finish();
                    }
                    return true;
                } else if (itemId == R.id.nav_emergency_contact) {
                    if (getSelectedNavItemId(role) != R.id.nav_emergency_contact) {
                        startActivity(new Intent(this, EmergencyHotlineActivity.class));
                        finish();
                    }
                    return true;
                }
            }
            return false;
        });
    }

    protected int getSelectedNavItemId(String role) {
        if ("volunteer".equals(role)) {
            return R.id.volunteer_home; // Default item for volunteer role
        } else {
            return R.id.nav_home; // Default item for OKU role
        }
    }
}
