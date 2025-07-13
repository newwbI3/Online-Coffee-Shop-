package com.example.onlinecoffeeshop.view.admin;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onlinecoffeeshop.R;
import com.example.onlinecoffeeshop.adapter.AdminProductAdapter;
import com.example.onlinecoffeeshop.model.Product;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AdminProductActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AdminProductAdapter adapter;
    private List<Product> productList = new ArrayList<>();
    private DatabaseReference dbRef;
    private ImageButton btnAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_product);

        recyclerView = findViewById(R.id.recyclerProducts);
        btnAdd = findViewById(R.id.btnAddProduct);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        dbRef = FirebaseDatabase.getInstance().getReference("Items");

        adapter = new AdminProductAdapter(productList, new AdminProductAdapter.OnProductClickListener() {
            @Override
            public void onEdit(Product product) {
                showProductDialog(product);
            }

            @Override
            public void onDelete(Product product) {
                confirmDelete(product);
            }
        });

        recyclerView.setAdapter(adapter);
        fetchProducts();

        btnAdd.setOnClickListener(v -> showProductDialog(null));
    }

    private void fetchProducts() {
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productList.clear();
                for (DataSnapshot itemSnap : snapshot.getChildren()) {
                    Product product = itemSnap.getValue(Product.class);
                    if (product != null) {
                        product.setProductId(itemSnap.getKey());
                        productList.add(product);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AdminProductActivity.this, "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void confirmDelete(Product product) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa sản phẩm \"" + product.getTitle() + "\"?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    dbRef.child(product.getProductId()).removeValue()
                            .addOnSuccessListener(unused ->
                                    Toast.makeText(this, "Đã xóa: " + product.getTitle(), Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e ->
                                    Toast.makeText(this, "Lỗi khi xóa: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void showProductDialog(Product productToEdit) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_product_form, null);
        builder.setView(view);
        builder.setTitle(productToEdit == null ? "Thêm sản phẩm" : "Sửa sản phẩm");
        builder.setPositiveButton(productToEdit == null ? "Thêm" : "Cập nhật", null);
        builder.setNegativeButton("Hủy", null);

        EditText edtTitle = view.findViewById(R.id.edtTitle);
        EditText edtDescription = view.findViewById(R.id.edtDescription);
        EditText edtExtra = view.findViewById(R.id.edtExtra);
        EditText edtImage = view.findViewById(R.id.edtImage);
        EditText edtPrice = view.findViewById(R.id.edtPrice);
        EditText edtRating = view.findViewById(R.id.edtRating);
        EditText edtCategoryId = view.findViewById(R.id.edtCategoryId);

        if (productToEdit != null) {
            edtTitle.setText(productToEdit.getTitle());
            edtDescription.setText(productToEdit.getDescription());
            edtExtra.setText(productToEdit.getExtra());
            if (productToEdit.getPicUrl() != null && !productToEdit.getPicUrl().isEmpty()) {
                edtImage.setText(productToEdit.getPicUrl().get(0));
            }
            edtPrice.setText(String.valueOf(productToEdit.getPrice()));
            edtRating.setText(String.valueOf(productToEdit.getRating()));
            edtCategoryId.setText(productToEdit.getCategoryId());
        }

        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String title = edtTitle.getText().toString().trim();
            String description = edtDescription.getText().toString().trim();
            String extra = edtExtra.getText().toString().trim();
            String image = edtImage.getText().toString().trim();
            String priceStr = edtPrice.getText().toString().trim();
            String ratingStr = edtRating.getText().toString().trim();
            String categoryId = edtCategoryId.getText().toString().trim();

            boolean hasError = false;

            if (TextUtils.isEmpty(title)) {
                edtTitle.setError("Vui lòng nhập tên sản phẩm");
                hasError = true;
            }

            if (TextUtils.isEmpty(image)) {
                edtImage.setError("Vui lòng nhập URL ảnh");
                hasError = true;
            }

            double price = 0;
            try {
                price = Double.parseDouble(priceStr);
                if (price < 0) {
                    edtPrice.setError("Giá phải lớn hơn 0");
                    hasError = true;
                }
            } catch (NumberFormatException e) {
                edtPrice.setError("Giá không hợp lệ");
                hasError = true;
            }

            double rating = 0;
            if (!TextUtils.isEmpty(ratingStr)) {
                try {
                    rating = Double.parseDouble(ratingStr);
                    if (rating < 0 || rating > 5) {
                        edtRating.setError("Đánh giá từ 0 đến 5");
                        hasError = true;
                    }
                } catch (NumberFormatException e) {
                    edtRating.setError("Đánh giá không hợp lệ");
                    hasError = true;
                }
            }

            if (hasError) return;

            List<String> imageList = new ArrayList<>();
            imageList.add(image);

            if (productToEdit == null) {
                // Thêm mới
                String id = UUID.randomUUID().toString();
                Product newProduct = new Product(title, description, extra, imageList, price, rating, categoryId);
                newProduct.setProductId(id);
                dbRef.child(id).setValue(newProduct);
                Toast.makeText(this, "Đã thêm sản phẩm", Toast.LENGTH_SHORT).show();
            } else {
                // Cập nhật
                productToEdit.setTitle(title);
                productToEdit.setDescription(description);
                productToEdit.setExtra(extra);
                productToEdit.setPicUrl(imageList);
                productToEdit.setPrice(price);
                productToEdit.setRating(rating);
                productToEdit.setCategoryId(categoryId);
                dbRef.child(productToEdit.getProductId()).setValue(productToEdit);
                Toast.makeText(this, "Đã cập nhật sản phẩm", Toast.LENGTH_SHORT).show();
            }

            dialog.dismiss();
        });
    }
}
