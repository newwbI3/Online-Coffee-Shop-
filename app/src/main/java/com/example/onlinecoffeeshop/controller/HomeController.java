package com.example.onlinecoffeeshop.controller;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeController {
    private DatabaseReference db = FirebaseDatabase.getInstance().getReference();

    public void loadCategories(ValueEventListener listener) {
        db.child("Category").addListenerForSingleValueEvent(listener);
    }

    public void loadPopularItems(ValueEventListener listener) {
        db.child("Popular").addListenerForSingleValueEvent(listener);
    }

    public void loadBanner(ValueEventListener listener) {
        db.child("Banner").addListenerForSingleValueEvent(listener);
    }
}
