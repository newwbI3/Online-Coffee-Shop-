package com.example.onlinecoffeeshop.view.product;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;


import com.bumptech.glide.Glide;
import com.example.onlinecoffeeshop.R;
import com.example.onlinecoffeeshop.controller.CartController;
import com.example.onlinecoffeeshop.controller.PopularController;
import com.example.onlinecoffeeshop.controller.ProductController;
import com.example.onlinecoffeeshop.model.CartItem;
import com.example.onlinecoffeeshop.model.Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.UUID;

public class DetailProductActivity extends AppCompatActivity {
    private final String userId = "guest";
    private ImageView picMain;
    private TextView titleTxt, descriptionTxt, ratingTxt, priceTxt, quantityTxt, plusBtn, minusBtn;
    private TextView backBtn, smallTxt, mediumTxt, largeTxt;
    private ConstraintLayout addToCartBtn;
    private int quantity = 1;
    private Product selectedProduct;
    private String selectedSize = "Small";
    private PopularController popularController;
    private ProductController productController;
    private CartController cartController;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detail_product);

        initViews();

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
        plusBtn.setOnClickListener(v -> {
            quantity++;
            if(quantity == 1){
                priceTxt.setText(String.format("%.2f$", selectedProduct.getPrice()));
            }if(quantity >= 2) {
                quantityTxt.setText(String.valueOf(quantity));
                updatePriceText();
            }
        });

        minusBtn.setOnClickListener(v -> {
                quantity--;
                if (quantity == 0) {
                    quantity = 1;
                }
                quantityTxt.setText(String.valueOf(quantity));
                updatePriceText();
        });
        smallTxt.setOnClickListener(v -> {
            selectedSize = "Small";
            updateSizeUI();
            if (selectedProduct != null){
                updatePriceText();
            }
        });
        mediumTxt.setOnClickListener(v -> {
            selectedSize = "Medium";
            updateSizeUI();
            if (selectedProduct != null){
                updatePriceText();
            }
        });
        largeTxt.setOnClickListener(v -> {
            selectedSize = "Large";
            updateSizeUI();
            if (selectedProduct != null){
                updatePriceText();
            }
        });
        addToCartBtn.setOnClickListener(v -> {
            if (selectedProduct != null) {
                CartItem item = new CartItem();
                item.setProductId(selectedProduct.getProductId());
                item.setTitle(selectedProduct.getTitle() + " (" + selectedSize + ")");
                item.setImageUrl(selectedProduct.getPicUrl().get(0));
                item.setPrice(getPriceBySize());
                item.setQuantity(quantity);

                String cartId = UUID.randomUUID().toString();
                FirebaseDatabase.getInstance().getReference("Cart")
                        .child("guest")
                        .child(cartId)
                        .setValue(item)
                        .addOnSuccessListener(unused -> {
                            Toast.makeText(this, "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            }
        });
        backBtn.setOnClickListener(v -> finish());
    }
    private void updatePriceText() {
        double total = getPriceBySize() * quantity;
        priceTxt.setText(String.format("%.2f$", total));
    }
    private double getPriceBySize() {
        double basePrice = selectedProduct.getPrice();
        switch (selectedSize) {
            case "Small" : return basePrice;
            case "Medium": return  basePrice + 2.0;
            case "Large": return basePrice + 4.0;
            default: return basePrice;
        }
    }
    private void updateSizeUI() {
        smallTxt.setBackgroundResource(selectedSize.equals("Small") ? R.drawable.brown_full_corner : android.R.color.transparent);
        mediumTxt.setBackgroundResource(selectedSize.equals("Medium") ? R.drawable.brown_full_corner : android.R.color.transparent);
        largeTxt.setBackgroundResource(selectedSize.equals("Large") ? R.drawable.brown_full_corner : android.R.color.transparent);

        int white = getResources().getColor(R.color.white);
        int brown = getResources().getColor(R.color.darkBrown);

        smallTxt.setTextColor(selectedSize.equals("Small") ? white : brown);
        mediumTxt.setTextColor(selectedSize.equals("Medium") ? white : brown);
        largeTxt.setTextColor(selectedSize.equals("Large") ? white : brown);
    }
    private void initViews() {
        picMain = findViewById(R.id.picMain);
        titleTxt = findViewById(R.id.titleTxt);
        descriptionTxt = findViewById(R.id.descriptionTxt);
        ratingTxt = findViewById(R.id.ratingTxt);
        priceTxt = findViewById(R.id.priceTxt);
        quantityTxt = findViewById(R.id.quantityTxt);
        plusBtn = findViewById(R.id.plusCart_btn);
        minusBtn = findViewById(R.id.minusCart_btn);
        backBtn = findViewById(R.id.back_btn);
        addToCartBtn = findViewById(R.id.addToCart_btn);
        smallTxt = findViewById(R.id.smallTxt);
        mediumTxt = findViewById(R.id.mediumTxt);
        largeTxt = findViewById(R.id.largeTxt);
    }

    private void loadProductDetail(String productId) {
        productController.getProductById(productId, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot item : snapshot.getChildren()) {
                    Product p = item.getValue(Product.class);
                    if (p != null) {
                        selectedProduct = p;
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
                        selectedProduct = p;
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