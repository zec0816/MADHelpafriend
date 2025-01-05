package com.example.helpafriend;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.pm.PackageManager;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;

public class Profile extends BaseActivity {
    private static final int CAMERA_REQUEST_CODE = 1001;
    private static final int GALLERY_REQUEST_CODE = 1002;
    private static final int REQUEST_PERMISSION = 2000;
    private static final String PREFS_NAME = "MyAppPrefs";

    private ImageView profileImage;
    private EditText usernameEditText;

    private boolean isReadingAloud = false;
    private ImageButton btnEditProfile, btnSettings, btnHelpSupport, btnDonate;

    private Uri currentPhotoUri;
    private Bitmap currentBitmap;
    private SharedPreferences preferences;
    private String currentUsername;
    private String currentRole;

    private TextToSpeech tts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize preferences
        preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String Locale = preferences.getString("appLocale", "en");

        // Get current user information from SharedPreferences
        currentUsername = preferences.getString("username", null);
        currentRole = preferences.getString("role", null);

        // Check if user is logged in
        if (currentUsername == null || currentRole == null) {
            Toast.makeText(this, "Please log in first", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, Login.class));
            finish();
            return;
        }



        // Initialize views
        initializeViews();

        // Load user data
        loadUserData();

        // Set up click listeners
        setupClickListeners();
        setupBottomNavigation();
        applySelectedLanguage();
    }

    private void applySelectedLanguage() {
        String savedLocale = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                .getString("appLocale", "en");
        Locale locale = new Locale(savedLocale);
        Locale.setDefault(locale);

        Configuration config = new Configuration();
        config.setLocale(locale);
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
    }



    private void readAloudForumContent() {
        String forumContent = "Welcome to the Profile Page."; // Content to be spoken
        if (tts != null) {
            tts.speak(forumContent, TextToSpeech.QUEUE_FLUSH, null, null);
        } else {
            Log.e("TTS", "TTS is not initialized.");
        }
    }

    private void stopTTS() {
        if (tts != null && tts.isSpeaking()) {
            tts.stop();
            Log.d("TTS", "Text-to-Speech stopped");
        }
    }


    private void initializeViews() {
        profileImage = findViewById(R.id.profileImage);
        usernameEditText = findViewById(R.id.usernameEditText);
        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnSettings = findViewById(R.id.btnSettings);
        btnHelpSupport = findViewById(R.id.btnHelpSupport);
        btnDonate = findViewById(R.id.btnDonate);
    }

    private void loadUserData() {
        // Set username
        usernameEditText.setText(currentUsername);

        // Load profile image
        loadProfileImage();
    }

    private void setupClickListeners() {
        profileImage.setOnClickListener(this::handleProfileImageClick);

        btnEditProfile.setOnClickListener(v -> showPasswordVerificationDialog());

        btnSettings.setOnClickListener(v -> {
            Intent intent = new Intent(Profile.this, SettingsProfile.class);
            startActivity(intent);
        });

        btnHelpSupport.setOnClickListener(v -> {
            Intent intent = new Intent(Profile.this, HelpSupportProfile.class);
            startActivity(intent);
        });

        btnDonate.setOnClickListener(v -> {
            Intent intent = new Intent(Profile.this, DonateProfile.class);
            startActivity(intent);
        });
    }

    private void loadProfileImage() {
        // Get user-specific image file
        File imageFile = getUserProfileImageFile();

        if (imageFile.exists()) {
            try {
                currentPhotoUri = Uri.fromFile(imageFile);
                profileImage.setImageURI(currentPhotoUri);
                return;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // If no user-specific image exists, try to get from SharedPreferences
        String profileImageUri = preferences.getString("profile_image_uri_" + currentUsername, null);
        if (profileImageUri != null) {
            try {
                currentPhotoUri = Uri.parse(profileImageUri);
                profileImage.setImageURI(currentPhotoUri);
                // Copy external image to internal storage for persistence
                copyUriToInternalStorage(currentPhotoUri);
            } catch (Exception e) {
                e.printStackTrace();
                setDefaultImage();
            }
        } else {
            setDefaultImage();
        }
    }

    private File getUserProfileImageFile() {
        // Create a username-specific filename
        return new File(getFilesDir(), "profile_image_" + currentUsername + ".jpg");
    }

    private void setDefaultImage() {
        profileImage.setImageResource(R.drawable.man);
        currentPhotoUri = null;
        currentBitmap = null;
    }

    private void copyUriToInternalStorage(Uri sourceUri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), sourceUri);
            saveImageToStorage(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showPasswordVerificationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_verify_password, null);
        EditText passwordEditText = dialogView.findViewById(R.id.passwordEditText);

        builder.setView(dialogView)
                .setTitle(getString(R.string.verify_password_title)) // Use string resource for title
                .setPositiveButton(getString(R.string.verify_button), null) // Use string resource for "Verify"
                .setNegativeButton(getString(R.string.cancel_button), (dialog, which) -> dialog.dismiss()); // Use string resource for "Cancel"

        AlertDialog dialog = builder.create();
        passwordEditText.requestFocus();

        dialog.setOnShowListener(dialogInterface -> {
            Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            button.setOnClickListener(view -> {
                String password = passwordEditText.getText().toString().trim();
                if (password.isEmpty()) {
                    passwordEditText.setError(getString(R.string.password_required_error)); // Use string resource for error message
                    return;
                }
                verifyPassword(password, dialog);
            });
        });

        dialog.show();
    }

    private void verifyPassword(String password, Dialog dialog) {
        String storedPassword = preferences.getString("password", "");

        if (storedPassword.equals(password)) {
            dialog.dismiss();
            Intent intent = new Intent(Profile.this, EditProfile.class);
            startActivity(intent);
        } else {
            Toast.makeText(Profile.this, "Invalid password", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleProfileImageClick(View view) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            showImageOptionsDialog();
        } else {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.CAMERA},
                    REQUEST_PERMISSION
            );
        }
    }

    private void showImageOptionsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Change Profile Image")
                .setItems(new CharSequence[]{"Take Photo", "Choose from Gallery"}, (dialog, which) -> {
                    if (which == 0) {
                        openCamera();
                    } else {
                        openGallery();
                    }
                })
                .show();
    }

    private void openGallery() {
        Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhotoIntent, GALLERY_REQUEST_CODE);
    }

    private void openCamera() {
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePhotoIntent, CAMERA_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == GALLERY_REQUEST_CODE && data != null) {
                Uri selectedImageUri = data.getData();
                currentPhotoUri = selectedImageUri;
                profileImage.setImageURI(selectedImageUri);
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                    currentBitmap = bitmap;
                    uploadProfileImage();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Failed to process image", Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == CAMERA_REQUEST_CODE && data != null) {
                currentBitmap = (Bitmap) data.getExtras().get("data");
                profileImage.setImageBitmap(currentBitmap);
                uploadProfileImage();
            }
        }
    }

    private void uploadProfileImage() {
        if (currentBitmap != null) {
            // Save the image in internal storage
            saveImageToStorage(currentBitmap);

            // Update SharedPreferences with the internal storage URI
            File savedFile = getUserProfileImageFile();
            currentPhotoUri = Uri.fromFile(savedFile);

            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("profile_image_uri_" + currentUsername, currentPhotoUri.toString());
            editor.apply();

            Toast.makeText(this, "Profile photo updated!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No image to upload", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveImageToStorage(Bitmap bitmap) {
        try {
            FileOutputStream outputStream = new FileOutputStream(getUserProfileImageFile());
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showImageOptionsDialog();
            } else {
                Toast.makeText(this, "Camera permission is required", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected int getSelectedNavItemId(String role) {
        if ("volunteer".equals(role)) {
            return R.id.volunteer_profile;
        } else {
            return R.id.nav_profile;
        }
    }
}