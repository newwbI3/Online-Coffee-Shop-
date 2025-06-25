package com.example.onlinecoffeeshop.view.product;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.onlinecoffeeshop.R;
import com.example.onlinecoffeeshop.controller.ProductController;
import com.example.onlinecoffeeshop.model.Product;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class AddProductActivity extends AppCompatActivity {
    private EditText edtName, edtCategoryId, edtDescription,edtPrice, edtImageUrl;
    private CheckBox chkIsActive;
    private Button btnAdd;
    private ProductController productController;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_product);

        // Bind UI
        edtName = findViewById(R.id.edtName);
        edtCategoryId = findViewById(R.id.edtCategoryId);
        edtPrice = findViewById(R.id.edtPrice);
        edtImageUrl = findViewById(R.id.edtImageUrl);
        edtDescription = findViewById(R.id.edtDescription);
        chkIsActive = findViewById(R.id.chkIsActive);
        btnAdd = findViewById(R.id.btnAction);

        productController = new ProductController();

        btnAdd.setOnClickListener(v -> {
            String name = edtName.getText().toString().trim();
            String categoryId = edtCategoryId.getText().toString().trim();
            String priceStr = edtPrice.getText().toString().trim();
            String description = edtDescription.getText().toString().trim();
            String imageUrl = edtImageUrl.getText().toString().trim();
            boolean isActive = chkIsActive.isChecked();

            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(priceStr) || TextUtils.isEmpty(imageUrl)) {
                Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
                return;
            }

            double price = Double.parseDouble(priceStr);
            String productId = UUID.randomUUID().toString();
            String createdAt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

            Product product = new Product(productId, name, categoryId, price, description, imageUrl, isActive, createdAt);

            productController.addProduct(product, productId, new ProductController.OnProductAddedListener() {
                @Override
                public void onSuccess() {
                    Toast.makeText(AddProductActivity.this, "Product added!", Toast.LENGTH_SHORT).show();
                    finish();
                }

                @Override
                public void onFailure(String message) {
                    Toast.makeText(AddProductActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}