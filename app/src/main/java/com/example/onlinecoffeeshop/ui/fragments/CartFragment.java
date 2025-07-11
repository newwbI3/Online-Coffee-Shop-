package com.example.onlinecoffeeshop.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onlinecoffeeshop.R;
import com.example.onlinecoffeeshop.adapter.CartAdapter;
import com.example.onlinecoffeeshop.model.CartItem;
import com.example.onlinecoffeeshop.view.cart.CartActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CartFragment extends Fragment implements CartAdapter.CartUpdateListener {
    private RecyclerView recyclerViewCart;
    private TextView totalPriceTxt, emptyCartTxt;
    private Button checkoutBtn;
    private LinearLayout cartLayout;
    private CartAdapter cartAdapter;
    private List<CartItem> cartItems;
    private DatabaseReference cartRef;
    private FirebaseAuth mAuth;
    private double totalPrice = 0.0;
    private ValueEventListener cartListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        try {
            initViews(view);
            setupFirebase();
            setupListeners();
            loadCartItems();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return view;
    }

    private void initViews(View view) {
        try {
            recyclerViewCart = view.findViewById(R.id.recyclerViewCart);
            totalPriceTxt = view.findViewById(R.id.totalPriceTxt);
            emptyCartTxt = view.findViewById(R.id.emptyCartTxt);
            checkoutBtn = view.findViewById(R.id.checkoutBtn);
            cartLayout = view.findViewById(R.id.cartLayout);

            cartItems = new ArrayList<>();
            if (recyclerViewCart != null && getContext() != null) {
                recyclerViewCart.setLayoutManager(new LinearLayoutManager(getContext()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupFirebase() {
        try {
            mAuth = FirebaseAuth.getInstance();
            if (mAuth.getCurrentUser() != null) {
                String userId = mAuth.getCurrentUser().getUid();
                cartRef = FirebaseDatabase.getInstance().getReference("Cart")
                        .child(userId); // Use actual user ID instead of "guest"
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupListeners() {
        try {
            if (checkoutBtn != null) {
                checkoutBtn.setOnClickListener(v -> {
                    try {
                        if (cartItems == null || cartItems.isEmpty()) {
                            Toast.makeText(getContext(), "Your cart is empty", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (getActivity() != null) {
                            Intent intent = new Intent(getActivity(), CartActivity.class);
                            intent.putExtra("total_price", totalPrice);
                            startActivity(intent);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Error opening checkout", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadCartItems() {
        try {
            if (cartRef == null) {
                showEmptyCart();
                return;
            }

            cartListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try {
                        if (cartItems != null) {
                            cartItems.clear();
                        } else {
                            cartItems = new ArrayList<>();
                        }
                        totalPrice = 0.0;

                        for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                            CartItem item = itemSnapshot.getValue(CartItem.class);
                            if (item != null) {
                                cartItems.add(item);
                                totalPrice += item.getPrice() * item.getQuantity();
                            }
                        }

                        if (isAdded() && getActivity() != null) {
                            updateUI();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (isAdded()) {
                            showEmptyCart();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    if (isAdded() && getContext() != null) {
                        Toast.makeText(getContext(), "Failed to load cart items", Toast.LENGTH_SHORT).show();
                        showEmptyCart();
                    }
                }
            };

            cartRef.addValueEventListener(cartListener);
        } catch (Exception e) {
            e.printStackTrace();
            showEmptyCart();
        }
    }

    private void updateUI() {
        try {
            if (cartItems == null || cartItems.isEmpty()) {
                showEmptyCart();
            } else {
                showCartItems();
            }
        } catch (Exception e) {
            e.printStackTrace();
            showEmptyCart();
        }
    }

    private void showEmptyCart() {
        try {
            if (emptyCartTxt != null) {
                emptyCartTxt.setVisibility(View.VISIBLE);
            }
            if (cartLayout != null) {
                cartLayout.setVisibility(View.GONE);
            }
            if (recyclerViewCart != null) {
                recyclerViewCart.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showCartItems() {
        try {
            if (emptyCartTxt != null) {
                emptyCartTxt.setVisibility(View.GONE);
            }
            if (cartLayout != null) {
                cartLayout.setVisibility(View.VISIBLE);
            }
            if (recyclerViewCart != null) {
                recyclerViewCart.setVisibility(View.VISIBLE);
            }

            if (cartAdapter == null && getContext() != null && cartItems != null) {
                cartAdapter = new CartAdapter(getContext(), cartItems, this);
                if (recyclerViewCart != null) {
                    recyclerViewCart.setAdapter(cartAdapter);
                }
            } else if (cartAdapter != null) {
                cartAdapter.notifyDataSetChanged();
            }

            if (totalPriceTxt != null) {
                totalPriceTxt.setText(String.format(Locale.getDefault(), "$%.2f", totalPrice));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCartUpdated() {
        try {
            // Reload cart items when the cart is updated
            if (isAdded()) {
                loadCartItems();
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
            if (cartRef != null && cartListener != null) {
                cartRef.removeEventListener(cartListener);
            }

            // Clean up references
            recyclerViewCart = null;
            totalPriceTxt = null;
            emptyCartTxt = null;
            checkoutBtn = null;
            cartLayout = null;
            cartAdapter = null;
            cartItems = null;
            cartRef = null;
            cartListener = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
