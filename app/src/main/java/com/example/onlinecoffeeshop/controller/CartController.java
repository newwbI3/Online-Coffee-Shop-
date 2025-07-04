package com.example.onlinecoffeeshop.controller;

import com.example.onlinecoffeeshop.model.CartItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CartController {
    private final DatabaseReference cartRef;

    public CartController(String userId) {
        cartRef = FirebaseDatabase.getInstance().getReference("Cart").child(userId);
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
}
