package com.example.onlinecoffeeshop.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.onlinecoffeeshop.R;
import com.example.onlinecoffeeshop.model.Product;
import com.example.onlinecoffeeshop.view.product.DetailProductActivity;

import java.util.List;

public class ProductAdapterWithViewTypes extends RecyclerView.Adapter<ProductAdapterWithViewTypes.ProductViewHolder> {

    public static final int VIEW_TYPE_LIST = 1;
    public static final int VIEW_TYPE_GRID = 2;

    private Context context;
    private List<Product> list;
    private int viewType = VIEW_TYPE_LIST; // Default to list view

    public ProductAdapterWithViewTypes(Context context, List<Product> list) {
        this.context = context;
        this.list = list;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return viewType;
    }

    @NonNull
    @Override
    public ProductAdapterWithViewTypes.ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_GRID) {
            view = LayoutInflater.from(context).inflate(R.layout.viewholder_product_grid, parent, false);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.viewholder_product_pic_right, parent, false);
        }
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductAdapterWithViewTypes.ProductViewHolder holder, int position) {
        Product p = list.get(position);
        holder.titleTxt.setText(p.getTitle());
        holder.priceTxt.setText("$" + p.getPrice());
        holder.ratingBar.setRating((float) p.getRating());

        if (p.getPicUrl() != null && !p.getPicUrl().isEmpty()) {
            Glide.with(context).load(p.getPicUrl().get(0)).into(holder.imageView);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DetailProductActivity.class);
                intent.putExtra("productId", p.getProductId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView titleTxt, priceTxt;
        RatingBar ratingBar;
        ImageView imageView;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTxt = itemView.findViewById(R.id.titleTxt);
            priceTxt = itemView.findViewById(R.id.priceTxt);
            imageView = itemView.findViewById(R.id.imageView9);
            ratingBar = itemView.findViewById(R.id.ratingBarList);
        }
    }
}
