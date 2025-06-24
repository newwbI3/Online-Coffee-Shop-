package com.example.onlinecoffeeshop.controller;

import com.example.onlinecoffeeshop.model.CartItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class CartController {
    public void addToCart(CartItem item, OnCartAddListener listener) {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) {
            listener.onFailure("User not logged in");
            return;
        }

        FirebaseDatabase.getInstance().getReference("cart")
                .child(uid)
                .push()
                .setValue(item)
                .addOnSuccessListener(unused -> listener.onSuccess())
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }
    public interface OnCartAddListener {
        void onSuccess();
        void onFailure(String error);
    }
}
