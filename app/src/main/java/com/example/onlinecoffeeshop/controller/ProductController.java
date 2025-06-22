package com.example.onlinecoffeeshop.controller;

import com.example.onlinecoffeeshop.model.Product;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ProductController {
    private DatabaseReference databaseReference;

    public ProductController() {
        databaseReference = FirebaseDatabase.getInstance().getReference("products");
    }
    public void addProduct(Product product, String product_id , OnProductAddedListener listener) {
       databaseReference.child(product_id).setValue(product)
               .addOnSuccessListener(aVoid -> listener.onSuccess())
               .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }
    public interface OnProductAddedListener {
        void onSuccess();
        void onFailure(String message);
    }
}
