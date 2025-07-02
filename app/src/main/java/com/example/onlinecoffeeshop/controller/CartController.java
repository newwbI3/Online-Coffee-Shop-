package com.example.onlinecoffeeshop.controller;

import com.example.onlinecoffeeshop.model.CartItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CartController {
    private final DatabaseReference cartRef;

    public CartController(String userId) {
        cartRef = FirebaseDatabase.getInstance().getReference("Cart").child(userId);
    }

    public void addToCart(CartItem item) {
        cartRef.child(item.getProductId()).setValue(item);
    }
}
