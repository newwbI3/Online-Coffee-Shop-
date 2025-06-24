package com.example.onlinecoffeeshop.controller;

import androidx.annotation.NonNull;

import com.example.onlinecoffeeshop.model.Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

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
    public void loadAllProducts(OnProductLoadListener listener) {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Product> productList = new ArrayList<>();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Product p = data.getValue(Product.class);
                    productList.add(p);
                }
                listener.onLoaded(productList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                listener.onFailed(error.getMessage());
            }
        });
    }

    public interface OnProductLoadListener {
        void onLoaded(List<Product> products);
        void onFailed(String errorMessage);
    }

    public interface OnProductAddedListener {
        void onSuccess();
        void onFailure(String message);
    }

}
