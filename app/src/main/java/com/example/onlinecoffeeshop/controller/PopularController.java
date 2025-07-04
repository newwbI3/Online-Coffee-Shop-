package com.example.onlinecoffeeshop.controller;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PopularController {
    private final DatabaseReference ref;

    public PopularController() {
        ref = FirebaseDatabase.getInstance().getReference("Popular");
    }
    public void getProductById(String productId, ValueEventListener listener) {
        ref.orderByChild("productId").equalTo(productId).addListenerForSingleValueEvent(listener);
    }
    public void getAllProducts(ValueEventListener listener) {
        ref.addListenerForSingleValueEvent(listener);
    }
}
