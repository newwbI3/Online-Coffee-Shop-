package com.example.onlinecoffeeshop.controller;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProductController {
    private final DatabaseReference ref;

    public ProductController() {
        ref = FirebaseDatabase.getInstance().getReference("Popular");
    }
    public void getProductById(String productId, ValueEventListener listener) {
        ref.orderByChild("productId").equalTo(productId).addListenerForSingleValueEvent(listener);
    }
}
