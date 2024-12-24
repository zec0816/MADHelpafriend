package com.example.helpafriend;


import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class DonateProfile extends AppCompatActivity {

    private EditText etDonationAmount;
    private Spinner spPaymentMethod;
    private Button btnDonate;
    private Button btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donate);

        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(view -> {
            finish();
        });

        // Initialize UI components
        etDonationAmount = findViewById(R.id.etDonationAmount);
        spPaymentMethod = findViewById(R.id.spPaymentMethod);
        btnDonate = findViewById(R.id.btnDonate);
        btnCancel = findViewById(R.id.btnCancel);

        // Populate Spinner with payment methods
        String[] paymentMethods = {"Credit Card", "PayPal", "Google Pay", "Apple Pay"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                paymentMethods
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPaymentMethod.setAdapter(adapter);

        // Set click listener for Donate button
        btnDonate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleDonation();
            }
        });

        // Set click listener for Cancel button
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Close the activity
            }
        });
    }

    /**
     * Handles the donation process when the Donate button is clicked.
     */
    private void handleDonation() {
        String amount = etDonationAmount.getText().toString().trim();
        String paymentMethod = spPaymentMethod.getSelectedItem().toString();

        if (amount.isEmpty()) {
            Toast.makeText(this, "Please enter a donation amount.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double donationAmount = Double.parseDouble(amount);
            if (donationAmount <= 0) {
                Toast.makeText(this, "Please enter a valid donation amount.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Simulate a donation process
            String message = "Thank you for your donation of $" + donationAmount + " via " + paymentMethod + "!";
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();

            // Clear the fields after successful donation
            etDonationAmount.setText("");
            spPaymentMethod.setSelection(0);

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid amount format. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }
}

