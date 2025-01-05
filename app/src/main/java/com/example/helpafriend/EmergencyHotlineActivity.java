package com.example.helpafriend;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageButton;


import java.util.Locale;


public class EmergencyHotlineActivity extends BaseActivity {
    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_hotline);
        sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String localeCode = sharedPreferences.getString("appLocale", "en");

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

        applySelectedLanguage();
    }

    private void applySelectedLanguage() {
        String savedLocale = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                .getString("appLocale", "en");
        Locale locale = new Locale(savedLocale);
        Locale.setDefault(locale);

        Configuration config = new Configuration();
        config.setLocale(locale);
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
    }

    @Override
    protected int getSelectedNavItemId(String role) {
        if ("volunteer".equals(role)) {
            return R.id.volunteer_home; // Default item for volunteer role
        } else {
            return R.id.nav_emergency_contact; // Correct ID for Emergency Hotline in OKU role
        }
    }

}
