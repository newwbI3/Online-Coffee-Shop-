package com.example.onlinecoffeeshop.view.product;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
    private ActivityResultLauncher<Intent> updateLauncher;
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

        updateLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        loadProductsFromFirebase();  // Gọi lại controller để load danh sách
                    }
                });

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
    private void openUpdateScreen(Product product) {
        Intent intent = new Intent(this, UpdateProductActivity.class);
        intent.putExtra("product_id", product.getProduct_id());
        intent.putExtra("name", product.getName());
        intent.putExtra("price", product.getPrice());
        intent.putExtra("category_id", product.getCategory_id());
        intent.putExtra("image_url", product.getImage_url());
        updateLauncher.launch(intent);
    }
}