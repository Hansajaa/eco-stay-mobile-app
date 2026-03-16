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

public class PastStaysActivity extends AppCompatActivity {

    private RecyclerView rvPastStays;
    private TextView tvNoStays;
    private ImageButton btnBack;
    private DatabaseHelper dbHelper;
    private PastStaysAdapter adapter;
    private List<PastStay> stayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_stays);

        dbHelper = new DatabaseHelper(this);
        rvPastStays = findViewById(R.id.rvPastStays);
        tvNoStays = findViewById(R.id.tvNoStays);
        btnBack = findViewById(R.id.btnBack);

        rvPastStays.setLayoutManager(new LinearLayoutManager(this));
        stayList = new ArrayList<>();
        adapter = new PastStaysAdapter(stayList);
        rvPastStays.setAdapter(adapter);

        btnBack.setOnClickListener(v -> finish());

        loadPastStays();
    }

    private void loadPastStays() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        String email = sharedPreferences.getString("email", null);
        if (email == null) return;

        int userId = dbHelper.getUserIdByEmail(email);
        if (userId != -1) {
            String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            Cursor cursor = dbHelper.getPastStays(userId, currentDate);
            
            stayList.clear();
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String roomName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ROOM_NAME));
                    String checkIn = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BOOKING_CHECK_IN));
                    String checkOut = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BOOKING_CHECK_OUT));
                    double price = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ROOM_PRICE));

                    // Calculate nights between check-in and check-out
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    int nights = 1;
                    try {
                        Date checkInDate = sdf.parse(checkIn);
                        Date checkOutDate = sdf.parse(checkOut);
                        if (checkInDate != null && checkOutDate != null) {
                            long diffMillis = checkOutDate.getTime() - checkInDate.getTime();
                            nights = (int) Math.max(1, diffMillis / (1000 * 60 * 60 * 24));
                        }
                    } catch (Exception e) {
                        nights = 1;
                    }

                    String dates = checkIn + " to " + checkOut;
                    String totalPrice = "$" + (int)(price * nights) + " (" + nights + " nights)";

                    stayList.add(new PastStay(roomName, dates, totalPrice));
                } while (cursor.moveToNext());
                
                tvNoStays.setVisibility(View.GONE);
                rvPastStays.setVisibility(View.VISIBLE);
            } else {
                tvNoStays.setVisibility(View.VISIBLE);
                rvPastStays.setVisibility(View.GONE);
            }
            if (cursor != null) cursor.close();
            adapter.notifyDataSetChanged();
        }
    }
}