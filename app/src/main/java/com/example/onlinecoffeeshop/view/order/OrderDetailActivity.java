// OrderDetailActivity.java
package com.example.onlinecoffeeshop.view.order;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.onlinecoffeeshop.R;
import com.example.onlinecoffeeshop.model.CartItem;
import com.example.onlinecoffeeshop.model.Order;
import com.google.gson.Gson;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class OrderDetailActivity extends AppCompatActivity {

    private TextView tvOrderId, tvCustomer, tvAddress, tvPhone, tvNote, tvTotal, tvStatus;
    private LinearLayout itemsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        // Init views
        tvOrderId = findViewById(R.id.tv_order_id);
        tvCustomer = findViewById(R.id.tv_customer_name);
        tvAddress = findViewById(R.id.tv_address);
        tvPhone = findViewById(R.id.tv_phone);
        tvNote = findViewById(R.id.tv_note);
        tvTotal = findViewById(R.id.tv_total);
        tvStatus = findViewById(R.id.tv_status);
        itemsContainer = findViewById(R.id.items_container);

        // Deserialize Order from Intent
        String orderJson = getIntent().getStringExtra("order_json");
        Order order = new Gson().fromJson(orderJson, Order.class);

        ImageView backBtn = findViewById(R.id.backBtnOrder);
        backBtn.setOnClickListener(v -> finish());
        // Bind data
        tvOrderId.setText("Mã đơn: " + order.getOrderId());
        tvCustomer.setText("Tên: " + order.getFullName());
        tvAddress.setText("Địa chỉ: " + order.getAddress());
        tvPhone.setText("SĐT: " + order.getPhone());
        tvNote.setText("Ghi chú: " + (order.getNote().isEmpty() ? "Không có" : order.getNote()));
        tvTotal.setText("Tổng: " + formatCurrency(order.getTotal()));
        tvStatus.setText("Trạng thái: " + mapStatus(order.getShipmentStatus()));

        // Load items
        for (CartItem item : order.getItems()) {
            View itemView = getLayoutInflater().inflate(R.layout.item_order_item, itemsContainer, false);

            ImageView image = itemView.findViewById(R.id.iv_item_image);
            TextView title = itemView.findViewById(R.id.tv_item_title);
            TextView delivery = itemView.findViewById(R.id.tv_item_delivery);
            TextView priceQty = itemView.findViewById(R.id.tv_item_price_quantity);

            Glide.with(this).load(item.getImageUrl()).into(image);
            title.setText(item.getTitle());
            delivery.setText(order.getDeliveryMethod());
            priceQty.setText("+ " + formatCurrency(item.getPrice()) + "   " + item.getQuantity() + " item(s)");

            itemsContainer.addView(itemView);
        }
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
