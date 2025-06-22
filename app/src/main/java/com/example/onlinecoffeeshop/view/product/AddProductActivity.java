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

public class AddProductActivity extends AppCompatActivity {
private EditText edtName, edtCategoryId, edtPrice;
private Button btnCreate;
private ProductController productController;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_product);

        edtName = findViewById(R.id.edtName);
        edtCategoryId = findViewById(R.id.edtCategoryId);
        edtPrice = findViewById(R.id.edtPrice);
        btnCreate = findViewById(R.id.btnCreate);
        productController = new ProductController();

        btnCreate.setOnClickListener(v -> {
            String name = edtName.getText().toString();
            String categoryId = edtCategoryId.getText().toString();
            double price = Double.parseDouble(edtPrice.getText().toString());
            String createdAt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
            String product_Id = "prd" + System.currentTimeMillis(); //initial simple ID
            Product product = new Product(product_Id, name, categoryId, price, "", true, createdAt);

            productController.addProduct(product, product_Id, new ProductController.OnProductAddedListener() {
                @Override
                public void onSuccess() {
                    //
                    Toast.makeText(AddProductActivity.this, "Product added", Toast.LENGTH_SHORT).show();
                }
                @Override
                public void onFailure(String message) {
                    //
                    Toast.makeText(AddProductActivity.this, "Product added faileds ", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}