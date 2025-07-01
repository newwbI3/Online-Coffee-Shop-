package com.example.onlinecoffeeshop;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.onlinecoffeeshop.adapter.CategoryAdapter;
import com.example.onlinecoffeeshop.adapter.PopularAdapter;
import com.example.onlinecoffeeshop.controller.CategoryController;
import com.example.onlinecoffeeshop.controller.HomeController;
import com.example.onlinecoffeeshop.model.Category;
import com.example.onlinecoffeeshop.model.Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ImageView imgBanner;
    private ProgressBar progressBarBanner, progressBarCat, progressBarDrink;
    private RecyclerView recyclerViewCat, recyclerViewDrinks;

    private HomeController controller;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        imgBanner = findViewById(R.id.imgBanner);
        progressBarBanner = findViewById(R.id.progressBarBanner);
        progressBarCat = findViewById(R.id.progressBarCat);
        progressBarDrink = findViewById(R.id.progressBarDrink);
        recyclerViewCat = findViewById(R.id.recyclerViewCat);
        recyclerViewDrinks = findViewById(R.id.recyclerViewDrinks);

        controller = new HomeController();

        loadBanner();
        loadCategories();
        loadPopularDrinks();
    }

    private void loadBanner() {
        controller.loadBanner(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressBarBanner.setVisibility(View.GONE);
                for (DataSnapshot item : snapshot.getChildren()) {
                    String url = item.child("url").getValue(String.class);
                    Glide.with(MainActivity.this).load(url).into(imgBanner);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
    private void loadCategories() {
        controller.loadCategories(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressBarCat.setVisibility(View.GONE);
                List<Category> categories = new ArrayList<>();
                for (DataSnapshot item : snapshot.getChildren()) {
                    Category cat = item.getValue(Category.class);
                    categories.add(cat);
                }
                recyclerViewCat.setLayoutManager(new LinearLayoutManager(MainActivity.this, RecyclerView.HORIZONTAL, false));
                recyclerViewCat.setAdapter(new CategoryAdapter(categories));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void loadPopularDrinks() {
        controller.loadPopularItems(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressBarDrink.setVisibility(View.GONE);
                List<Product> products = new ArrayList<>();
                for (DataSnapshot item : snapshot.getChildren()) {
                    Product p = item.getValue(Product.class);
                    products.add(p);
                }
                recyclerViewDrinks.setLayoutManager(new GridLayoutManager(MainActivity.this, 2));
                recyclerViewDrinks.setAdapter(new PopularAdapter(MainActivity.this, products));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

}