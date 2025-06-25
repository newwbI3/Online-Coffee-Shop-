package com.example.onlinecoffeeshop.view.product;

import android.os.Bundle;
import android.widget.Button;
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

public class UpdateProductActivity extends AppCompatActivity {
    private EditText edtName, edtCategoryId,edtDescription, edtPrice, edtImageUrl;
    private Button btnUpdate;
    private String productId;
    private ProductController controller;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_product);

        productId = getIntent().getStringExtra("product_id");

        edtName = findViewById(R.id.edtName);
        edtCategoryId = findViewById(R.id.edtCategoryId);
        edtPrice = findViewById(R.id.edtPrice);
        edtImageUrl = findViewById(R.id.edtImageUrl);
        edtDescription = findViewById(R.id.edtDescription);
        btnUpdate = findViewById(R.id.btnAction);
        // Set old values
        edtName.setText(getIntent().getStringExtra("name"));
        edtCategoryId.setText(getIntent().getStringExtra("category_id"));
        edtPrice.setText(String.valueOf(getIntent().getDoubleExtra("price", 0.0)));
        edtImageUrl.setText(getIntent().getStringExtra("image_url"));
        edtDescription.setText(getIntent().getStringExtra("description"));

        controller = new ProductController();

        btnUpdate.setOnClickListener(v -> {
            String name = edtName.getText().toString();
            String catId = edtCategoryId.getText().toString();
            double price = Double.parseDouble(edtPrice.getText().toString());
            String imageUrl = edtImageUrl.getText().toString();
            String description = edtDescription.getText().toString();

            Product product = new Product(productId, name, catId, price, description,imageUrl, true, getCurrentTime());

            controller.updateProduct(product, new ProductController.OnProductUpdatedListener() {
                @Override
                public void onSuccess() {
                    Toast.makeText(UpdateProductActivity.this, "Updated!", Toast.LENGTH_SHORT).show();
                    finish();
                }
                @Override
                public void onFailure(String message) {
                    Toast.makeText(UpdateProductActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
    private String getCurrentTime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
    }
}