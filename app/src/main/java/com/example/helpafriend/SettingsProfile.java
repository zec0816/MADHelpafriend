package com.example.helpafriend;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import android.app.AlertDialog;
import android.view.ViewGroup;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class SettingsProfile extends AppCompatActivity {
    private Switch darkModeSwitch;
    private Switch notificationSwitch;
    private TextView languageSelection;
    private TextView fontSizeSelection;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private View rootLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
            editor = sharedPreferences.edit();

            String localeCode = sharedPreferences.getString("appLocale", "en");
            setLocaleWithoutRestart(localeCode);

            setContentView(R.layout.activity_edit_settings);

            initializeViews();
            loadSettings();
            setupClickListeners();
            setupSwitch();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error initializing settings: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void initializeViews() {
        rootLayout = findViewById(R.id.settingsLayout);
        darkModeSwitch = findViewById(R.id.darkModeSwitch);
        notificationSwitch = findViewById(R.id.notificationSwitch);
        languageSelection = findViewById(R.id.languageSelection);
        fontSizeSelection = findViewById(R.id.fontSizeSelection);
    }

    private void loadSettings() {
        float savedFontSize = sharedPreferences.getFloat("fontSize", 16f);
        applyFontSize((ViewGroup) rootLayout, savedFontSize);
        fontSizeSelection.setTextSize(TypedValue.COMPLEX_UNIT_SP, savedFontSize);

        String selectedLanguage = getCurrentLanguageName();
        languageSelection.setText(selectedLanguage);

        String selectedFontSize = getFontSizeName(savedFontSize);
        fontSizeSelection.setText(selectedFontSize);

        boolean isDarkModeOn = sharedPreferences.getBoolean("isDarkModeOn", false);
        darkModeSwitch.setChecked(isDarkModeOn);
        updateBackgroundColor(isDarkModeOn);

        AppCompatDelegate.setDefaultNightMode(
                isDarkModeOn ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );
    }

    private void setupClickListeners() {
        ImageView backButton = findViewById(R.id.imageView);
        if (backButton != null) {
            backButton.setOnClickListener(v -> onBackPressed());
        }

        findViewById(R.id.logoutOption).setOnClickListener(this::onLogoutClicked);
        findViewById(R.id.deleteAccountOption).setOnClickListener(this::onDeleteAccountClicked);

        languageSelection.setOnClickListener(v -> showLanguageSelectionDialog());
        fontSizeSelection.setOnClickListener(v -> showFontSizeSelectionDialog());
    }

    private void setupSwitch() {
        darkModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            AppCompatDelegate.setDefaultNightMode(
                    isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
            );
            editor.putBoolean("isDarkModeOn", isChecked);
            editor.apply();
            updateBackgroundColor(isChecked);

            String message = getString(isChecked ? R.string.dark_mode_enabled : R.string.dark_mode_disabled);
            Toast.makeText(SettingsProfile.this, message, Toast.LENGTH_SHORT).show();
        });
        notificationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            String message = isChecked ? "Notifications ON" : "Notifications OFF";
            Toast.makeText(SettingsProfile.this, message, Toast.LENGTH_SHORT).show();
        });
    }

    private void showLanguageSelectionDialog() {
        String[] languages = {"English", "中文", "Bahasa Melayu", "हिंदी"};
        String[] localeCodes = {"en", "zh", "ms", "hi"};

        String currentLocale = sharedPreferences.getString("appLocale", "en");
        int checkedItem = 0;
        for (int i = 0; i < localeCodes.length; i++) {
            if (localeCodes[i].equals(currentLocale)) {
                checkedItem = i;
                break;
            }
        }

        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.select_language))
                .setSingleChoiceItems(languages, checkedItem, (dialog, which) -> {
                    setLocale(localeCodes[which]);
                    dialog.dismiss();
                })
                .setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void showFontSizeSelectionDialog() {
        String[] fontSizes = {
                getString(R.string.small),
                getString(R.string.medium),
                getString(R.string.large)
        };
        float[] sizeValues = {16f, 20f, 24f};

        int checkedItem = 0;
        float currentFontSize = sharedPreferences.getFloat("fontSize", 16f);
        for (int i = 0; i < sizeValues.length; i++) {
            if (sizeValues[i] == currentFontSize) {
                checkedItem = i;
                break;
            }
        }

        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.select_font_size))
                .setSingleChoiceItems(fontSizes, checkedItem, (dialog, which) -> {
                    float selectedFontSize = sizeValues[which];
                    editor.putFloat("fontSize", selectedFontSize);
                    editor.apply();

                    applyFontSize((ViewGroup) rootLayout, selectedFontSize);
                    fontSizeSelection.setText(fontSizes[which]);

                    Toast.makeText(this, fontSizes[which], Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                })
                .setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.dismiss())
                .show();
    }

    private String getCurrentLanguageName() {
        String localeCode = sharedPreferences.getString("appLocale", "en");
        switch (localeCode) {
            case "zh":
                return "中文";
            case "ms":
                return "Bahasa Melayu";
            case "hi":
                return "हिंदी";
            default:
                return "English";
        }
    }

    private String getFontSizeName(float fontSize) {
        if (fontSize == 16f) {
            return getString(R.string.small);
        } else if (fontSize == 20f) {
            return getString(R.string.medium);
        } else if (fontSize == 24f) {
            return getString(R.string.large);
        }
        return getString(R.string.medium); // Default
    }

    private void applyFontSize(ViewGroup root, float fontSize) {
        for (int i = 0; i < root.getChildCount(); i++) {
            View child = root.getChildAt(i);
            if (child instanceof TextView) {
                ((TextView) child).setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
            } else if (child instanceof ViewGroup) {
                applyFontSize((ViewGroup) child, fontSize);
            }
        }
    }

    private void setLocale(String localeCode) {
        java.util.Locale locale = new java.util.Locale(localeCode);
        java.util.Locale.setDefault(locale);

        android.content.res.Configuration config = new android.content.res.Configuration();
        config.setLocale(locale);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        editor.putString("appLocale", localeCode);
        editor.apply();

        recreate(); // Recreate the activity to apply changes
    }

    private void setLocaleWithoutRestart(String localeCode) {
        java.util.Locale locale = new java.util.Locale(localeCode);
        java.util.Locale.setDefault(locale);

        android.content.res.Configuration config = new android.content.res.Configuration();
        config.setLocale(locale);
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
    }

    private void updateBackgroundColor(boolean isDarkModeOn) {
        rootLayout.setBackgroundColor(isDarkModeOn ? Color.BLACK : Color.WHITE);
        // Update text colors for dark mode
        int textColor = isDarkModeOn ? Color.WHITE : Color.BLACK;
        languageSelection.setTextColor(textColor);
        fontSizeSelection.setTextColor(textColor);
    }

    private void onLogoutClicked(View view) {
        editor.clear();
        editor.apply();

        Intent intent = new Intent(SettingsProfile.this, Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void onDeleteAccountClicked(View view) {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.delete_account))
                .setMessage(getString(R.string.delete_account_confirm))
                .setPositiveButton(getString(R.string.yes), (dialog, which) -> deleteAccount())
                .setNegativeButton(getString(R.string.no), (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void deleteAccount() {
        String username = sharedPreferences.getString("username", "");
        Log.d("DeleteAccount", "Attempting to delete user: " + username);

        if (username.isEmpty()) {
            Toast.makeText(this, "Error: Username not found", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            try {
                // Create URL with parameters
                String encodedUsername = URLEncoder.encode(username, "UTF-8");
                URL url = new URL(Db_Contract.urlDeleteProfile + "?username=" + encodedUsername);
                Log.d("DeleteAccount", "Delete URL: " + url.toString());

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(10000);

                int responseCode = conn.getResponseCode();
                Log.d("DeleteAccount", "Response Code: " + responseCode);

                // Read the response
                StringBuilder response = new StringBuilder();
                try (BufferedReader in = new BufferedReader(
                        new InputStreamReader(conn.getInputStream()))) {
                    String line;
                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }
                }

                String responseStr = response.toString();
                Log.d("DeleteAccount", "Server Response: " + responseStr);

                try {
                    JSONObject jsonResponse = new JSONObject(responseStr);
                    String status = jsonResponse.getString("status");
                    String message = jsonResponse.optString("message", "Operation completed");

                    runOnUiThread(() -> {
                        Toast.makeText(SettingsProfile.this, message, Toast.LENGTH_SHORT).show();
                        if (status.equals("success")) {
                            // Clear preferences and redirect to login
                            editor.clear();
                            editor.apply();
                            Intent intent = new Intent(SettingsProfile.this, Login.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }
                    });
                } catch (JSONException e) {
                    Log.e("DeleteAccount", "JSON Error: " + e.getMessage());
                    runOnUiThread(() ->
                            Toast.makeText(SettingsProfile.this,
                                    "Error processing response", Toast.LENGTH_SHORT).show());
                }

            } catch (Exception e) {
                Log.e("DeleteAccount", "Error: " + e.getMessage());
                runOnUiThread(() ->
                        Toast.makeText(SettingsProfile.this,
                                "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
}
