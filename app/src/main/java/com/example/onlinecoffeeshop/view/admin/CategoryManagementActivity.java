package com.example.onlinecoffeeshop.view.admin;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onlinecoffeeshop.R;
import com.example.onlinecoffeeshop.adapter.CategoryManagementAdapter;
import com.example.onlinecoffeeshop.controller.CategoryController;
import com.example.onlinecoffeeshop.model.Category;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CategoryManagementActivity extends AppCompatActivity {
    private static final String TAG = "CategoryManagementActivity";

    private RecyclerView recyclerViewCategories;
    private CategoryManagementAdapter categoryAdapter;
    private ProgressBar progressBar;
    private LinearLayout emptyStateLayout;
    private ImageButton btnAddCategory;

    private List<Category> categoryList;
    private CategoryController categoryController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_management);

        initViews();
        setupBackButton();
        setupRecyclerView();
        loadCategories();

        btnAddCategory.setOnClickListener(v -> showCategoryDialog(null));
    }

    private void initViews() {
        recyclerViewCategories = findViewById(R.id.recyclerViewCategories);
        progressBar = findViewById(R.id.progressBar);
        emptyStateLayout = findViewById(R.id.emptyStateLayout);
        btnAddCategory = findViewById(R.id.btnAddCategory);

        categoryController = new CategoryController();
        categoryList = new ArrayList<>();
    }

    private void setupBackButton() {
        findViewById(R.id.back_btn).setOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        categoryAdapter = new CategoryManagementAdapter(this, categoryList, new CategoryManagementAdapter.OnCategoryActionListener() {
            @Override
            public void onEditCategory(Category category) {
                showCategoryDialog(category);
            }

            @Override
            public void onDeleteCategory(Category category) {
                confirmDeleteCategory(category);
            }
        });

        recyclerViewCategories.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewCategories.setAdapter(categoryAdapter);
    }

    private void loadCategories() {
        progressBar.setVisibility(View.VISIBLE);

        categoryController.getAllCategories(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressBar.setVisibility(View.GONE);
                categoryList.clear();

                for (DataSnapshot item : snapshot.getChildren()) {
                    Category category = item.getValue(Category.class);
                    if (category != null) {
                        categoryList.add(category);
                    }
                }

                categoryAdapter.notifyDataSetChanged();
                updateEmptyState();
                Log.d(TAG, "Loaded " + categoryList.size() + " categories");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Log.e(TAG, "Error loading categories: ", error.toException());
                Toast.makeText(CategoryManagementActivity.this, "Lỗi khi tải danh sách danh mục", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showCategoryDialog(Category categoryToEdit) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_category_form, null);
        builder.setView(view);
        builder.setTitle(categoryToEdit == null ? "Thêm danh mục" : "Sửa danh mục");
        builder.setPositiveButton(categoryToEdit == null ? "Thêm" : "Cập nhật", null);
        builder.setNegativeButton("Hủy", null);

        EditText edtId = view.findViewById(R.id.edtCategoryId);
        EditText edtTitle = view.findViewById(R.id.edtCategoryTitle);

        if (categoryToEdit != null) {
            edtId.setText(String.valueOf(categoryToEdit.getId()));
            edtTitle.setText(categoryToEdit.getTitle());
            edtId.setEnabled(false); // Don't allow editing ID
        }

        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String idStr = edtId.getText().toString().trim();
            String title = edtTitle.getText().toString().trim();

            boolean hasError = false;

            if (TextUtils.isEmpty(idStr)) {
                edtId.setError("Vui lòng nhập ID danh mục");
                hasError = true;
            }

            if (TextUtils.isEmpty(title)) {
                edtTitle.setError("Vui lòng nhập tên danh mục");
                hasError = true;
            }

            int id = 0;
            try {
                id = Integer.parseInt(idStr);
                if (id <= 0) {
                    edtId.setError("ID danh mục phải lớn hơn 0");
                    hasError = true;
                }
            } catch (NumberFormatException e) {
                edtId.setError("ID danh mục không hợp lệ");
                hasError = true;
            }

            if (hasError) return;

            if (categoryToEdit == null) {
                // Check if ID already exists
                for (Category existingCategory : categoryList) {
                    if (existingCategory.getId() == id) {
                        edtId.setError("ID danh mục đã tồn tại");
                        return;
                    }
                }
                // Add new category
                categoryController.createCategory(id, title);
                Toast.makeText(this, "Đã thêm danh mục", Toast.LENGTH_SHORT).show();
            } else {
                // Update existing category
                categoryController.updateCategoryTitle(categoryToEdit.getId(), title);
                Toast.makeText(this, "Đã cập nhật danh mục", Toast.LENGTH_SHORT).show();
            }

            dialog.dismiss();
            loadCategories(); // Reload the list
        });
    }

    private void confirmDeleteCategory(Category category) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa danh mục \"" + category.getTitle())
                .setPositiveButton("Xóa", (dialog, which) -> {
                    categoryController.deleteCategory(category.getId());
                    Toast.makeText(this, "Đã xóa danh mục: " + category.getTitle(), Toast.LENGTH_SHORT).show();
                    loadCategories(); // Reload the list
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void updateEmptyState() {
        if (categoryList.isEmpty()) {
            recyclerViewCategories.setVisibility(View.GONE);
            emptyStateLayout.setVisibility(View.VISIBLE);
        } else {
            recyclerViewCategories.setVisibility(View.VISIBLE);
            emptyStateLayout.setVisibility(View.GONE);
        }
    }
}
