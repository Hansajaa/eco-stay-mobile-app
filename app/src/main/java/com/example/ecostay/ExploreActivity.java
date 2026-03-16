package com.example.ecostay;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.slider.RangeSlider;

import java.util.ArrayList;
import java.util.List;

public class ExploreActivity extends AppCompatActivity {

    private RecyclerView rvRooms;
    private RoomAdapter roomAdapter;
    private List<Room> roomList;
    private List<Room> filteredList;

    private EditText etSearchRoom;
    private DrawerLayout drawerLayout;
    private ImageView ivFilterSettings;
    private RangeSlider priceRangeSlider;
    private TextView tvPriceRangeDisplay;
    private ChipGroup chipGroupType;
    private Button btnApplyFilters;
    private DatabaseHelper dbHelper;

    private float minPrice = 0;
    private float maxPrice = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);

        dbHelper = new DatabaseHelper(this);
        drawerLayout = findViewById(R.id.drawer_layout);
        ivFilterSettings = findViewById(R.id.ivFilterSettings);
        etSearchRoom = findViewById(R.id.etSearchRoom);
        priceRangeSlider = findViewById(R.id.priceRangeSlider);
        tvPriceRangeDisplay = findViewById(R.id.tvPriceRangeDisplay);
        chipGroupType = findViewById(R.id.chipGroupType);
        btnApplyFilters = findViewById(R.id.btnApplyFilters);

        rvRooms = findViewById(R.id.rvRooms);
        rvRooms.setLayoutManager(new LinearLayoutManager(this));

        // Load rooms from database
        roomList = getAllRooms();
        filteredList = new ArrayList<>(roomList);
        roomAdapter = new RoomAdapter(filteredList);
        rvRooms.setAdapter(roomAdapter);

        // Open Drawer
        ivFilterSettings.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.END));

        // Search Text Change (Real-time)
        etSearchRoom.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyFilters();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Price Range Slider Change
        priceRangeSlider.addOnChangeListener((slider, value, fromUser) -> {
            List<Float> values = slider.getValues();
            minPrice = values.get(0);
            maxPrice = values.get(1);
            tvPriceRangeDisplay.setText(String.format("$%d - $%d", (int)minPrice, (int)maxPrice));
        });

        // Apply Button in Drawer
        btnApplyFilters.setOnClickListener(v -> {
            applyFilters();
            drawerLayout.closeDrawer(GravityCompat.END);
        });

        setupBottomNavigation();
    }

    private List<Room> getAllRooms() {
        List<Room> rooms = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_ROOM, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ROOM_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ROOM_NAME));
                String type = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ROOM_TYPE));
                String desc = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ROOM_DESCRIPTION));
                double price = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ROOM_PRICE));
                rooms.add(new Room(id, name, desc, price, type));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return rooms;
    }

    private void applyFilters() {
        String query = etSearchRoom.getText().toString().toLowerCase().trim();
        int selectedChipId = chipGroupType.getCheckedChipId();
        
        String selectedType = "";
        if (selectedChipId != -1) {
            Chip chip = findViewById(selectedChipId);
            selectedType = chip.getText().toString();
        }

        List<Room> tempFiltered = new ArrayList<>();

        for (Room room : roomList) {
            boolean matchesSearch = room.getName().toLowerCase().contains(query);
            boolean matchesPrice = room.getPrice() >= minPrice && room.getPrice() <= maxPrice;
            boolean matchesType = selectedType.equals("All") || selectedType.isEmpty() || room.getType().equals(selectedType);

            if (matchesSearch && matchesPrice && matchesType) {
                tempFiltered.add(room);
            }
        }

        roomAdapter.updateList(tempFiltered);
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.navigation_explore);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.navigation_home) {
                startActivity(new Intent(ExploreActivity.this, HomeActivity.class));
                finish();
                return true;
            } else if (id == R.id.navigation_explore) {
                return true;
            } else if (id == R.id.navigation_activity) {
                startActivity(new Intent(ExploreActivity.this, ActivitiesActivity.class));
                finish();
                return true;
            } else if (id == R.id.navigation_profile) {
                startActivity(new Intent(ExploreActivity.this, ProfileActivity.class));
                finish();
                return true;
            }
            return false;
        });
    }
}