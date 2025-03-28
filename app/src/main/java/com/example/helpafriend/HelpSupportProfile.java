package com.example.helpafriend;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class HelpSupportProfile extends AppCompatActivity {

    private ImageButton backButton;
    private Button btnContactSupport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_help_support);

        backButton = findViewById(R.id.backButton);
        btnContactSupport = findViewById(R.id.btnContactSupport);

        backButton.setOnClickListener(view -> finish());
        btnContactSupport.setOnClickListener(view -> openGmail());
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

    private void openGmail() {
        String recipient = "support@helpAfriend.com";
        String subject = "Support Request";
        String body = "Dear Support Team,\n\nPlease describe your issue here...\n\n---";

        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{recipient});
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, body);

        try {
            startActivity(Intent.createChooser(intent, "Send email..."));
        } catch (Exception e) {
            Toast.makeText(this, "No email application found!", Toast.LENGTH_SHORT).show();
        }
    }
}
