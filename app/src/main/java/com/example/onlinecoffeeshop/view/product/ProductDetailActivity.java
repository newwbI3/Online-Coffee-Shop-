package com.example.onlinecoffeeshop.view.product;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.onlinecoffeeshop.R;
import com.example.onlinecoffeeshop.controller.CartController;
import com.example.onlinecoffeeshop.controller.ProductController;
import com.example.onlinecoffeeshop.model.CartItem;

public class ProductDetailActivity extends AppCompatActivity {

    private ImageView imgProduct;
    private TextView txtName, txtPrice, txtDescription;
    private Button btnAddToCart, btnUpdate, btnDelete;

    private String productId, name, description, imageUrl;
    private double price;
    private CartController cartController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product_detail);

        // Bind View
        imgProduct = findViewById(R.id.imgProduct);
        txtName = findViewById(R.id.txtName);
        txtPrice = findViewById(R.id.txtPrice);
        txtDescription = findViewById(R.id.txtDescription);
        btnAddToCart = findViewById(R.id.btnAddToCart);
        btnUpdate = findViewById(R.id.btnUpdate);
        // Get data from intent
        productId = getIntent().getStringExtra("product_id");
        name = getIntent().getStringExtra("name");
        description = getIntent().getStringExtra("description");
        imageUrl = getIntent().getStringExtra("image_url");
        price = getIntent().getDoubleExtra("price", 0.0);

        // Show product data
        txtName.setText(name);
        txtPrice.setText("$" + price);
        txtDescription.setText(description);
        Glide.with(this).load(imageUrl).into(imgProduct);

        cartController = new CartController();
        btnAddToCart.setOnClickListener(v -> {
            CartItem item = new CartItem(productId, name, price, 1, imageUrl);
            cartController.addToCart(item, new CartController.OnCartAddListener() {
                @Override
                public void onSuccess() {
                    Toast.makeText(ProductDetailActivity.this, "Added to cart!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(String error) {
                    Toast.makeText(ProductDetailActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                }
            });
        });

        btnUpdate.setOnClickListener(v -> {
            Intent intent = new Intent(ProductDetailActivity.this, UpdateProductActivity.class);
            intent.putExtra("product_id", productId);
            intent.putExtra("name", name);
            intent.putExtra("category_id", getIntent().getStringExtra("category_id"));
            intent.putExtra("price", price);
            intent.putExtra("image_url", imageUrl);
            intent.putExtra("description", description);
            startActivity(intent);
        });


    }
}