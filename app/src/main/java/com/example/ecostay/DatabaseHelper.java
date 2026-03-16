package com.example.ecostay;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "EcoStay.db";
    private static final int DATABASE_VERSION = 5;

    public static final String TABLE_USER = "user";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_FULLNAME = "fullName";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PASSWORD = "password";

    public static final String TABLE_ROOM = "room";
    public static final String COLUMN_ROOM_ID = "roomId";
    public static final String COLUMN_ROOM_TYPE = "roomType";
    public static final String COLUMN_ROOM_NAME = "roomName";
    public static final String COLUMN_ROOM_DESCRIPTION = "description";
    public static final String COLUMN_ROOM_PRICE = "pricePerNight";

    public static final String TABLE_BOOKING = "booking";
    public static final String COLUMN_BOOKING_ID = "bookingId";
    public static final String COLUMN_BOOKING_USER_ID = "userId";
    public static final String COLUMN_BOOKING_ROOM_ID = "roomId";
    public static final String COLUMN_BOOKING_CHECK_IN = "checkInDate";
    public static final String COLUMN_BOOKING_CHECK_OUT = "checkOutDate";

    public static final String TABLE_ACTIVITY = "activity";
    public static final String COLUMN_ACTIVITY_ID = "activityId";
    public static final String COLUMN_ACTIVITY_NAME = "activityName";
    public static final String COLUMN_ACTIVITY_DESCRIPTION = "description";
    public static final String COLUMN_ACTIVITY_PRICE = "price";
    public static final String COLUMN_ACTIVITY_DURATION = "duration";
    public static final String COLUMN_ACTIVITY_START_TIME = "startTime";
    public static final String COLUMN_ACTIVITY_CATEGORY = "category";

    public static final String TABLE_ACTIVITY_BOOKING = "activityBooking";
    public static final String COLUMN_ACTIVITY_BOOKING_ID = "activityBookingId";
    public static final String COLUMN_ACT_BOOKING_USER_ID = "userId";
    public static final String COLUMN_ACT_BOOKING_ACTIVITY_ID = "activityId";
    public static final String COLUMN_ACT_BOOKING_DATE = "bookingDate";

    public static final String TABLE_PREFERENCES = "preferences";
    public static final String COLUMN_PREF_USER_ID = "userId";
    public static final String COLUMN_PREF_DIETARY = "dietaryNeeds";
    public static final String COLUMN_PREF_TEMP = "roomTemp";
    public static final String COLUMN_PREF_PILLOW = "pillowType";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_USER + "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_FULLNAME + " TEXT NOT NULL," + COLUMN_EMAIL + " TEXT NOT NULL UNIQUE," + COLUMN_PASSWORD + " TEXT NOT NULL" + ")");
        db.execSQL("CREATE TABLE " + TABLE_ROOM + "(" + COLUMN_ROOM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_ROOM_NAME + " TEXT NOT NULL," + COLUMN_ROOM_TYPE + " TEXT NOT NULL," + COLUMN_ROOM_DESCRIPTION + " TEXT NOT NULL," + COLUMN_ROOM_PRICE + " REAL NOT NULL CHECK(" + COLUMN_ROOM_PRICE + " >= 0)" + ")");
        db.execSQL("CREATE TABLE " + TABLE_BOOKING + "(" + COLUMN_BOOKING_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_BOOKING_USER_ID + " INTEGER NOT NULL," + COLUMN_BOOKING_ROOM_ID + " INTEGER NOT NULL," + COLUMN_BOOKING_CHECK_IN + " TEXT NOT NULL," + COLUMN_BOOKING_CHECK_OUT + " TEXT NOT NULL," + "FOREIGN KEY (" + COLUMN_BOOKING_USER_ID + ") REFERENCES " + TABLE_USER + "(" + COLUMN_ID + ") ON DELETE CASCADE," + "FOREIGN KEY (" + COLUMN_BOOKING_ROOM_ID + ") REFERENCES " + TABLE_ROOM + "(" + COLUMN_ROOM_ID + ") ON DELETE CASCADE" + ")");
        db.execSQL("CREATE TABLE " + TABLE_ACTIVITY + "(" + COLUMN_ACTIVITY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_ACTIVITY_NAME + " TEXT NOT NULL," + COLUMN_ACTIVITY_DESCRIPTION + " TEXT NOT NULL," + COLUMN_ACTIVITY_PRICE + " REAL NOT NULL CHECK(" + COLUMN_ACTIVITY_PRICE + " >= 0)," + COLUMN_ACTIVITY_DURATION + " TEXT NOT NULL," + COLUMN_ACTIVITY_START_TIME + " TEXT NOT NULL," + COLUMN_ACTIVITY_CATEGORY + " TEXT" + ")");
        db.execSQL("CREATE TABLE " + TABLE_ACTIVITY_BOOKING + "(" + COLUMN_ACTIVITY_BOOKING_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_ACT_BOOKING_USER_ID + " INTEGER NOT NULL," + COLUMN_ACT_BOOKING_ACTIVITY_ID + " INTEGER NOT NULL," + COLUMN_ACT_BOOKING_DATE + " TEXT NOT NULL," + "FOREIGN KEY (" + COLUMN_ACT_BOOKING_USER_ID + ") REFERENCES " + TABLE_USER + "(" + COLUMN_ID + ") ON DELETE CASCADE," + "FOREIGN KEY (" + COLUMN_ACT_BOOKING_ACTIVITY_ID + ") REFERENCES " + TABLE_ACTIVITY + "(" + COLUMN_ACTIVITY_ID + ") ON DELETE CASCADE" + ")");
        db.execSQL("CREATE TABLE " + TABLE_PREFERENCES + "(" + COLUMN_PREF_USER_ID + " INTEGER PRIMARY KEY," + COLUMN_PREF_DIETARY + " TEXT," + COLUMN_PREF_TEMP + " TEXT," + COLUMN_PREF_PILLOW + " TEXT," + "FOREIGN KEY (" + COLUMN_PREF_USER_ID + ") REFERENCES " + TABLE_USER + "(" + COLUMN_ID + ") ON DELETE CASCADE" + ")");
        insertInitialData(db);
    }

    private void insertInitialData(SQLiteDatabase db) {
        insertRoom(db, "Pine View Eco-Pod", "Eco-Pod", "High Altitude • 3.2 miles away", 240.0);
        insertRoom(db, "Alpine Glass Lodge", "Glass Lodge", "Peak Summit • 5.1 miles away", 315.0);
        insertRoom(db, "Lakefront Eco-Cabin", "Eco-Cabin", "Still Water • 1.2 miles away", 285.0);
        insertRoom(db, "Forest Retreat", "Cabin", "Deep Woods • 10.5 miles away", 150.0);
        insertActivity(db, "Guided Sunrise Hike", "Experience the peaks at dawn with local expert guides.", 45.0, "3 hrs", "05:30 AM", "Hiking");
        insertActivity(db, "Reforestation Workshop", "Plant a native tree and leave your legacy in the retreat park.", 0.0, "2 hrs", "10:00 AM", "Workshops");
        insertActivity(db, "Mountain Yoga", "Outdoor meditation and flow session with a 360° view.", 25.0, "1.5 hrs", "04:00 PM", "Relaxation");
    }

    private void insertRoom(SQLiteDatabase db, String name, String type, String desc, double price) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_ROOM_NAME, name);
        values.put(COLUMN_ROOM_TYPE, type);
        values.put(COLUMN_ROOM_DESCRIPTION, desc);
        values.put(COLUMN_ROOM_PRICE, price);
        db.insert(TABLE_ROOM, null, values);
    }

    private void insertActivity(SQLiteDatabase db, String name, String desc, double price, String duration, String startTime, String category) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_ACTIVITY_NAME, name);
        values.put(COLUMN_ACTIVITY_DESCRIPTION, desc);
        values.put(COLUMN_ACTIVITY_PRICE, price);
        values.put(COLUMN_ACTIVITY_DURATION, duration);
        values.put(COLUMN_ACTIVITY_START_TIME, startTime);
        values.put(COLUMN_ACTIVITY_CATEGORY, category);
        db.insert(TABLE_ACTIVITY, null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PREFERENCES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACTIVITY_BOOKING);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACTIVITY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKING);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ROOM);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        onCreate(db);
    }

    public int getUserIdByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USER, new String[]{COLUMN_ID}, COLUMN_EMAIL + "=?", new String[]{email}, null, null, null);
        int userId = -1;
        if (cursor.moveToFirst()) {
            userId = cursor.getInt(0);
        }
        cursor.close();
        return userId;
    }

    public Cursor getPastStays(int userId, String currentDate) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT b.*, r." + COLUMN_ROOM_NAME + ", r." + COLUMN_ROOM_PRICE + 
                       " FROM " + TABLE_BOOKING + " b " +
                       " JOIN " + TABLE_ROOM + " r ON b." + COLUMN_BOOKING_ROOM_ID + " = r." + COLUMN_ROOM_ID +
                       " WHERE b." + COLUMN_BOOKING_USER_ID + " = ? AND b." + COLUMN_BOOKING_CHECK_OUT + " < ?";
        return db.rawQuery(query, new String[]{String.valueOf(userId), currentDate});
    }

    public Cursor getUpcomingActivities(int userId, String currentDate) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT ab.*, a." + COLUMN_ACTIVITY_NAME + ", a." + COLUMN_ACTIVITY_PRICE + ", a." + COLUMN_ACTIVITY_START_TIME +
                       " FROM " + TABLE_ACTIVITY_BOOKING + " ab " +
                       " JOIN " + TABLE_ACTIVITY + " a ON ab." + COLUMN_ACT_BOOKING_ACTIVITY_ID + " = a." + COLUMN_ACTIVITY_ID +
                       " WHERE ab." + COLUMN_ACT_BOOKING_USER_ID + " = ? AND ab." + COLUMN_ACT_BOOKING_DATE + " >= ?";
        return db.rawQuery(query, new String[]{String.valueOf(userId), currentDate});
    }
}