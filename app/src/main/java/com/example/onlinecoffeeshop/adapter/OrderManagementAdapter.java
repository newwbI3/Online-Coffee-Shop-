package com.example.onlinecoffeeshop.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.onlinecoffeeshop.R;

import com.example.onlinecoffeeshop.controller.OrderController;
import com.example.onlinecoffeeshop.model.CartItem;
import com.example.onlinecoffeeshop.model.Order;
import com.example.onlinecoffeeshop.view.order.OrderDetailActivity;
import com.google.gson.Gson;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class OrderManagementAdapter extends RecyclerView.Adapter<OrderManagementAdapter.OrderManagementViewHolder> {

    private Context context;
    private List<Order> orderList;


    public interface OnOrderActionListener {
        void onDeliverClicked(Order order);
        void onCancelClicked(Order order);
        void onConfirmReceivedClicked(Order order);
    }

    private OnOrderActionListener listener;

    public OrderManagementAdapter(Context context, List<Order> orderList, OnOrderActionListener listener) {
        this.context = context;
        this.orderList = orderList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public OrderManagementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order_manager, parent, false);
        return new OrderManagementViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderManagementViewHolder holder, int position) {
        Order order = orderList.get(position);

        // Set status text
        holder.tvStatus.setText("Trạng thái: " + order.getShipmentStatus());

        // Set total amount
        holder.tvTotal.setText("+ $" + order.getTotal());

        // Set up click to view detail
        holder.itemView.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(context, com.example.onlinecoffeeshop.view.order.OrderDetailActivity.class);
                intent.putExtra("order_json", new Gson().toJson(order)); // 👈 pass entire Order as JSON
                context.startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(context, "Không thể mở chi tiết đơn hàng", Toast.LENGTH_SHORT).show();
                Log.e("OrderAdapter", "Error launching OrderDetailActivity", e);
            }
        });

        holder.itemsContainer.removeAllViews();

        if (order.getItems() != null) {
            for (CartItem item : order.getItems()) {
                View itemView = LayoutInflater.from(context).inflate(R.layout.item_order_item, holder.itemsContainer, false);

                ImageView image = itemView.findViewById(R.id.iv_item_image);
                TextView title = itemView.findViewById(R.id.tv_item_title);
                TextView delivery = itemView.findViewById(R.id.tv_item_delivery);
                TextView priceQty = itemView.findViewById(R.id.tv_item_price_quantity);

                Glide.with(context).load(item.getImageUrl()).into(image);
                title.setText(item.getTitle());
                delivery.setText(order.getDeliveryMethod());
                priceQty.setText("+ " + formatCurrency(item.getPrice()) + "   " + item.getQuantity() + " item(s)");

                holder.itemsContainer.addView(itemView);
            }
        }

        // Show/hide buttons based on order status
        holder.btnDeliver.setVisibility(View.GONE);
        holder.btnCancel.setVisibility(View.GONE);
        holder.btnConfirmReceived.setVisibility(View.GONE);

        switch (order.getShipmentStatus()) {
            case "processing":
                holder.btnDeliver.setVisibility(View.VISIBLE);
                holder.btnCancel.setVisibility(View.VISIBLE);
                break;
            case "Delivering":
                holder.btnConfirmReceived.setVisibility(View.VISIBLE);
                holder.btnCancel.setVisibility(View.VISIBLE);
                break;
        }

        // Handle button clicks
        holder.btnDeliver.setOnClickListener(v -> listener.onDeliverClicked(order));
        holder.btnCancel.setOnClickListener(v -> listener.onCancelClicked(order));
        holder.btnConfirmReceived.setOnClickListener(v -> listener.onConfirmReceivedClicked(order));
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class OrderManagementViewHolder extends RecyclerView.ViewHolder {
        TextView tvStatus, tvTotal;
        LinearLayout itemsContainer;
        Button btnDeliver, btnCancel, btnConfirmReceived;

        public OrderManagementViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStatus = itemView.findViewById(R.id.tv_order_status);
            tvTotal = itemView.findViewById(R.id.tv_order_total);
            itemsContainer = itemView.findViewById(R.id.items_container);
            btnDeliver = itemView.findViewById(R.id.btn_deliver);
            btnCancel = itemView.findViewById(R.id.btn_cancel);
            btnConfirmReceived = itemView.findViewById(R.id.btn_confirm_received);
        }
    }

    private String formatCurrency(double value) {
        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.US);
        return format.format(value);
    }
    public void setOrders(List<Order> newOrders) {
        this.orderList.clear();
        this.orderList.addAll(newOrders); // ✅ Works with final lists
        Log.d("Adapter", "setOrders called with: " + newOrders.size() + " items");
        notifyDataSetChanged();// Refresh RecyclerView
    }
    public void updateOrderStatus(String orderId, String newStatus) {
        for (int i = 0; i < orderList.size(); i++) {
            Order order = orderList.get(i);
            OrderController orderController =new OrderController();
            if (order.getOrderId().equals(orderId)) {
                order.setShipmentStatus(newStatus);
                orderController.updateOrderStatus(order.getOrderId(), newStatus);
                notifyItemChanged(i);
                break;
            }
        }
    }

}
