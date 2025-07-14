package com.example.onlinecoffeeshop.view.order;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onlinecoffeeshop.MainActivity;
import com.example.onlinecoffeeshop.R;
import com.example.onlinecoffeeshop.base.BaseActivity;
import com.example.onlinecoffeeshop.controller.OrderController;
import com.example.onlinecoffeeshop.model.Order;
import com.example.onlinecoffeeshop.view.auth.LoginActivity;

import java.util.ArrayList;
import java.util.List;

public class OrderHistoryActivity extends BaseActivity {

    private RecyclerView orderRecyclerView;
    private LinearLayout emptyStateLayout;
    private Button btnStartShopping;
    // backBtnOrder removed from layout

    private List<Order> orderList;
    private com.example.onlinecoffeeshop.adapter.OrderAdapter orderAdapter;
    private OrderController orderController;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        initViews();
        setupListeners();
        fetchOrders();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Kiểm tra đăng nhập và refresh orders khi quay lại activity
        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else {
            fetchOrders();
        }
    }

    private void initViews() {
        orderRecyclerView = findViewById(R.id.orderRecyclerView);
        emptyStateLayout = findViewById(R.id.emptyStateLayout);
        btnStartShopping = findViewById(R.id.btn_start_shopping);
        // backBtnOrder removed from layout - no findViewById needed

        orderList = new ArrayList<>();
        orderAdapter = new com.example.onlinecoffeeshop.adapter.OrderAdapter(this, orderList);
        orderRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        orderRecyclerView.setAdapter(orderAdapter);

        orderController = new OrderController();
    }

    private void setupListeners() {
        // backBtnOrder removed from layout - no listener needed

        btnStartShopping.setOnClickListener(v -> {
            Intent intent = new Intent(OrderHistoryActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });
    }

    private void fetchOrders() {
        if (mAuth.getCurrentUser() == null) {
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();
        orderController.getOrdersByUserId(userId, new OrderController.OnOrdersLoadedListener() {
            @Override
            public void onSuccess(List<Order> orders) {
                orderList.clear();
                if (orders != null) {
                    orderList.addAll(orders);
                }

                if (orderList.isEmpty()) {
                    orderRecyclerView.setVisibility(View.GONE);
                    emptyStateLayout.setVisibility(View.VISIBLE);
                } else {
                    orderRecyclerView.setVisibility(View.VISIBLE);
                    emptyStateLayout.setVisibility(View.GONE);
                    orderAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(String message) {
                Toast.makeText(OrderHistoryActivity.this, "Lỗi khi tải đơn hàng: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Thêm method để refresh khi cần
    public void refreshOrders() {
        fetchOrders();
    }
}
