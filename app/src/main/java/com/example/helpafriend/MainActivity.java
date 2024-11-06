package com.example.helpafriend;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    ImageButton forumImageButton, emergencyHotlineButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);  // Make sure the layout is correct

        // Find the forum ImageButton
        forumImageButton = findViewById(R.id.forumImageButton);
        emergencyHotlineButton = findViewById(R.id.emergencyHotlineButton);

        // Set an OnClickListener for Forum button
        forumImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Add a Toast message for debugging
                Toast.makeText(MainActivity.this, "Forum button clicked", Toast.LENGTH_SHORT).show();

                // Navigate to Forum activity
                try {
                    Intent intent = new Intent(MainActivity.this, Forum.class);
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

        // Set an OnClickListener for Emergency Hotline button
        emergencyHotlineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Add a Toast message for debugging
                Toast.makeText(MainActivity.this, "Emergency Hotline button clicked", Toast.LENGTH_SHORT).show();

                // Navigate to Emergency Hotline activity
                try {
                    Intent intent = new Intent(MainActivity.this, EmergencyHotlineActivity.class);
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
