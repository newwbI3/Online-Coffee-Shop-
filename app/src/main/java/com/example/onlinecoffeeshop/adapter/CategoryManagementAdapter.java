package com.example.onlinecoffeeshop.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onlinecoffeeshop.R;
import com.example.onlinecoffeeshop.model.Category;

import java.util.List;

public class CategoryManagementAdapter extends RecyclerView.Adapter<CategoryManagementAdapter.CategoryViewHolder> {
    
    private Context context;
    private List<Category> categoryList;
    private OnCategoryActionListener listener;

    public interface OnCategoryActionListener {
        void onEditCategory(Category category);
        void onDeleteCategory(Category category);
    }

    public CategoryManagementAdapter(Context context, List<Category> categoryList, OnCategoryActionListener listener) {
        this.context = context;
        this.categoryList = categoryList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.viewholder_category_management, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categoryList.get(position);
        
        holder.tvCategoryId.setText("ID: " + category.getId());
        holder.tvCategoryTitle.setText(category.getTitle());
        
        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditCategory(category);
            }
        });
        
        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteCategory(category);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategoryId, tvCategoryTitle;
        ImageButton btnEdit, btnDelete;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategoryId = itemView.findViewById(R.id.tvCategoryId);
            tvCategoryTitle = itemView.findViewById(R.id.tvCategoryTitle);
            btnEdit = itemView.findViewById(R.id.btnEditCategory);
            btnDelete = itemView.findViewById(R.id.btnDeleteCategory);
        }
    }
}
