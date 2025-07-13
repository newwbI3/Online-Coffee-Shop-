package com.example.onlinecoffeeshop.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.onlinecoffeeshop.R;
import com.example.onlinecoffeeshop.model.Product;
import java.util.List;

public class AdminProductAdapter extends RecyclerView.Adapter<AdminProductAdapter.ProductViewHolder> {

    public interface OnProductClickListener {
        void onEdit(Product product);
        void onDelete(Product product);
    }

    private List<Product> productList;
    private OnProductClickListener listener;

    public AdminProductAdapter(List<Product> productList, OnProductClickListener listener) {
        this.productList = productList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.tvTitle.setText(product.getTitle());
        holder.tvPrice.setText("$" + product.getPrice());
        holder.tvDesc.setText(product.getDescription());

        if (product.getPicUrl() != null && !product.getPicUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(product.getPicUrl().get(0))
                    .into(holder.ivProduct);
        }

        holder.btnEdit.setOnClickListener(v -> listener.onEdit(product));
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(product));
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvPrice, tvDesc;
        ImageView ivProduct;
        Button btnEdit, btnDelete;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvProductName);
            tvPrice = itemView.findViewById(R.id.tvProductPrice);
            tvDesc = itemView.findViewById(R.id.tvProductDesc);
            ivProduct = itemView.findViewById(R.id.ivProduct);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
