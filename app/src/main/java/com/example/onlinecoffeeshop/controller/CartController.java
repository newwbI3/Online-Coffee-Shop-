package com.example.onlinecoffeeshop.controller;

import com.example.onlinecoffeeshop.model.CartItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CartController {
    private final DatabaseReference cartRef;

    public CartController(String userId) {
        cartRef = FirebaseDatabase.getInstance().getReference("Cart").child(userId);
    }

    public void addToCart(CartItem newItem, OnCartActionListener listener) {
        // Create a unique key based on product ID and size
        String itemKey = newItem.getProductId() + "_" + extractSizeFromTitle(newItem.getTitle());

        // Check if item already exists in cart
        cartRef.child(itemKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Item already exists, update quantity
                    CartItem existingItem = snapshot.getValue(CartItem.class);
                    if (existingItem != null) {
                        int newQuantity = existingItem.getQuantity() + newItem.getQuantity();
                        cartRef.child(itemKey).child("quantity").setValue(newQuantity)
                                .addOnSuccessListener(aVoid -> {
                                    if (listener != null) {
                                        listener.onSuccess("Updated quantity in cart (Total: " + newQuantity + ")");
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    if (listener != null) {
                                        listener.onFailure("Failed to update cart: " + e.getMessage());
                                    }
                                });
                    }
                } else {
                    // Item doesn't exist, add new item
                    cartRef.child(itemKey).setValue(newItem)
                            .addOnSuccessListener(aVoid -> {
                                if (listener != null) {
                                    listener.onSuccess("Added to cart");
                                }
                            })
                            .addOnFailureListener(e -> {
                                if (listener != null) {
                                    listener.onFailure("Failed to add to cart: " + e.getMessage());
                                }
                            });
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                if (listener != null) {
                    listener.onFailure("Error checking cart: " + error.getMessage());
                }
            }
        });
    }

    private String extractSizeFromTitle(String title) {
        // Extract size from title like "Coffee Name (Medium)"
        if (title.contains("(") && title.contains(")")) {
            int start = title.lastIndexOf("(") + 1;
            int end = title.lastIndexOf(")");
            if (start < end) {
                return title.substring(start, end).trim();
            }
        }
        return "Small"; // Default size
    }

    public void addToCart(CartItem item) {
        cartRef.child(item.getProductId()).setValue(item);
    }

    public void getCartItems(ValueEventListener listener) {
        cartRef.addListenerForSingleValueEvent(listener);
    }

    public void removeItem(String itemId) {
        cartRef.child(itemId).removeValue();
    }

    public void updateQuantity(String itemId, int newQty) {
        cartRef.child(itemId).child("quantity").setValue(newQty);
    }

    public void clearCart() {
        cartRef.removeValue();
    }

    public interface OnCartActionListener {
        void onSuccess(String message);
        void onFailure(String error);
    }
}
