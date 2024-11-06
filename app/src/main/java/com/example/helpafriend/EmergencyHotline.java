package com.example.helpafriend;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

public class EmergencyHotline extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_hotline);

        // First hotline button
        ImageButton callButton1 = findViewById(R.id.callButton1);
        callButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hotline number 1
                String hotlineNumber1 = "+603-7782 3603";
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:" + hotlineNumber1));
                startActivity(callIntent);
            }
        });

        // Second hotline button
        ImageButton callButton2 = findViewById(R.id.callButton2);
        callButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hotline number 2
                String hotlineNumber2 = "+603-2272 2677";
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:" + hotlineNumber2));
                startActivity(callIntent);
            }
        });

        // Third hotline button
        ImageButton callButton3 = findViewById(R.id.callButton3);
        callButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hotline number 3
                String hotlineNumber3 = "+603-8323 1656";
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:" + hotlineNumber3));
                startActivity(callIntent);
            }
        });

        // Fourth hotline button
        ImageButton callButton4 = findViewById(R.id.callButton4);
        callButton4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hotline number 4
                String hotlineNumber4 = "+603 8070 9308 ";
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:" + hotlineNumber4));
                startActivity(callIntent);
            }
        });
    }
}
