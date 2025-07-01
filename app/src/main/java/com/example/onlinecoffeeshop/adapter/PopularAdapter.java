package com.example.onlinecoffeeshop.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.onlinecoffeeshop.R;
import com.example.onlinecoffeeshop.model.Product;

import java.util.List;

public class PopularAdapter extends RecyclerView.Adapter<PopularAdapter.PopularViewHolder> {
    private List<Product> list;
    private final Context context;

    public PopularAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.list = productList;
    }


    @NonNull
    @Override
    public PopularAdapter.PopularViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.viewholder_popular, parent, false);
        return new PopularViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PopularAdapter.PopularViewHolder holder, int position) {
        Product product = list.get(position);
        holder.txtTitle.setText(product.getTitle());
        holder.txtPrice.setText("$" + product.getPrice());

        List<String> images = product.getPicUrl();
        if (images != null && !images.isEmpty()) {
            Glide.with(context)
                    .load(images.get(0)) // Load ảnh đầu tiên
                    .into(holder.imgProduct);
        } else {
            holder.imgProduct.setImageResource(R.drawable.coffee_svgrepo_com);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class PopularViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle, txtPrice;
        ImageView imgProduct;

        public PopularViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtName);
            txtPrice = itemView.findViewById(R.id.txtPrice);
            imgProduct = itemView.findViewById(R.id.imgProduct);
        }
    }
}
