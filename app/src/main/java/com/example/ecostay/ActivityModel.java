package com.example.ecostay;

import android.os.Parcel;
import android.os.Parcelable;

public class ActivityModel implements Parcelable {
    private int activityId;
    private String name;
    private String description;
    private String price;
    private String startTime;
    private String duration;
    private String category;
    private int imageResId;

    public ActivityModel(int activityId, String name, String description, String price, String startTime, String duration, String category, int imageResId) {
        this.activityId = activityId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.startTime = startTime;
        this.duration = duration;
        this.category = category;
        this.imageResId = imageResId;
    }

    protected ActivityModel(Parcel in) {
        activityId = in.readInt();
        name = in.readString();
        description = in.readString();
        price = in.readString();
        startTime = in.readString();
        duration = in.readString();
        category = in.readString();
        imageResId = in.readInt();
    }

    public static final Creator<ActivityModel> CREATOR = new Creator<ActivityModel>() {
        @Override
        public ActivityModel createFromParcel(Parcel in) {
            return new ActivityModel(in);
        }

        @Override
        public ActivityModel[] newArray(int size) {
            return new ActivityModel[size];
        }
    };

    public int getActivityId() { return activityId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getPrice() { return price; }
    public String getStartTime() { return startTime; }
    public String getDuration() { return duration; }
    public String getCategory() { return category; }
    public int getImageResId() { return imageResId; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(activityId);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(price);
        dest.writeString(startTime);
        dest.writeString(duration);
        dest.writeString(category);
        dest.writeInt(imageResId);
    }
}