package com.example.onlinecoffeeshop.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.onlinecoffeeshop.R;
import com.example.onlinecoffeeshop.model.CartItem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class CartAdapter  extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private Context context;
    private List<CartItem> cartItems;
    private CartUpdateListener cartUpdateListener;
    private String userId;

    public CartAdapter(Context context, List<CartItem> cartItems, String userId, CartUpdateListener listener) {
        this.context = context;
        this.cartItems = cartItems;
        this.userId = userId;
        this.cartUpdateListener = listener;
    }

    @NonNull
    @Override
    public CartAdapter.CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.viewholder_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartAdapter.CartViewHolder holder, int position) {
        CartItem item = cartItems.get(position);
        Glide.with(context).load(item.getImageUrl()).into(holder.picCart);
        holder.titleTxt.setText(item.getTitle());
        holder.quantityTxt.setText(String.valueOf(item.getQuantity()));
        holder.feeEachItem.setText(String.format("$%.2f", item.getPrice()));
        holder.totalEachItem.setText(String.format("$%.2f", item.getPrice() * item.getQuantity()));

        holder.plusEachItem.setOnClickListener(v -> {
            int newQty = item.getQuantity() + 1;
            updateQuantity(item, newQty);
        });

        holder.minusEachItem.setOnClickListener(v -> {
            if (item.getQuantity() > 1) {
                int newQty = item.getQuantity() - 1;
                updateQuantity(item, newQty);
            }
        });

        holder.removeItem.setOnClickListener(v -> {
            FirebaseDatabase.getInstance().getReference("Cart")
                    .child(userId)
                    .orderByChild("productId")
                    .equalTo(item.getProductId())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot snap : snapshot.getChildren()) {
                                snap.getRef().removeValue();
                            }
                            Toast.makeText(context, "Đã xoá sản phẩm", Toast.LENGTH_SHORT).show();
                            cartUpdateListener.onCartUpdated();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(context, "Lỗi khi xoá", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }
    private void updateQuantity(CartItem item, int newQuantity) {
        FirebaseDatabase.getInstance().getReference("Cart")
                .child(userId)
                .orderByChild("productId")
                .equalTo(item.getProductId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot snap : snapshot.getChildren()) {
                            snap.getRef().child("quantity").setValue(newQuantity);
                        }
                        cartUpdateListener.onCartUpdated();  // Gọi callback
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(context, "Lỗi khi cập nhật", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    public interface CartUpdateListener {
        void onCartUpdated();
    }
    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public class CartViewHolder extends RecyclerView.ViewHolder {
        TextView titleTxt, quantityTxt, feeEachItem, totalEachItem;
        TextView minusEachItem, plusEachItem;
        ImageView picCart, removeItem;
        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTxt = itemView.findViewById(R.id.titleTxt);
            quantityTxt = itemView.findViewById(R.id.quantityTxt);
            feeEachItem = itemView.findViewById(R.id.feeEachItem);
            totalEachItem = itemView.findViewById(R.id.totalEachItem);
            minusEachItem = itemView.findViewById(R.id.minusEachItem);
            plusEachItem = itemView.findViewById(R.id.plusEachItem);
            picCart = itemView.findViewById(R.id.picCart);
            removeItem = itemView.findViewById(R.id.removeItem);
        }
    }
}
