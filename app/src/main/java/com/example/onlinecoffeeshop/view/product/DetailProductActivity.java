package com.example.onlinecoffeeshop.view.product;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;


import com.bumptech.glide.Glide;
import com.example.onlinecoffeeshop.R;
import com.example.onlinecoffeeshop.controller.PopularController;
import com.example.onlinecoffeeshop.controller.ProductController;
import com.example.onlinecoffeeshop.model.Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class DetailProductActivity extends AppCompatActivity {
    private ImageView picMain;
    private TextView titleTxt, descriptionTxt, ratingTxt, priceTxt;
    private TextView backBtn;

    private PopularController popularController;
    private ProductController productController;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detail_product);

        picMain = findViewById(R.id.picMain);
        titleTxt = findViewById(R.id.titleTxt);
        descriptionTxt = findViewById(R.id.descriptionTxt);
        ratingTxt = findViewById(R.id.ratingTxt);
        priceTxt = findViewById(R.id.priceTxt);

        popularController = new PopularController();
        productController = new ProductController();

        String productId = getIntent().getStringExtra("productId");
        if (productId != null) {
            loadPopularDetail(productId);
            loadProductDetail(productId);
        } else {
            Toast.makeText(this, "Không tìm thấy mã sản phẩm", Toast.LENGTH_SHORT).show();
        }



        backBtn = findViewById(R.id.back_btn);
        backBtn.setOnClickListener(v -> finish());
    }

    private void loadProductDetail(String productId) {
        productController.getProductById(productId, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot item : snapshot.getChildren()) {
                    Product p = item.getValue(Product.class);
                    if (p != null) {
                        titleTxt.setText(p.getTitle());
                        descriptionTxt.setText(p.getDescription());
                        ratingTxt.setText(String.valueOf(p.getRating()));
                        priceTxt.setText(p.getPrice() + "$");
                        List<String> images = p.getPicUrl();
                        if (images != null && !images.isEmpty()) {
                            Glide.with(DetailProductActivity.this).load(images.get(0)).into(picMain);
                        }
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DetailProductActivity.this, "Lỗi khi tải sản phẩm", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadPopularDetail(String productId) {
        popularController.getProductById(productId, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot item : snapshot.getChildren()) {
                    Product p = item.getValue(Product.class);
                    if (p != null) {
                        titleTxt.setText(p.getTitle());
                        descriptionTxt.setText(p.getDescription());
                        ratingTxt.setText(String.valueOf(p.getRating()));
                        priceTxt.setText(p.getPrice() + "$");
                        List<String> images = p.getPicUrl();
                        if (images != null && !images.isEmpty()) {
                            Glide.with(DetailProductActivity.this).load(images.get(0)).into(picMain);
                        }
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DetailProductActivity.this, "Lỗi khi tải sản phẩm", Toast.LENGTH_SHORT).show();
            }
        });
    }
}