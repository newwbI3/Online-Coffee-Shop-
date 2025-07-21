package com.example.onlinecoffeeshop.view.admin;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onlinecoffeeshop.R;
import com.example.onlinecoffeeshop.adapter.OrderManagementAdapter;
import com.example.onlinecoffeeshop.controller.OrderController;
import com.example.onlinecoffeeshop.model.Order;

import java.util.ArrayList;
import java.util.List;

public class OrderManagementActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private OrderManagementAdapter orderAdapter;
    private List<Order> allOrders = new ArrayList<>();

    private Button btnAll, btnProcessing, btnShipping, btnDelivered, btnCancelled;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_management);

        initViews();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        orderAdapter = new OrderManagementAdapter(this, new ArrayList<>(), new OrderManagementAdapter.OnOrderActionListener() {
            @Override
            public void onDeliverClicked(Order order) {
                // TODO: Implement your deliver logic here
                Toast.makeText(OrderManagementActivity.this, "Đã gửi đơn hàng: " + order.getOrderId(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelClicked(Order order) {
                // TODO: Implement your cancel logic here
                Toast.makeText(OrderManagementActivity.this, "Đã huỷ đơn hàng: " + order.getOrderId(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onConfirmReceivedClicked(Order order) {
                // TODO: Implement your confirm received logic here
                Toast.makeText(OrderManagementActivity.this, "Đã xác nhận đơn hàng: " + order.getOrderId(), Toast.LENGTH_SHORT).show();
            }
        });
        recyclerView.setAdapter(orderAdapter);

        OrderController orderController = new OrderController();
        orderController.getAllOrders(new OrderController.OnOrdersLoadedListener() {
            @Override
            public void onSuccess(List<Order> orders) {
                allOrders = orders != null ? orders : new ArrayList<>();
                orderAdapter.setOrders(allOrders);
            }

            @Override
            public void onFailure(String message) {
                Toast.makeText(OrderManagementActivity.this, "Failed to load orders: " + message, Toast.LENGTH_SHORT).show();
            }
        });

        setupStatusFilter();
    }


    private void initViews() {
        recyclerView = findViewById(R.id.recyclerView_orders);
        btnAll = findViewById(R.id.btn_all);
        btnProcessing = findViewById(R.id.btn_processing);
        btnShipping = findViewById(R.id.btn_shipping);
        btnDelivered = findViewById(R.id.btn_delivered);
        btnCancelled = findViewById(R.id.btn_cancelled);
    }

    private void setupStatusFilter() {
        btnAll.setOnClickListener(v -> filterOrders("All"));
        btnProcessing.setOnClickListener(v -> filterOrders("Processing"));
        btnShipping.setOnClickListener(v -> filterOrders("Delivering"));
        btnDelivered.setOnClickListener(v -> filterOrders("Delivered"));
        btnCancelled.setOnClickListener(v -> filterOrders("Canceled"));
    }

    private void filterOrders(String status) {
        List<Order> filtered = new ArrayList<>();

        String selectedStatus = status.trim().toLowerCase();
        Log.d("FilterDebug", "Filtering orders by status: " + selectedStatus);

        for (Order order : allOrders) {
            String shipmentStatus = order.getShipmentStatus() != null ? order.getShipmentStatus().trim().toLowerCase() : "";
            Log.d("OrderStatusDebug", "Status: " + shipmentStatus);

            if (selectedStatus.equals("all") || shipmentStatus.equals(selectedStatus)) {
                filtered.add(order);
            }
        }

        Log.d("Adapter", "setOrders called with: " + filtered.size() + " items");
        orderAdapter.setOrders(filtered);
    }
}
