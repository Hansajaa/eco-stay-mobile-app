package com.example.ecostay;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Parcel;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;

import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BookingActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private TextView tvBookingRoomName, tvBookingRoomDetails, tvBookingRoomPrice, tvSelectedDates;
    private Button btnSelectDateRange, btnBookRoom;
    private String checkInDate, checkOutDate;
    private DatabaseHelper dbHelper;
    private Room room;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        dbHelper = new DatabaseHelper(this);
        btnBack = findViewById(R.id.btnBack);
        tvBookingRoomName = findViewById(R.id.tvBookingRoomName);
        tvBookingRoomDetails = findViewById(R.id.tvBookingRoomDetails);
        tvBookingRoomPrice = findViewById(R.id.tvBookingRoomPrice);
        tvSelectedDates = findViewById(R.id.tvSelectedDates);
        btnSelectDateRange = findViewById(R.id.btnSelectDateRange);
        btnBookRoom = findViewById(R.id.btnBookRoom);

        room = getIntent().getParcelableExtra("room_data");

        if (room != null) {
            tvBookingRoomName.setText(room.getName());
            tvBookingRoomDetails.setText(room.getDescription());
            tvBookingRoomPrice.setText(room.getPriceString() + " / night");
        }

        btnBack.setOnClickListener(v -> finish());

        setupDatePicker();

        btnBookRoom.setOnClickListener(v -> {
            if (checkInDate == null || checkOutDate == null) {
                Toast.makeText(BookingActivity.this, "Please select your stay dates", Toast.LENGTH_SHORT).show();
            } else {
                bookRoom();
            }
        });
    }

    private void setupDatePicker() {
        MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
        builder.setTitleText("Select Dates");

        // Disable past dates and already booked dates
        CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder();
        // Set start date to today (disable past dates)
        Calendar today = Calendar.getInstance();
        constraintsBuilder.setStart(today.getTimeInMillis());
        // Combine validators: disallow past dates AND booked dates
        constraintsBuilder.setValidator(new CompositeValidator(
                DateValidatorPointForward.now(),
                new BookedDateValidator(getAlreadyBookedDates())
        ));

        builder.setCalendarConstraints(constraintsBuilder.build());

        final MaterialDatePicker<Pair<Long, Long>> datePicker = builder.build();

        btnSelectDateRange.setOnClickListener(v -> {
            datePicker.show(getSupportFragmentManager(), "DATE_RANGE_PICKER");
        });

        datePicker.addOnPositiveButtonClickListener(selection -> {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            checkInDate = sdf.format(new Date(selection.first));
            checkOutDate = sdf.format(new Date(selection.second));
            tvSelectedDates.setText("Dates: " + checkInDate + " to " + checkOutDate);
        });
    }

    private void bookRoom() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        String email = sharedPreferences.getString("email", null);

        if (email == null) {
            Toast.makeText(this, "Please login to book a room.", Toast.LENGTH_SHORT).show();
            return;
        }

        int userId = getUserIdByEmail(email);

        if (userId != -1 && room != null) {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_BOOKING_USER_ID, userId);
            values.put(DatabaseHelper.COLUMN_BOOKING_ROOM_ID, room.getRoomId());
            values.put(DatabaseHelper.COLUMN_BOOKING_CHECK_IN, checkInDate);
            values.put(DatabaseHelper.COLUMN_BOOKING_CHECK_OUT, checkOutDate);

            long result = db.insert(DatabaseHelper.TABLE_BOOKING, null, values);

            if (result != -1) {
                Toast.makeText(this, "Booking successful!", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(BookingActivity.this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Booking failed. Please try again.", Toast.LENGTH_SHORT).show();
            }
        }
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

    private List<Long> getAlreadyBookedDates() {
        List<Long> bookedDates = new ArrayList<>();
        if (room == null) return bookedDates;

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_BOOKING,
                new String[]{DatabaseHelper.COLUMN_BOOKING_CHECK_IN, DatabaseHelper.COLUMN_BOOKING_CHECK_OUT},
                DatabaseHelper.COLUMN_BOOKING_ROOM_ID + "=?",
                new String[]{String.valueOf(room.getRoomId())}, null, null, null);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        
        if (cursor.moveToFirst()) {
            do {
                try {
                    String checkInStr = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BOOKING_CHECK_IN));
                    String checkOutStr = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BOOKING_CHECK_OUT));
                    Date checkIn = sdf.parse(checkInStr);
                    Date checkOut = sdf.parse(checkOutStr);
                    
                    Calendar calendar = Calendar.getInstance();
                    if (checkIn != null) {
                        calendar.setTime(checkIn);
                        while (calendar.getTime().before(checkOut)){
                            bookedDates.add(calendar.getTimeInMillis());
                            calendar.add(Calendar.DAY_OF_MONTH, 1);
                        }
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return bookedDates;
    }

    public static class BookedDateValidator implements CalendarConstraints.DateValidator {
        private List<Long> bookedDates;

        public BookedDateValidator(List<Long> bookedDates) {
            this.bookedDates = bookedDates;
        }

        @Override
        public boolean isValid(long date) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(date);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            long day = calendar.getTimeInMillis();
            
            for (long booked : bookedDates) {
                Calendar bookedCal = Calendar.getInstance();
                bookedCal.setTimeInMillis(booked);
                bookedCal.set(Calendar.HOUR_OF_DAY, 0);
                bookedCal.set(Calendar.MINUTE, 0);
                bookedCal.set(Calendar.SECOND, 0);
                bookedCal.set(Calendar.MILLISECOND, 0);
                if (day == bookedCal.getTimeInMillis()) {
                    return false;
                }
            }
            return true;
        }

        public static final Creator<BookedDateValidator> CREATOR = new Creator<BookedDateValidator>() {
            @Override
            public BookedDateValidator createFromParcel(Parcel source) {
                ArrayList<Long> dates = new ArrayList<>();
                source.readList(dates, Long.class.getClassLoader());
                return new BookedDateValidator(dates);
            }

            @Override
            public BookedDateValidator[] newArray(int size) {
                return new BookedDateValidator[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(@NonNull Parcel dest, int flags) {
            dest.writeList(bookedDates);
        }
    }
}