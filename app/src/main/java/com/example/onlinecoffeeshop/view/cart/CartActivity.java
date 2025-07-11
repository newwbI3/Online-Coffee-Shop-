package com.example.onlinecoffeeshop.view.cart;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.onlinecoffeeshop.base.BaseActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onlinecoffeeshop.R;
import com.example.onlinecoffeeshop.adapter.CartAdapter;
import com.example.onlinecoffeeshop.model.CartItem;
import com.example.onlinecoffeeshop.view.auth.LoginActivity;
import com.example.onlinecoffeeshop.view.order.CheckoutActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CartActivity extends BaseActivity {
    private  String userId ;
    private final double taxRate = 0.2;
    private final double deliveryFee = 2.5;
    private Button checkOutBtn;
    private RecyclerView cartView;
    private TextView totalFeeTxt, deliveryTxt, taxTxt, totalTxt;
    private List<CartItem> cartItemList;
    private CartAdapter cartAdapter;
    private double totalAmount = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cart);

        initViews();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(this, "Vui lòng đăng nhập để xem giỏ hàng", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        userId = mAuth.getCurrentUser().getUid();

        checkOutBtn.setOnClickListener(v -> {
            try {
                String totalString = totalTxt.getText().toString().replace("$", "").trim();
                double total = Double.parseDouble(totalString);

                Intent intent = new Intent(CartActivity.this, CheckoutActivity.class);
                intent.putExtra("totalAmount", total);  // Gửi đúng kiểu double
                startActivity(intent);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Không thể chuyển đổi tổng tiền", Toast.LENGTH_SHORT).show();
            }
        });


        loadCartFromFirebase();

        findViewById(R.id.back_btn).setOnClickListener(v -> finish());
    }

    private void initViews() {
        cartView = findViewById(R.id.cartView);
        cartView.setLayoutManager(new LinearLayoutManager(this));
        totalFeeTxt = findViewById(R.id.totalFeeTxt);
        deliveryTxt = findViewById(R.id.deliveryTxt);
        taxTxt = findViewById(R.id.taxTxt);
        totalTxt = findViewById(R.id.totalTxt);
        checkOutBtn = findViewById(R.id.checkOut_btn);
    }

    private void loadCartFromFirebase() {
        cartItemList = new ArrayList<>();

        FirebaseDatabase.getInstance().getReference("Cart")
                .child(userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        cartItemList.clear();
                        for (DataSnapshot item : snapshot.getChildren()) {
                            CartItem cartItem = item.getValue(CartItem.class);
                            if (cartItem != null) {
                                cartItemList.add(cartItem);
                            }
                        }

                        cartAdapter = new CartAdapter(CartActivity.this, cartItemList,userId, new CartAdapter.CartUpdateListener() {
                            @Override
                            public void onCartUpdated() {
                                calculateTotal();
                            }
                        });
                        cartView.setAdapter(cartAdapter);
                        calculateTotal();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(CartActivity.this, "Lỗi khi tải giỏ hàng", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void calculateTotal() {
        double subtotal = 0;
        for (CartItem item : cartItemList) {
            subtotal += item.getPrice() * item.getQuantity();
        }

        double tax = subtotal * taxRate;
        totalAmount = subtotal + tax + deliveryFee;

        totalFeeTxt.setText(String.format("%.2f$", subtotal));
        taxTxt.setText(String.format("%.2f$", tax));
        deliveryTxt.setText(String.format("%.2f$", deliveryFee));
        totalTxt.setText(String.format("%.2f$", totalAmount));
    }
}
