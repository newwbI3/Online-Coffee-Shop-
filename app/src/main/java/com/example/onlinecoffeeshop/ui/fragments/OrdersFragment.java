package com.example.onlinecoffeeshop.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onlinecoffeeshop.R;
import com.example.onlinecoffeeshop.adapter.OrderAdapter;
import com.example.onlinecoffeeshop.model.Order;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class OrdersFragment extends Fragment {
    private RecyclerView recyclerViewOrders;
    private TextView emptyOrdersTxt;
    private OrderAdapter orderAdapter;
    private List<Order> orders;
    private DatabaseReference ordersRef;
    private FirebaseAuth mAuth;
    private ValueEventListener ordersListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_orders, container, false);

        try {
            initViews(view);
            setupFirebase();
            loadOrders();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return view;
    }

    private void initViews(View view) {
        try {
            recyclerViewOrders = view.findViewById(R.id.recyclerViewOrders);
            emptyOrdersTxt = view.findViewById(R.id.emptyOrdersTxt);

            orders = new ArrayList<>();
            if (recyclerViewOrders != null && getContext() != null) {
                recyclerViewOrders.setLayoutManager(new LinearLayoutManager(getContext()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupFirebase() {
        try {
            mAuth = FirebaseAuth.getInstance();
            if (mAuth.getCurrentUser() != null) {
                ordersRef = FirebaseDatabase.getInstance().getReference("orders")
                        .child(mAuth.getCurrentUser().getUid());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadOrders() {
        try {
            if (ordersRef == null) {
                showEmptyOrders();
                return;
            }

            ordersListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try {
                        if (orders != null) {
                            orders.clear();
                        } else {
                            orders = new ArrayList<>();
                        }

                        for (DataSnapshot orderSnapshot : snapshot.getChildren()) {
                            Order order = orderSnapshot.getValue(Order.class);
                            if (order != null) {
                                orders.add(order);
                            }
                        }

                        // Sort orders by timestamp (most recent first)
                        if (orders != null && !orders.isEmpty()) {
                            orders.sort((o1, o2) ->
                                    Long.compare(o2.getTimestamp(), o1.getTimestamp()));
                        }

                        if (isAdded() && getActivity() != null) {
                            updateUI();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (isAdded()) {
                            showEmptyOrders();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    if (isAdded() && getContext() != null) {
                        Toast.makeText(getContext(), "Failed to load orders", Toast.LENGTH_SHORT).show();
                        showEmptyOrders();
                    }
                }
            };

            ordersRef.addValueEventListener(ordersListener);
        } catch (Exception e) {
            e.printStackTrace();
            showEmptyOrders();
        }
    }

    private void updateUI() {
        try {
            if (orders == null || orders.isEmpty()) {
                showEmptyOrders();
            } else {
                showOrders();
            }
        } catch (Exception e) {
            e.printStackTrace();
            showEmptyOrders();
        }
    }

    private void showEmptyOrders() {
        try {
            if (emptyOrdersTxt != null) {
                emptyOrdersTxt.setVisibility(View.VISIBLE);
            }
            if (recyclerViewOrders != null) {
                recyclerViewOrders.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showOrders() {
        try {
            if (emptyOrdersTxt != null) {
                emptyOrdersTxt.setVisibility(View.GONE);
            }
            if (recyclerViewOrders != null) {
                recyclerViewOrders.setVisibility(View.VISIBLE);
            }

            if (orderAdapter == null && getContext() != null && orders != null) {
                orderAdapter = new OrderAdapter(getContext(), orders);
                if (recyclerViewOrders != null) {
                    recyclerViewOrders.setAdapter(orderAdapter);
                }
            } else if (orderAdapter != null) {
                orderAdapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            // Refresh orders when fragment becomes visible
            if (ordersRef != null && isAdded()) {
                loadOrders();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            // Remove Firebase listener to prevent memory leaks
            if (ordersRef != null && ordersListener != null) {
                ordersRef.removeEventListener(ordersListener);
            }

            // Clean up references
            recyclerViewOrders = null;
            emptyOrdersTxt = null;
            orderAdapter = null;
            orders = null;
            ordersRef = null;
            ordersListener = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
