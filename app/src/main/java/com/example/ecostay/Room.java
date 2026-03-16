package com.example.ecostay;

import android.os.Parcel;
import android.os.Parcelable;

public class Room implements Parcelable {
    private int roomId;
    private String name;
    private String description;
    private double price;
    private String type;

    public Room(int roomId, String name, String description, double price, String type) {
        this.roomId = roomId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.type = type;
    }

    protected Room(Parcel in) {
        roomId = in.readInt();
        name = in.readString();
        description = in.readString();
        price = in.readDouble();
        type = in.readString();
    }

    public static final Creator<Room> CREATOR = new Creator<Room>() {
        @Override
        public Room createFromParcel(Parcel in) {
            return new Room(in);
        }

        @Override
        public Room[] newArray(int size) {
            return new Room[size];
        }
    };

    public int getRoomId() { return roomId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public double getPrice() { return price; }
    public String getPriceString() { return "$" + (int)price; }
    public String getType() { return type; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(roomId);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeDouble(price);
        dest.writeString(type);
    }
}