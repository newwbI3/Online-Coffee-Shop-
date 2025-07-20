package com.example.onlinecoffeeshop.model;

import android.os.Parcel;
import android.os.Parcelable;

public class OrderStatusUpdate implements Parcelable {
    private String status;
    private long timestamp;

    public OrderStatusUpdate() {}

    public OrderStatusUpdate(String status, long timestamp) {
        this.status = status;
        this.timestamp = timestamp;
    }

    protected OrderStatusUpdate(Parcel in) {
        status = in.readString();
        timestamp = in.readLong();
    }

    public static final Creator<OrderStatusUpdate> CREATOR = new Creator<OrderStatusUpdate>() {
        @Override
        public OrderStatusUpdate createFromParcel(Parcel in) {
            return new OrderStatusUpdate(in);
        }

        @Override
        public OrderStatusUpdate[] newArray(int size) {
            return new OrderStatusUpdate[size];
        }
    };

    public String getStatus() {
        return status;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(status);
        dest.writeLong(timestamp);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
