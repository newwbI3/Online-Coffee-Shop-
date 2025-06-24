package com.example.onlinecoffeeshop.view.product;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onlinecoffeeshop.R;
import com.example.onlinecoffeeshop.adapter.ProductAdapter;
import com.example.onlinecoffeeshop.controller.ProductController;
import com.example.onlinecoffeeshop.model.Product;

import java.util.ArrayList;
import java.util.List;

public class ProductListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    private List<Product> productList;
    private ProductController controller;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product_list);
        // Init View
        recyclerView = findViewById(R.id.recyclerProducts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Init data
        productList = new ArrayList<>();
        adapter = new ProductAdapter(productList);
        recyclerView.setAdapter(adapter);

        // Controller
        controller = new ProductController();
        loadProductsFromFirebase();
    }

    private void loadProductsFromFirebase() {
        controller.loadAllProducts(new ProductController.OnProductLoadListener() {
            @Override
            public void onLoaded(List<Product> products) {
                productList.clear();
                productList.addAll(products);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailed(String errorMessage) {
                Toast.makeText(ProductListActivity.this, "Load failed: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
}