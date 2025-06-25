package com.example.onlinecoffeeshop.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.onlinecoffeeshop.R;
import com.example.onlinecoffeeshop.controller.ProductController;
import com.example.onlinecoffeeshop.model.Product;
import com.example.onlinecoffeeshop.view.product.ProductDetailActivity;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private List<Product> list;

    public ProductAdapter(List<Product> list) {
        this.list = list;
    }
    @NonNull
    @Override
    public ProductAdapter.ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductAdapter.ProductViewHolder holder, int position) {
        Product p = list.get(position);
        Context context = holder.itemView.getContext();
        holder.name.setText(p.getName());
        holder.price.setText("$" + p.getPrice());
       // holder.description.setText(p.getDescription());
        Glide.with(holder.img.getContext()).load(p.getImage_url()).into(holder.img);
        holder.itemView.setOnClickListener(v -> {
            // Handle item click
            Intent intent = new Intent(holder.itemView.getContext(), ProductDetailActivity.class);
            intent.putExtra("product_id", p.getProduct_id());
            intent.putExtra("name", p.getName());
            intent.putExtra("description", p.getDescription());
            intent.putExtra("price", p.getPrice());
            intent.putExtra("image_url", p.getImage_url());
            v.getContext().startActivity(intent);
        });
        holder.btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Xóa sản phẩm")
                    .setMessage("Bạn có chắc muốn xóa sản phẩm này không?")
                    .setPositiveButton("Xóa", (dialog, which) -> {
                        ProductController controller = new ProductController();
                        controller.deleteProduct(p.getProduct_id(), new ProductController.OnProductDeletedListener() {
                            @Override
                            public void onSuccess() {
                                int indexToRemove = holder.getAdapterPosition();
                                if (indexToRemove != RecyclerView.NO_POSITION) {
                                    list.remove(indexToRemove);
                                    notifyItemRemoved(indexToRemove);
                                }
                                Toast.makeText(context, "Đã xóa sản phẩm", Toast.LENGTH_SHORT).show();
                            }
                            @Override
                            public void onFailure(String message) {
                                Toast.makeText(context, "Lỗi: " + message, Toast.LENGTH_SHORT).show();
                            }
                        });
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView name, price, description;
        Button btnDelete;
        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.imgProduct);
            name = itemView.findViewById(R.id.txtName);
            price = itemView.findViewById(R.id.txtPrice);
            description = itemView.findViewById(R.id.txtDescription);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
