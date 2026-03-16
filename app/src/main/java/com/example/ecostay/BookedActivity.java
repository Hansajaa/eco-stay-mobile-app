package com.example.ecostay;

public class BookedActivity {
    private String name;
    private String date;
    private String startTime;
    private String price;

    public BookedActivity(String name, String date, String startTime, String price) {
        this.name = name;
        this.date = date;
        this.startTime = startTime;
        this.price = price;
    }

    public String getName() { return name; }
    public String getDate() { return date; }
    public String getStartTime() { return startTime; }
    public String getPrice() { return price; }
}