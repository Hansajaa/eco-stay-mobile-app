package com.example.ecostay;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Retrieve user data from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        String fullName = sharedPreferences.getString("fullName", "Guest");

        // Extract first name
        String firstName = fullName;
        if (fullName != null && fullName.trim().contains(" ")) {
            firstName = fullName.split(" ")[0];
        }

        TextView tvWelcome = findViewById(R.id.tvWelcomeBack);
        if (tvWelcome != null) {
            tvWelcome.setText("Welcome,\n" + firstName);
        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.navigation_home);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.navigation_home) {
                    return true;
                } else if (id == R.id.navigation_explore) {
                    startActivity(new Intent(HomeActivity.this, ExploreActivity.class));
                    finish();
                    return true;
                } else if (id == R.id.navigation_activity) {
                    startActivity(new Intent(HomeActivity.this, ActivitiesActivity.class));
                    finish();
                    return true;
                } else if (id == R.id.navigation_profile) {
                    startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
                    finish();
                    return true;
                }
                return false;
            }
        });
    }
}