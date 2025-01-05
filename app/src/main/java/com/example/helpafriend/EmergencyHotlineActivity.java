package com.example.helpafriend;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;


import java.util.Locale;


public class EmergencyHotlineActivity extends BaseActivity {
    private SharedPreferences sharedPreferences;


    private TextToSpeech tts;
    private boolean isReadingAloud = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_hotline);
        sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        
        // Set up BottomNavigationView
        setupBottomNavigation();

        // Read Aloud Button
        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int result = tts.setLanguage(Locale.US);
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TTS", "Language not supported or missing data.");
                }
            } else {
                Log.e("TTS", "Initialization failed.");
            }
        });

        // Read Aloud Button
        Button readAloudButton = findViewById(R.id.TTSButton);
        readAloudButton.setOnClickListener(view -> {
            if (isReadingAloud) {
                stopTTS(); // Stop TTS if already speaking
            } else {
                readAloudEmergencyContent(); // Start reading aloud
            }
            isReadingAloud = !isReadingAloud; // Toggle the state
        });


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

    private void readAloudEmergencyContent() {
        // Fetch the content to read aloud
        String emergencyInfo = "Welcome to the Emergency Hotline Page. " +
                "First contact, Jabatan Kebajikan Masyarakat, hotline plus 603 8323 1656. " +
                "Second contact, Malaysian Association for the Blind, hotline plus 603 2272 2677. " +
                "Third contact, Malaysian Association of the Deaf, hotline plus 603 8070 9308. " +
                "Fourth contact, Persatuan Damai OKU Malaysia, hotline plus 603 7782 3603.";

        if (tts != null) {
            tts.speak(emergencyInfo, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }

    private void stopTTS() {
        if (tts != null && tts.isSpeaking()) {
            tts.stop();
            Toast.makeText(this, "Text-to-Speech stopped", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
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
