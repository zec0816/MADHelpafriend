package com.example.helpafriend;

import android.content.Intent;
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

        bottomNavigationView.setSelectedItemId(getSelectedNavItemId());

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                if (getSelectedNavItemId() != R.id.nav_home) {
                    startActivity(new Intent(this,MainActivity.class));
                    finish();
                }
                return true;
            } else if (itemId == R.id.nav_forum) {
                if (getSelectedNavItemId() != R.id.nav_forum) {
                    startActivity(new Intent(this, ForumOKU.class));
                    finish();
                }
                return true;
            } else if (itemId == R.id.nav_activity) {
                if (getSelectedNavItemId() != R.id.nav_activity) {
                    startActivity(new Intent(this, MapOKU.class));
                    finish();
                }
                return true;
            } else if (itemId == R.id.nav_profile) {
                if (getSelectedNavItemId() != R.id.nav_profile) {
                    startActivity(new Intent(this, Profile.class));
                    finish();
                }
                return true;
            }else if (itemId == R.id.nav_emergency_contact) {
                if (getSelectedNavItemId() != R.id.nav_emergency_contact) {
                    startActivity(new Intent(this, EmergencyHotlineActivity.class));
                    finish();
                }
                return true;
            }
            return false;
        });
    }

    protected int getSelectedNavItemId() {
        return R.id.nav_home;
    }
}
