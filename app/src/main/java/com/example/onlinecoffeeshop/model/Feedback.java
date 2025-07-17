package com.example.onlinecoffeeshop.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Feedback implements Parcelable {
    private String feedbackId;
    private String productId;
    private String userId;
    private String userName;
    private String orderId;
    private float rating;
    private String comment;
    private long timestamp;

    public Feedback() {}

    public Feedback(String productId, String userId, String userName, String orderId,
                   float rating, String comment, long timestamp) {
        this.productId = productId;
        this.userId = userId;
        this.userName = userName;
        this.orderId = orderId;
        this.rating = rating;
        this.comment = comment;
        this.timestamp = timestamp;
    }

    protected Feedback(Parcel in) {
        feedbackId = in.readString();
        productId = in.readString();
        userId = in.readString();
        userName = in.readString();
        orderId = in.readString();
        rating = in.readFloat();
        comment = in.readString();
        timestamp = in.readLong();
    }

    public static final Creator<Feedback> CREATOR = new Creator<Feedback>() {
        @Override
        public Feedback createFromParcel(Parcel in) {
            return new Feedback(in);
        }

        @Override
        public Feedback[] newArray(int size) {
            return new Feedback[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(feedbackId);
        parcel.writeString(productId);
        parcel.writeString(userId);
        parcel.writeString(userName);
        parcel.writeString(orderId);
        parcel.writeFloat(rating);
        parcel.writeString(comment);
        parcel.writeLong(timestamp);
    }

    // Getters and Setters
    public String getFeedbackId() {
        return feedbackId;
    }

    public void setFeedbackId(String feedbackId) {
        this.feedbackId = feedbackId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    // Helper methods for UserFeedbackAdapter
    public String getFeedbackText() {
        return comment;
    }

    public String getProductName() {
        // This would need to be fetched from product collection using productId
        // For now, return null - can be enhanced later
        return null;
    }
}
