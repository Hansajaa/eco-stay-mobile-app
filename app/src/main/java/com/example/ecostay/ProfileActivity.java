package com.example.ecostay;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvProfileName, tvHeaderTitle, tvProfileEmail;
    private ImageButton btnEditProfile;
    private LinearLayout llTravelPreferences, llPastStays, llActivitiesBooked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        tvProfileName = findViewById(R.id.tvProfileName);
        tvHeaderTitle = findViewById(R.id.tvHeaderTitle);
        tvProfileEmail = findViewById(R.id.tvProfileEmail);
        btnEditProfile = findViewById(R.id.btnEditProfile);
        llTravelPreferences = findViewById(R.id.llTravelPreferences);
        llPastStays = findViewById(R.id.llPastStays);
        llActivitiesBooked = findViewById(R.id.llActivitiesBooked);

        loadUserData();
        setupBottomNavigation();

        LinearLayout llSignOut = findViewById(R.id.llSignOut);
        llSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        btnEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
            startActivity(intent);
        });

        llTravelPreferences.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, PreferencesActivity.class);
            startActivity(intent);
        });

        llPastStays.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, PastStaysActivity.class);
            startActivity(intent);
        });

        llActivitiesBooked.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, BookedActivitiesActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserData(); // Refresh data if updated
    }

    private void loadUserData() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        String fullName = sharedPreferences.getString("fullName", "Guest");
        String email = sharedPreferences.getString("email", "guest@ecostay.com");
        
        if (tvProfileName != null) {
            tvProfileName.setText(fullName);
        }
        if (tvHeaderTitle != null) {
            tvHeaderTitle.setText(fullName.split(" ")[0] + "'s Retreat");
        }
        if (tvProfileEmail != null) {
            tvProfileEmail.setText(email);
        }
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.navigation_profile);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.navigation_home) {
                startActivity(new Intent(ProfileActivity.this, HomeActivity.class));
                finish();
                return true;
            } else if (id == R.id.navigation_explore) {
                startActivity(new Intent(ProfileActivity.this, ExploreActivity.class));
                finish();
                return true;
            } else if (id == R.id.navigation_activity) {
                startActivity(new Intent(ProfileActivity.this, ActivitiesActivity.class));
                finish();
                return true;
            } else if (id == R.id.navigation_profile) {
                return true;
            }
            return false;
        });
    }
}