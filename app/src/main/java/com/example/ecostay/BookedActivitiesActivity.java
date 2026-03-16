package com.example.ecostay;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BookedActivitiesActivity extends AppCompatActivity {

    private RecyclerView rvBookedActivities;
    private TextView tvNoActivities;
    private ImageButton btnBack;
    private DatabaseHelper dbHelper;
    private BookedActivitiesAdapter adapter;
    private List<BookedActivity> activityList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booked_activities);

        dbHelper = new DatabaseHelper(this);
        rvBookedActivities = findViewById(R.id.rvBookedActivities);
        tvNoActivities = findViewById(R.id.tvNoActivities);
        btnBack = findViewById(R.id.btnBack);

        rvBookedActivities.setLayoutManager(new LinearLayoutManager(this));
        activityList = new ArrayList<>();
        adapter = new BookedActivitiesAdapter(activityList);
        rvBookedActivities.setAdapter(adapter);

        btnBack.setOnClickListener(v -> finish());

        loadBookedActivities();
    }

    private void loadBookedActivities() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        String email = sharedPreferences.getString("email", null);
        if (email == null) return;

        int userId = dbHelper.getUserIdByEmail(email);
        if (userId != -1) {
            String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            Cursor cursor = dbHelper.getUpcomingActivities(userId, currentDate);
            
            activityList.clear();
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACTIVITY_NAME));
                    String date = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACT_BOOKING_DATE));
                    String startTime = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACTIVITY_START_TIME));
                    double priceValue = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACTIVITY_PRICE));
                    String price = "$" + (int)priceValue;

                    activityList.add(new BookedActivity(name, date, startTime, price));
                } while (cursor.moveToNext());
                
                tvNoActivities.setVisibility(View.GONE);
                rvBookedActivities.setVisibility(View.VISIBLE);
            } else {
                tvNoActivities.setVisibility(View.VISIBLE);
                rvBookedActivities.setVisibility(View.GONE);
            }
            if (cursor != null) cursor.close();
            adapter.notifyDataSetChanged();
        }
    }
}