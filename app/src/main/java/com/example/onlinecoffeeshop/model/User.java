package com.example.onlinecoffeeshop.model;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.PropertyName;
import java.util.Arrays;
import java.util.List;

public class User {
    public static final List<String> VALID_ROLES = Arrays.asList("marketer", "admin", "inventory", "user");
    @PropertyName("uid")
    private String uid;

    // Document ID from Firestore (not stored in document)
    private String userId;

    @PropertyName("fullname")
    private String fullname;

    @PropertyName("dob")
    private String dob;

    @PropertyName("avatar")
    private String avatar;

    @PropertyName("address")
    private String address;

    @PropertyName("phone")
    private String phone;

    @PropertyName("role")
    private String role;

    @PropertyName("isBan")
    private boolean isBan;

    @PropertyName("email")
    private String email;

    // Default constructor required for Firestore
    public User() {
        this.isBan = false; // Default not banned
    }

    public User(String uid, String fullname, String dob, String avatar, String address, String phone, String role, String email) {
        this.uid = uid;
        this.fullname = fullname;
        this.dob = dob;
        this.avatar = avatar;
        this.address = address;
        this.phone = phone;
        this.role = role != null && VALID_ROLES.contains(role.toLowerCase()) ? role.toLowerCase() : "user";
        this.email = email;
        this.isBan = false; // Default not banned
    }

    // Constructor without uid (for creating new users)
    public User(String fullname, String dob, String avatar, String address, String phone, String role, String email) {
        this.fullname = fullname;
        this.dob = dob;
        this.avatar = avatar;
        this.address = address;
        this.phone = phone;
        this.role = role != null && VALID_ROLES.contains(role.toLowerCase()) ? role.toLowerCase() : "user";
        this.email = email;
        this.isBan = false; // Default not banned
    }

    public User(String userId, String fullName, String phone, String email, String address) {
        this.uid = userId;
        this.fullname = fullName;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.isBan = false; // Default not banned
    }

    public String getUid() {
        return uid;
    }
    public void setUid(String uid) {
        this.uid = uid;
    }
    public String getFullname() {
        return fullname;
    }
    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getDob() {
        return dob;
    }
    public void setDob(String dob) {
        this.dob = dob;
    }
    public String getAvatar() {
        return avatar;
    }
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role != null && VALID_ROLES.contains(role.toLowerCase()) ? role.toLowerCase() : "user";
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isBan() {
        return isBan;
    }

    public void setBan(boolean ban) {
        isBan = ban;
    }

    // Utility methods
    public boolean isAdmin() {
        return "admin".equals(this.role);
    }

    public boolean isMarketer() {
        return "marketer".equals(this.role);
    }

    public boolean isInventory() {
        return "inventory".equals(this.role);
    }

    public boolean isUser() {
        return "user".equals(this.role);
    }
    @Override
    public String toString() {
        return "User{" +
                "uid='" + uid + '\'' +
                ", fullname='" + fullname + '\'' +
                ", dob='" + dob + '\'' +
                ", avatar='" + avatar + '\'' +
                ", address='" + address + '\'' +
                ", phone='" + phone + '\'' +
                ", role='" + role + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
