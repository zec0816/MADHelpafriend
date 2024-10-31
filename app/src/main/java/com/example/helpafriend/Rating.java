package com.example.helpafriend;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;



public class Rating extends AppCompatActivity {
    private ImageButton btn_noway, btn_poor, btn_ok, btn_verygood, btn_loving;
    private Button btnSubmit;
    private int selectedRating = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);

        // Initialize all buttons
        btn_noway = findViewById(R.id.btn_noway);
        btn_poor = findViewById(R.id.btn_poor);
        btn_ok = findViewById(R.id.btn_ok);
        btn_verygood = findViewById(R.id.btn_verygood);
        btn_loving = findViewById(R.id.btn_loving);
        btnSubmit = findViewById(R.id.btnSubmit);

        // Disable submit button initially
        btnSubmit.setEnabled(false);

        // Set click listeners for rating buttons
        btn_noway.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRating(1);
            }
        });

        btn_poor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRating(2);
            }
        });

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRating(3);
            }
        });

        btn_verygood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRating(4);
            }
        });

        btn_loving.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRating(5);
            }
        });

        // Submit button click listener
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedRating > 0) {
                    Toast.makeText(Rating.this,
                            "Thank you for your rating!",
                            Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    private void setRating(int rating) {
        selectedRating = rating;
        btnSubmit.setEnabled(true);

        // Reset all buttons to default state
        btn_noway.setAlpha(0.5f);
        btn_poor.setAlpha(0.5f);
        btn_ok.setAlpha(0.5f);
        btn_verygood.setAlpha(0.5f);
        btn_loving.setAlpha(0.5f);

        // Highlight selected button
        switch (rating) {
            case 1:
                btn_noway.setAlpha(1.0f);
                break;
            case 2:
                btn_poor.setAlpha(1.0f);
                break;
            case 3:
                btn_ok.setAlpha(1.0f);
                break;
            case 4:
                btn_verygood.setAlpha(1.0f);
                break;
            case 5:
                btn_loving.setAlpha(1.0f);
                break;
        }
    }
}