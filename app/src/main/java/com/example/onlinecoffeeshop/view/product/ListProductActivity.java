package com.example.onlinecoffeeshop.view.product;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onlinecoffeeshop.R;
import com.example.onlinecoffeeshop.adapter.ProductAdapter;
import com.example.onlinecoffeeshop.controller.ProductController;
import com.example.onlinecoffeeshop.model.Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ListProductActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private ProductController productController;
    private LinearLayout backBtn;
    private LinearLayout emptyStateSearch;
    private TextView tvEmptySearchTitle, tvEmptySearchSubtitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_list_product);

        recyclerView = findViewById(R.id.cartView);
        progressBar = findViewById(R.id.progressBar3);
        backBtn = findViewById(R.id.back_btn);
        emptyStateSearch = findViewById(R.id.emptyStateSearch);
        tvEmptySearchTitle = findViewById(R.id.tv_empty_search_title);
        tvEmptySearchSubtitle = findViewById(R.id.tv_empty_search_subtitle);

        backBtn.setOnClickListener(v -> finish());

        Button btnBrowseAll = findViewById(R.id.btn_browse_all);
        btnBrowseAll.setOnClickListener(v -> {
            // Clear search and show all products
            getIntent().removeExtra("searchQuery");
            loadProducts();
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        productController = new ProductController();

        loadProducts();
    }

    private void loadProducts() {
        String categoryId = getIntent().getStringExtra("categoryId");
        String searchQuery = getIntent().getStringExtra("searchQuery");

        progressBar.setVisibility(View.VISIBLE);

        productController.getAllProducts(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressBar.setVisibility(View.GONE);
                List<Product> productList = new ArrayList<>();

                for (DataSnapshot item : snapshot.getChildren()) {
                    Product product = item.getValue(Product.class);
                    if (product == null) continue;

                    boolean matchCategory = categoryId == null || categoryId.equals(product.getCategoryId());
                    boolean matchSearch = searchQuery == null || product.getTitle().toLowerCase().contains(searchQuery.toLowerCase());

                    if (matchCategory && matchSearch) {
                        productList.add(product);
                    }
                }

                recyclerView.setAdapter(new ProductAdapter(ListProductActivity.this, productList));

                // Show/hide empty state
                if (productList.isEmpty()) {
                    recyclerView.setVisibility(View.GONE);
                    emptyStateSearch.setVisibility(View.VISIBLE);

                    // Update messages based on search query
                    if (searchQuery != null && !searchQuery.isEmpty()) {
                        tvEmptySearchTitle.setText("Không tìm thấy \"" + searchQuery + "\"");
                        tvEmptySearchSubtitle.setText("Thử tìm kiếm với từ khóa khác");
                    } else {
                        tvEmptySearchTitle.setText("Không có sản phẩm");
                        tvEmptySearchSubtitle.setText("Danh mục này chưa có sản phẩm nào");
                    }
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    emptyStateSearch.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ListProductActivity.this, "Lỗi tải sản phẩm", Toast.LENGTH_SHORT).show();
            }
        });
    }



}
