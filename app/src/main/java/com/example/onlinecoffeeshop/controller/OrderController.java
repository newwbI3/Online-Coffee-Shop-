package com.example.onlinecoffeeshop.controller;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.onlinecoffeeshop.model.Order;
import com.example.onlinecoffeeshop.model.OrderStatusUpdate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class OrderController {
    private final DatabaseReference orderRef;

    public OrderController() {
        orderRef = FirebaseDatabase.getInstance().getReference("Orders");
    }

    // ✅ Save a new order
    public void createOrder(Order order, OnOrderCreatedListener listener) {
        if (order == null || order.getOrderId() == null) {
            listener.onFailure("Order hoặc orderId không hợp lệ");
            return;
        }

        if (order.getUserId() == null || order.getItems() == null || order.getItems().isEmpty()) {
            listener.onFailure("Thông tin đơn hàng không đầy đủ");
            return;
        }

        orderRef.child(order.getOrderId()).setValue(order)
                .addOnSuccessListener(unused -> listener.onSuccess())
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }

    // ✅ Get a specific order by ID
    public void getOrderById(String orderId, OnOrderLoadedListener listener) {
        orderRef.child(orderId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Order order = snapshot.getValue(Order.class);
                if (order != null) {
                    listener.onSuccess(order);
                } else {
                    listener.onFailure("Không tìm thấy đơn hàng");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                listener.onFailure(error.getMessage());
            }
        });
    }

    // ✅ Get all orders by user ID
    public void getOrdersByUserId(String userId, OnOrdersLoadedListener listener) {
        orderRef.orderByChild("userId").equalTo(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<Order> orderList = new ArrayList<>();
                        for (DataSnapshot orderSnapshot : snapshot.getChildren()) {
                            Order order = orderSnapshot.getValue(Order.class);
                            if (order != null) {
                                orderList.add(order);
                            }
                        }
                        orderList.sort((o1, o2) -> Long.compare(o2.getTimestamp(), o1.getTimestamp()));
                        listener.onSuccess(orderList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        listener.onFailure(error.getMessage());
                    }
                });
    }

    // ✅ Get all orders (new method)
    public void getAllOrders(OnOrdersLoadedListener listener) {
        orderRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Order> allOrders = new ArrayList<>();
                for (DataSnapshot orderSnapshot : snapshot.getChildren()) {
                    Order order = orderSnapshot.getValue(Order.class);
                    if (order != null) {
                        allOrders.add(order);
                    }
                }
                allOrders.sort((o1, o2) -> Long.compare(o2.getTimestamp(), o1.getTimestamp()));
                listener.onSuccess(allOrders);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                listener.onFailure(error.getMessage());
            }
        });
    }

    // ✅ Cancel order
    public void cancelOrder(String orderId, OnOrderCancelledListener listener) {
        orderRef.child(orderId).removeValue()
                .addOnSuccessListener(unused -> listener.onSuccess())
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }

    // ✅ Update shipment status and history
    public void updateOrderStatus(String orderId, String newStatus) {
        orderRef.child(orderId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Order order = snapshot.getValue(Order.class);
                    if (order == null) return;

                    order.setShipmentStatus(newStatus);
                    List<OrderStatusUpdate> history = order.getStatusHistory();
                    if (history == null) history = new ArrayList<>();

                    long timestamp = getCurrentTimestamp();
                    history.add(new OrderStatusUpdate(newStatus, timestamp));
                    order.setStatusHistory(history);

                    orderRef.child(orderId).setValue(order)
                            .addOnSuccessListener(aVoid -> Log.d("OrderController", "Status + history updated"))
                            .addOnFailureListener(e -> Log.e("OrderController", "Failed to update", e));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("OrderController", "Database error: " + error.getMessage());
            }
        });
    }

    public static long getCurrentTimestamp() {
        return System.currentTimeMillis();
    }

    // ✅ Interfaces
    public interface OnOrderCreatedListener {
        void onSuccess();
        void onFailure(String message);
    }

    public interface OnOrderLoadedListener {
        void onSuccess(Order order);
        void onFailure(String message);
    }

    public interface OnOrdersLoadedListener {
        void onSuccess(List<Order> orders);
        void onFailure(String message);
    }

    public interface OnOrderCancelledListener {
        void onSuccess();
        void onFailure(String message);
    }
}
