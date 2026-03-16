package com.example.ecostay;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class PreferencesActivity extends AppCompatActivity {

    private EditText etDietary, etRoomTemp;
    private AutoCompleteTextView actvPillowType;
    private Button btnSave;
    private ImageButton btnBack;
    private DatabaseHelper dbHelper;
    private int userId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);

        dbHelper = new DatabaseHelper(this);
        etDietary = findViewById(R.id.etDietary);
        etRoomTemp = findViewById(R.id.etRoomTemp);
        actvPillowType = findViewById(R.id.actvPillowType);
        btnSave = findViewById(R.id.btnSavePreferences);
        btnBack = findViewById(R.id.btnBack);

        // Setup Dropdown for Pillow Type
        String[] pillowTypes = new String[]{"Memory Foam", "Feather", "Down", "Latex", "Buckwheat"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, pillowTypes);
        actvPillowType.setAdapter(adapter);

        // Load current user ID
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        String email = sharedPreferences.getString("email", null);
        if (email != null) {
            userId = getUserIdByEmail(email);
            loadPreferences();
        }

        btnBack.setOnClickListener(v -> finish());

        btnSave.setOnClickListener(v -> savePreferences());
    }

    private int getUserIdByEmail(String email) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_USER, new String[]{DatabaseHelper.COLUMN_ID},
                DatabaseHelper.COLUMN_EMAIL + "=?", new String[]{email}, null, null, null);
        int id = -1;
        if (cursor.moveToFirst()) {
            id = cursor.getInt(0);
        }
        cursor.close();
        return id;
    }

    private void loadPreferences() {
        if (userId == -1) return;

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_PREFERENCES, null,
                DatabaseHelper.COLUMN_PREF_USER_ID + "=?", new String[]{String.valueOf(userId)}, null, null, null);

        if (cursor.moveToFirst()) {
            etDietary.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PREF_DIETARY)));
            etRoomTemp.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PREF_TEMP)));
            actvPillowType.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PREF_PILLOW)), false);
        }
        cursor.close();
    }

    private void savePreferences() {
        if (userId == -1) return;

        String dietary = etDietary.getText().toString().trim();
        String temp = etRoomTemp.getText().toString().trim();
        String pillow = actvPillowType.getText().toString().trim();

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_PREF_USER_ID, userId);
        values.put(DatabaseHelper.COLUMN_PREF_DIETARY, dietary);
        values.put(DatabaseHelper.COLUMN_PREF_TEMP, temp);
        values.put(DatabaseHelper.COLUMN_PREF_PILLOW, pillow);

        long result = db.insertWithOnConflict(DatabaseHelper.TABLE_PREFERENCES, null, values, SQLiteDatabase.CONFLICT_REPLACE);

        if (result != -1) {
            Toast.makeText(this, "Preferences updated successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to update preferences", Toast.LENGTH_SHORT).show();
        }
    }
}