package com.example.onlinecoffeeshop.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.onlinecoffeeshop.R;
import com.example.onlinecoffeeshop.controller.FavouriteController;
import com.example.onlinecoffeeshop.model.FavouriteItem;
import com.example.onlinecoffeeshop.view.product.DetailProductActivity;
import java.util.List;

public class FavouriteAdapter extends RecyclerView.Adapter<FavouriteAdapter.FavViewHolder> {
    private Context context;
    private List<FavouriteItem> list;
    private FavouriteController controller;

    public FavouriteAdapter(Context context, List<FavouriteItem> list, String userId) {
        this.context = context;
        this.list = list;
        controller = new FavouriteController(userId);
    }

    @NonNull
    @Override
    public FavViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.viewholder_favorite, parent, false);
        return new FavViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavViewHolder holder, int position) {
        FavouriteItem item = list.get(position);
        holder.txtTitle.setText(item.getTitle());
        holder.txtPrice.setText("$" + item.getPrice());
        Glide.with(context).load(item.getImageUrl()).into(holder.imgProduct);
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailProductActivity.class);
            intent.putExtra("productId", item.getProductId());
            context.startActivity(intent);
        });
        holder.removeBtn.setOnClickListener(v -> {
            controller.removeFavourite(item.getFavId());
            list.remove(position);
            notifyItemRemoved(position);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class FavViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct, removeBtn;
        TextView txtTitle, txtPrice;
        public FavViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgFav);
            txtTitle = itemView.findViewById(R.id.txtFavTitle);
            txtPrice = itemView.findViewById(R.id.txtFavPrice);
            removeBtn = itemView.findViewById(R.id.removeFavBtn);
        }
    }
}
