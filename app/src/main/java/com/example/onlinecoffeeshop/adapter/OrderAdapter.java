package com.example.onlinecoffeeshop.view.order;

import android.content.Context;
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

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
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

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);

        holder.tvOrderId.setText("Mã đơn: " + order.getOrderId());
        holder.tvOrderTotal.setText("Tổng tiền: " + formatCurrency(order.getTotal()));
        holder.tvOrderDate.setText("Ngày đặt: " + formatDate(order.getTimestamp()));
        holder.tvOrderStatus.setText("Trạng thái: " + mapStatus(order.getShipmentStatus()));

        // Clear and re-add all items
        holder.itemsContainer.removeAllViews();
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

        // Handle confirmation button
        if ("Đang xử lý".equals(mapStatus(order.getShipmentStatus())) || "Đang giao".equals(mapStatus(order.getShipmentStatus()))) {
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
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvOrderTotal, tvOrderDate, tvOrderStatus;
        LinearLayout itemsContainer;
        Button btnConfirmReceived;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tv_order_id);
            tvOrderTotal = itemView.findViewById(R.id.tv_order_total);
            tvOrderDate = itemView.findViewById(R.id.tv_order_date);
            tvOrderStatus = itemView.findViewById(R.id.tv_order_status);
            itemsContainer = itemView.findViewById(R.id.items_container);
            btnConfirmReceived = itemView.findViewById(R.id.btn_confirm_received);
        }
    }

    private String formatDate(long timestamp) {
        return new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                .format(new Date(timestamp));
    }

    private String formatCurrency(double value) {
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        return format.format(value).replace("₫", "VNĐ");
    }

    private String mapStatus(String status) {
        if (status == null) return "Đang xử lý";
        switch (status.toLowerCase()) {
            case "processing": return "Đang xử lý";
            case "shipping": return "Đang giao";
            case "delivered": return "Đã giao";
            case "cancelled": return "Đã hủy";
            default: return status;
        }
    }
}
