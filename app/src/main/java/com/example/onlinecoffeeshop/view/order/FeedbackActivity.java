package com.example.onlinecoffeeshop.view.order;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onlinecoffeeshop.R;
import com.example.onlinecoffeeshop.adapter.FeedbackItemAdapter;
import com.example.onlinecoffeeshop.controller.OrderController;
import com.example.onlinecoffeeshop.model.Order;

public class FeedbackActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FeedbackItemAdapter adapter;

    private String orderId;
    private OrderController orderController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        try {
            initViews();
            getIntentData();

            orderController = new OrderController();
            loadOrderItems();
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi khởi tạo: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recycler_view_feedback_items);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void getIntentData() {
        orderId = getIntent().getStringExtra("orderId");
        if (orderId == null) {
            Toast.makeText(this, "Không tìm thấy mã đơn hàng", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void loadOrderItems() {
        if (orderId != null && orderController != null) {
            orderController.getOrderById(orderId, new OrderController.OnOrderLoadedListener() {
                @Override
                public void onSuccess(Order order) {
                    try {
                        if (order != null && order.getItems() != null && !order.getItems().isEmpty()) {
                            adapter = new FeedbackItemAdapter(FeedbackActivity.this, order.getItems(), orderId);
                            recyclerView.setAdapter(adapter);
                        } else {
                            Toast.makeText(FeedbackActivity.this, "Không có sản phẩm trong đơn hàng", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    } catch (Exception e) {
                        Toast.makeText(FeedbackActivity.this, "Lỗi khi tải sản phẩm: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }

                @Override
                public void onFailure(String error) {
                    Toast.makeText(FeedbackActivity.this, "Lỗi: " + error, Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        }
    }
}
