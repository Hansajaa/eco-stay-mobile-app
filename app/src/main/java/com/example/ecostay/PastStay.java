package com.example.ecostay;

public class PastStay {
    private String roomName;
    private String dates;
    private String totalPrice;

    public PastStay(String roomName, String dates, String totalPrice) {
        this.roomName = roomName;
        this.dates = dates;
        this.totalPrice = totalPrice;
    }

    public String getRoomName() { return roomName; }
    public String getDates() { return dates; }
    public String getTotalPrice() { return totalPrice; }
}