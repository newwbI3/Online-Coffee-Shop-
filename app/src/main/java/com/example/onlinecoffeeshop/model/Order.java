package com.example.onlinecoffeeshop.model;

import java.util.List;

public class Order {

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

    // Bắt buộc cho Firebase
    public Order() {}

    // Full constructor dùng trong Firebase write
    public Order(String orderId, String userId, String fullName, String phone, String email,
                 String address, String note, String deliveryMethod, String paymentMethod,
                 List<CartItem> items, double total, long timestamp) {
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
    }

    // Constructor tiện dụng khi đã có đối tượng User
    public Order(String orderId, User user, String note, String deliveryMethod,
                 String paymentMethod, List<CartItem> items, double total, long timestamp) {
        this.orderId = orderId;
        this.userId = user.getUid();
        this.fullName = user.getFullname();
        this.phone = user.getPhone();
        this.email = user.getEmail();
        this.address = user.getAddress();
        this.note = note;
        this.deliveryMethod = deliveryMethod;
        this.paymentMethod = paymentMethod;
        this.items = items;
        this.total = total;
        this.timestamp = timestamp;
    }

    // Getters & Setters

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
}
