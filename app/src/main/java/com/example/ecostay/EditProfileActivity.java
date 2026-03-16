package com.example.ecostay;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class EditProfileActivity extends AppCompatActivity {

    private EditText etFullName, etEmail, etNewPassword;
    private Button btnSave;
    private ImageButton btnBack;
    private DatabaseHelper dbHelper;
    private String currentEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        dbHelper = new DatabaseHelper(this);
        etFullName = findViewById(R.id.etEditFullName);
        etEmail = findViewById(R.id.etEditEmail);
        etNewPassword = findViewById(R.id.etEditNewPassword);
        btnSave = findViewById(R.id.btnSaveProfile);
        btnBack = findViewById(R.id.btnBack);

        // Load current data
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        String fullName = sharedPreferences.getString("fullName", "");
        currentEmail = sharedPreferences.getString("email", "");

        etFullName.setText(fullName);
        etEmail.setText(currentEmail);

        btnBack.setOnClickListener(v -> finish());

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile();
            }
        });
    }

    private void updateProfile() {
        String newName = etFullName.getText().toString().trim();
        String newPassword = etNewPassword.getText().toString().trim();

        if (newName.isEmpty()) {
            Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_FULLNAME, newName);

        if (!newPassword.isEmpty()) {
            values.put(DatabaseHelper.COLUMN_PASSWORD, hashPassword(newPassword));
        }

        int rows = db.update(DatabaseHelper.TABLE_USER, values, 
                DatabaseHelper.COLUMN_EMAIL + "=?", new String[]{currentEmail});

        if (rows > 0) {
            // Update Session
            SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("fullName", newName);
            editor.apply();

            Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show();
        }
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return password;
        }
    }
}