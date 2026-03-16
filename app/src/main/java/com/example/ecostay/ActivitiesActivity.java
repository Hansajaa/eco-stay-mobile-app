package com.example.ecostay;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.navigation.NavigationBarView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ActivitiesActivity extends AppCompatActivity {

    private RecyclerView rvActivities;
    private ActivityAdapter activityAdapter;
    private List<ActivityModel> allActivities;
    private List<ActivityModel> filteredActivities;
    private ChipGroup chipGroupActivityType;
    
    private Button btnSelectDate;
    private TextView tvSelectedDate;
    private String selectedDate = "";
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activities);

        dbHelper = new DatabaseHelper(this);
        btnSelectDate = findViewById(R.id.btnSelectDate);
        tvSelectedDate = findViewById(R.id.tvSelectedDate);
        chipGroupActivityType = findViewById(R.id.chipGroupActivityType);
        rvActivities = findViewById(R.id.rvActivities);
        rvActivities.setLayoutManager(new LinearLayoutManager(this));

        loadActivities();

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        btnSelectDate.setOnClickListener(v -> {
            // Create calendar constraints to disable past dates
            CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder();
            // Set start date to today (disable past dates)
            Calendar today = Calendar.getInstance();
            constraintsBuilder.setStart(today.getTimeInMillis());
            // Only allow dates from today onwards
            constraintsBuilder.setValidator(com.google.android.material.datepicker.DateValidatorPointForward.now());

            MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Select Date")
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .setCalendarConstraints(constraintsBuilder.build())
                    .build();

            datePicker.show(getSupportFragmentManager(), "DATE_PICKER");

            datePicker.addOnPositiveButtonClickListener(selection -> {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                selectedDate = sdf.format(new Date(selection));
                tvSelectedDate.setText("Selected Date: " + selectedDate);
                applyFilters();
            });
        });

        chipGroupActivityType.setOnCheckedStateChangeListener((group, checkedIds) -> applyFilters());

        setupBottomNavigation();
    }

    private void loadActivities() {
        allActivities = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_ACTIVITY, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACTIVITY_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACTIVITY_NAME));
                String desc = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACTIVITY_DESCRIPTION));
                String price = "$" + (int)cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACTIVITY_PRICE));
                String duration = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACTIVITY_DURATION));
                String startTime = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACTIVITY_START_TIME));
                String category = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACTIVITY_CATEGORY));
                
                allActivities.add(new ActivityModel(id, name, desc, price, startTime, duration, category, R.drawable.login_page));
            } while (cursor.moveToNext());
        }
        cursor.close();

        filteredActivities = new ArrayList<>(allActivities);
        activityAdapter = new ActivityAdapter(filteredActivities);
        rvActivities.setAdapter(activityAdapter);
        
        activityAdapter.setOnBookNowClickListener(activity -> {
            if (selectedDate.isEmpty()) {
                Toast.makeText(this, "Please select a date first", Toast.LENGTH_SHORT).show();
                return;
            }
            showBookingConfirmationDialog(activity);
        });
    }

    private void showBookingConfirmationDialog(ActivityModel activity) {
        new AlertDialog.Builder(this)
                .setTitle("Confirm Booking")
                .setMessage("Do you want to book " + activity.getName() + " for " + selectedDate + "?")
                .setPositiveButton("Confirm", (dialog, which) -> bookActivity(activity))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void bookActivity(ActivityModel activity) {
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        String email = sharedPreferences.getString("email", null);
        if (email == null) return;

        int userId = getUserIdByEmail(email);
        if (userId != -1) {
            if (isAlreadyBooked(userId, activity.getActivityId(), selectedDate)) {
                Toast.makeText(this, "You have already booked this activity for this date.", Toast.LENGTH_SHORT).show();
                return;
            }

            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_ACT_BOOKING_USER_ID, userId);
            values.put(DatabaseHelper.COLUMN_ACT_BOOKING_ACTIVITY_ID, activity.getActivityId());
            values.put(DatabaseHelper.COLUMN_ACT_BOOKING_DATE, selectedDate);

            long result = db.insert(DatabaseHelper.TABLE_ACTIVITY_BOOKING, null, values);
            if (result != -1) {
                Toast.makeText(this, "Activity booked successfully!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Booking failed.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean isAlreadyBooked(int userId, int activityId, String date) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_ACTIVITY_BOOKING, null,
                DatabaseHelper.COLUMN_ACT_BOOKING_USER_ID + "=? AND " +
                        DatabaseHelper.COLUMN_ACT_BOOKING_ACTIVITY_ID + "=? AND " +
                        DatabaseHelper.COLUMN_ACT_BOOKING_DATE + "=?",
                new String[]{String.valueOf(userId), String.valueOf(activityId), date}, null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    private int getUserIdByEmail(String email) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_USER, new String[]{DatabaseHelper.COLUMN_ID},
                DatabaseHelper.COLUMN_EMAIL + "=?", new String[]{email}, null, null, null);
        int userId = -1;
        if (cursor.moveToFirst()) {
            userId = cursor.getInt(0);
        }
        cursor.close();
        return userId;
    }

    private void applyFilters() {
        int checkedId = chipGroupActivityType.getCheckedChipId();
        String category = "";
        if (checkedId == R.id.chipHiking) category = "Hiking";
        else if (checkedId == R.id.chipWorkshops) category = "Workshops";
        else if (checkedId == R.id.chipRelaxation) category = "Relaxation";

        List<ActivityModel> temp = new ArrayList<>();
        for (ActivityModel activity : allActivities) {
            if (category.isEmpty() || activity.getCategory().equals(category)) {
                temp.add(activity);
            }
        }
        activityAdapter.updateList(temp);
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.navigation_activity);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.navigation_home) {
                    startActivity(new Intent(ActivitiesActivity.this, HomeActivity.class));
                    finish();
                    return true;
                } else if (id == R.id.navigation_explore) {
                    startActivity(new Intent(ActivitiesActivity.this, ExploreActivity.class));
                    finish();
                    return true;
                } else if (id == R.id.navigation_activity) {
                    return true;
                } else if (id == R.id.navigation_profile) {
                    startActivity(new Intent(ActivitiesActivity.this, ProfileActivity.class));
                    finish();
                    return true;
                }
                return false;
            }
        });
    }
}