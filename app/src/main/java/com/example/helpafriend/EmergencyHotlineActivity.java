package com.example.helpafriend;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageButton;

public class EmergencyHotlineActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_hotline);

        // Set up BottomNavigationView
        setupBottomNavigation();

        // First hotline button
        ImageButton callButton1 = findViewById(R.id.callButton1);
        callButton1.setOnClickListener(view -> {
            String hotlineNumber1 = "+603-8323 1656";
            Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + hotlineNumber1));
            startActivity(callIntent);
        });

        // Second hotline button
        ImageButton callButton2 = findViewById(R.id.callButton2);
        callButton2.setOnClickListener(view -> {
            String hotlineNumber2 = "+603-2272 2677";
            Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + hotlineNumber2));
            startActivity(callIntent);
        });

        // Third hotline button
        ImageButton callButton3 = findViewById(R.id.callButton3);
        callButton3.setOnClickListener(view -> {
            String hotlineNumber3 = "+603-8070 9308";
            Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + hotlineNumber3));
            startActivity(callIntent);
        });

        // Fourth hotline button
        ImageButton callButton4 = findViewById(R.id.callButton4);
        callButton4.setOnClickListener(view -> {
            String hotlineNumber4 = "+603-7782 3603";
            Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + hotlineNumber4));
            startActivity(callIntent);
        });
    }

    @Override
    protected int getSelectedNavItemId() {
        return R.id.nav_emergency_contact; // Replace with the correct ID for this activity in your menu file
    }
}
