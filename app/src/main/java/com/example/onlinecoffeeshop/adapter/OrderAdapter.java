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
import com.example.onlinecoffeeshop.view.order.FeedbackActivity;
import com.example.onlinecoffeeshop.view.order.OrderDetailActivity;
import com.google.gson.Gson;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private final List<Order> orderList;
    private final Context context;
    private final OrderController orderController;

    public OrderAdapter(Context context, List<Order> orderList) {
        this.context = context;
        this.orderList = orderList;
        this.orderController = new OrderController();
    }

    public void updateOrderList(List<Order> newOrders) {
        orderList.clear();
        orderList.addAll(newOrders);
        notifyDataSetChanged();
    }

    public void addOrder(Order order) {
        orderList.add(0, order);
        notifyItemInserted(0);
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);
        if (order == null) return;

        holder.tvOrderTotal.setText("Tổng tiền: " + formatCurrency(order.getTotal()));
        holder.tvOrderStatus.setText("Trạng thái: " + mapStatus(order.getShipmentStatus()));

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

        // Confirm Received Button
        String mappedStatus = mapStatus(order.getShipmentStatus());
        if ("Đang xử lý".equals(mappedStatus) || "Đang giao".equals(mappedStatus)) {
            holder.btnConfirmReceived.setVisibility(View.VISIBLE);
            holder.btnConfirmReceived.setOnClickListener(v -> {
                order.setShipmentStatus("delivered");
                orderController.updateOrderStatus(order.getOrderId(), "delivered");
                Toast.makeText(context, "Cảm ơn bạn đã xác nhận!", Toast.LENGTH_SHORT).show();
                notifyItemChanged(position);
            });
        } else {
            holder.btnConfirmReceived.setVisibility(View.GONE);
        }

        // View Details Button
        holder.btnViewDetails.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(context, OrderDetailActivity.class);
                intent.putExtra("order_json", new Gson().toJson(order)); // 👈 pass entire Order as JSON
                context.startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(context, "Không thể mở chi tiết đơn hàng", Toast.LENGTH_SHORT).show();
                Log.e("OrderAdapter", "Error launching OrderDetailActivity", e);
            }
        });

        // Feedback Button
        if ("Đã giao".equals(mappedStatus)) {
            holder.btnFeedback.setVisibility(View.VISIBLE);
            holder.btnFeedback.setOnClickListener(v -> {
                Intent intent = new Intent(context, FeedbackActivity.class);
                intent.putExtra("orderId", order.getOrderId());
                context.startActivity(intent);
            });
        } else {
            holder.btnFeedback.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderTotal, tvOrderStatus;
        LinearLayout itemsContainer;
        Button btnConfirmReceived, btnViewDetails, btnFeedback;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderTotal = itemView.findViewById(R.id.tv_order_total);
            tvOrderStatus = itemView.findViewById(R.id.tv_order_status);
            itemsContainer = itemView.findViewById(R.id.items_container);
            btnConfirmReceived = itemView.findViewById(R.id.btn_confirm_received);
            btnViewDetails = itemView.findViewById(R.id.btn_view_details);
            btnFeedback = itemView.findViewById(R.id.btn_feedback);
        }
    }

    private String formatCurrency(double value) {
        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.US);
        return format.format(value);
    }

    private String mapStatus(String status) {
        if (status == null) return "Đang xử lý";
        switch (status.toLowerCase()) {
            case "processing":
                return "Đang xử lý";
            case "shipping":
                return "Đang giao";
            case "delivered":
                return "Đã giao";
            case "cancelled":
                return "Đã hủy";
            default:
                return status;
        }
    }

}