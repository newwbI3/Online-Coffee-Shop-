package com.example.onlinecoffeeshop.controller;

import androidx.annotation.NonNull;

import com.example.onlinecoffeeshop.model.Order;
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
    // Lưu đơn hàng mới
    public void createOrder(Order order, OnOrderCreatedListener listener) {
        if (order == null || order.getOrderId() == null) {
            listener.onFailure("Order hoặc orderId không hợp lệ");
            return;
        }
        orderRef.child(order.getOrderId()).setValue(order)
                .addOnSuccessListener(unused -> listener.onSuccess())
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }
    // Lấy đơn hàng theo ID
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

    public void cancelOrder(String orderId, OnOrderCancelledListener listener) {
        orderRef.child(orderId).removeValue()
                .addOnSuccessListener(unused -> listener.onSuccess())
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }
    // Giao diện callback
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
    public void updateOrderStatus(String orderId, String newStatus) {
        orderRef.child(orderId).child("shipmentStatus").setValue(newStatus);
    }
}
