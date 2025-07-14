package com.example.onlinecoffeeshop.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Order implements Parcelable {

    private String orderId;
    private String userId;
    private String fullName;
    private String phone;
    private String email;
    private String address;
    private String note;
    private String deliveryMethod;
    private String paymentMethod;
    private List<CartItem> items;
    private double total;
    private long timestamp;
    private String shipmentStatus;

    public Order() {}

    public Order(String orderId, String userId, String fullName, String phone, String email,
                 String address, String note, String deliveryMethod, String paymentMethod,
                 List<CartItem> items, double total, long timestamp, String shipmentStatus) {
        this.orderId = orderId;
        this.userId = userId;
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.note = note;
        this.deliveryMethod = deliveryMethod;
        this.paymentMethod = paymentMethod;
        this.items = items;
        this.total = total;
        this.timestamp = timestamp;
        this.shipmentStatus = shipmentStatus;
    }

    public Order(String orderId, User user, String note, String deliveryMethod,
                 String paymentMethod, List<CartItem> items, double total, long timestamp, String shipmentStatus) {
        this(orderId, user.getUid(), user.getFullname(), user.getPhone(), user.getEmail(),
                user.getAddress(), note, deliveryMethod, paymentMethod, items, total, timestamp, shipmentStatus);
    }

    protected Order(Parcel in) {
        orderId = in.readString();
        userId = in.readString();
        fullName = in.readString();
        phone = in.readString();
        email = in.readString();
        address = in.readString();
        note = in.readString();
        deliveryMethod = in.readString();
        paymentMethod = in.readString();
        items = new ArrayList<>();
        in.readList(items, CartItem.class.getClassLoader());
        total = in.readDouble();
        timestamp = in.readLong();
        shipmentStatus = in.readString();
    }

    public static final Creator<Order> CREATOR = new Creator<Order>() {
        @Override
        public Order createFromParcel(Parcel in) {
            return new Order(in);
        }

        @Override
        public Order[] newArray(int size) {
            return new Order[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(orderId);
        parcel.writeString(userId);
        parcel.writeString(fullName);
        parcel.writeString(phone);
        parcel.writeString(email);
        parcel.writeString(address);
        parcel.writeString(note);
        parcel.writeString(deliveryMethod);
        parcel.writeString(paymentMethod);
        parcel.writeList(items);
        parcel.writeDouble(total);
        parcel.writeLong(timestamp);
        parcel.writeString(shipmentStatus);
    }


    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getDeliveryMethod() {
        return deliveryMethod;
    }

    public void setDeliveryMethod(String deliveryMethod) {
        this.deliveryMethod = deliveryMethod;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getShipmentStatus() {
        return shipmentStatus;
    }

    public void setShipmentStatus(String shipmentStatus) {
        this.shipmentStatus = shipmentStatus;
    }
}
