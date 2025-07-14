package com.example.onlinecoffeeshop.controller;

import com.example.onlinecoffeeshop.model.FavouriteItem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
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
        // First check if the product already exists in favorites
        checkIfProductExists(item.getProductId(), new OnProductCheckListener() {
            @Override
            public void onExists(String existingFavId) {
                // Product already exists in favorites
                if (listener != null) {
                    listener.onFailure("Product is already in your favourites");
                }
            }

            @Override
            public void onNotExists() {
                // Product doesn't exist, safe to add
                String favId = favRef.push().getKey();
                if (favId != null) {
                    item.setFavId(favId);
                    favRef.child(favId).setValue(item)
                            .addOnSuccessListener(aVoid -> {
                                if (listener != null) listener.onSuccess("Added to favourites");
                            })
                            .addOnFailureListener(e -> {
                                if (listener != null) listener.onFailure("Failed to add to favourites: " + e.getMessage());
                            });
                } else {
                    if (listener != null) listener.onFailure("Failed to generate unique ID");
                }
            }

            @Override
            public void onError(String error) {
                if (listener != null) listener.onFailure("Error checking duplicates: " + error);
            }
        });
    }

    private void checkIfProductExists(String productId, OnProductCheckListener listener) {
        favRef.orderByChild("productId").equalTo(productId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            // Product already exists
                            String existingFavId = snapshot.getChildren().iterator().next().getKey();
                            listener.onExists(existingFavId);
                        } else {
                            // Product doesn't exist
                            listener.onNotExists();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        listener.onError(error.getMessage());
                    }
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

    private interface OnProductCheckListener {
        void onExists(String existingFavId);
        void onNotExists();
        void onError(String error);
    }
}
