package com.example.onlinecoffeeshop.controller;

import com.example.onlinecoffeeshop.model.FavouriteItem;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FavouriteController {
    private final DatabaseReference favRef;

    public FavouriteController(String userId) {
        favRef = FirebaseDatabase.getInstance().getReference("Favourite").child(userId);
    }

    public void addFavourite(FavouriteItem item) {
        String favId = favRef.push().getKey();
        item.setFavId(favId);
        favRef.child(favId).setValue(item);
    }

    public void removeFavourite(String favId) {
        favRef.child(favId).removeValue();
    }

    public void getFavourites(ValueEventListener listener) {
        favRef.addListenerForSingleValueEvent(listener);
    }
}
