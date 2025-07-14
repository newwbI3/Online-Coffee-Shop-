package com.example.onlinecoffeeshop.controller;

import com.example.onlinecoffeeshop.model.FavouriteItem;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FavouriteController {
    private final DatabaseReference favRef;
    private ValueEventListener currentListener;

    public FavouriteController(String userId) {
        favRef = FirebaseDatabase.getInstance().getReference("Favourite").child(userId);
    }

    public void addFavourite(FavouriteItem item, OnFavouriteActionListener listener) {
        String favId = favRef.push().getKey();
        item.setFavId(favId);
        favRef.child(favId).setValue(item)
                .addOnSuccessListener(aVoid -> {
                    if (listener != null) listener.onSuccess("Added to favourites");
                })
                .addOnFailureListener(e -> {
                    if (listener != null) listener.onFailure("Failed to add to favourites: " + e.getMessage());
                });
    }

    public void removeFavourite(String favId, OnFavouriteActionListener listener) {
        favRef.child(favId).removeValue()
                .addOnSuccessListener(aVoid -> {
                    if (listener != null) listener.onSuccess("Removed from favourites");
                })
                .addOnFailureListener(e -> {
                    if (listener != null) listener.onFailure("Failed to remove from favourites: " + e.getMessage());
                });
    }

    public void getFavourites(ValueEventListener listener) {
        // Remove previous listener if exists
        if (currentListener != null) {
            favRef.removeEventListener(currentListener);
        }
        // Add real-time listener
        currentListener = listener;
        favRef.addValueEventListener(listener);
    }

    public void getFavouritesOnce(ValueEventListener listener) {
        favRef.addListenerForSingleValueEvent(listener);
    }

    public void removeListener() {
        if (currentListener != null) {
            favRef.removeEventListener(currentListener);
            currentListener = null;
        }
    }

    public interface OnFavouriteActionListener {
        void onSuccess(String message);
        void onFailure(String error);
    }
}
