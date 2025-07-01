package com.example.onlinecoffeeshop.controller;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CategoryController {

    private final DatabaseReference categoryRef;

    public CategoryController() {
        categoryRef = FirebaseDatabase.getInstance().getReference("Category");
    }

    public void getAllCategories(ValueEventListener listener) {
        categoryRef.addListenerForSingleValueEvent(listener);
    }

    public void getCategoryById(int id, ValueEventListener listener) {
        categoryRef.child(String.valueOf(id)).addListenerForSingleValueEvent(listener);
    }

    public void observeCategories(ValueEventListener listener) {
        categoryRef.addValueEventListener(listener);
    }

    public void createCategory(int id, String title) {
        categoryRef.child(String.valueOf(id)).child("id").setValue(id);
        categoryRef.child(String.valueOf(id)).child("title").setValue(title);
    }

    public void deleteCategory(int id) {
        categoryRef.child(String.valueOf(id)).removeValue();
    }

    public void updateCategoryTitle(int id, String newTitle) {
        categoryRef.child(String.valueOf(id)).child("title").setValue(newTitle);
    }
}
