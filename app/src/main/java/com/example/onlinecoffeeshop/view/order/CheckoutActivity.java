package com.example.onlinecoffeeshop.view.order;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.onlinecoffeeshop.R;
import com.example.onlinecoffeeshop.controller.OrderController;
import com.example.onlinecoffeeshop.model.CartItem;
import com.example.onlinecoffeeshop.model.Order;
import com.example.onlinecoffeeshop.model.User;
import com.example.onlinecoffeeshop.view.auth.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CheckoutActivity extends AppCompatActivity {
    private EditText fullNameEdt, phoneEdt, emailEdt, addressEdt, noteEdt;
    private RadioGroup deliveryMethodGroup, paymentMethodGroup;
    private TextView totalAmountTxt;
    private Button btnPlaceOrder;

    private List<CartItem> cartItems;
    private double totalAmount = 0;

    private FirebaseAuth mAuth;
    private String userId;
    private DatabaseReference cartRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(this, "Vui lòng đăng nhập để tiếp tục", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        userId = mAuth.getCurrentUser().getUid();
        cartRef = FirebaseDatabase.getInstance().getReference("Cart").child(userId);

        initViews();
        loadCartItems();

        btnPlaceOrder.setOnClickListener(v -> {
            if (validateInputs()) {
                placeOrder();
            }
        });
    }
    private void initViews() {
        fullNameEdt = findViewById(R.id.fullName);
        phoneEdt = findViewById(R.id.phone);
        emailEdt = findViewById(R.id.email);
        addressEdt = findViewById(R.id.address);
        noteEdt = findViewById(R.id.note);
        deliveryMethodGroup = findViewById(R.id.deliveryMethod);
        paymentMethodGroup = findViewById(R.id.paymentMethod);
        totalAmountTxt = findViewById(R.id.totalAmountTxt);
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder);
    }
    private void loadCartItems() {
        cartItems = (List<CartItem>) getIntent().getSerializableExtra("cartItems");
        cartRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                totalAmount = 0;
                for (DataSnapshot itemSnap : snapshot.getChildren()) {
                    CartItem item = itemSnap.getValue(CartItem.class);
                    if (item != null) {
                        cartItems.add(item);
                        totalAmount += item.getPrice() * item.getQuantity();
                    }
                }
                totalAmountTxt.setText("Tổng: $" + String.format("%.2f", totalAmount));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CheckoutActivity.this, "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private boolean validateInputs() {
        if (TextUtils.isEmpty(fullNameEdt.getText()) ||
                TextUtils.isEmpty(phoneEdt.getText()) ||
                TextUtils.isEmpty(addressEdt.getText())) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (deliveryMethodGroup.getCheckedRadioButtonId() == -1 ||
                paymentMethodGroup.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Vui lòng chọn phương thức giao hàng và thanh toán", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
    private void placeOrder() {
        String fullName = fullNameEdt.getText().toString().trim();
        String phone = phoneEdt.getText().toString().trim();
        String email = emailEdt.getText().toString().trim();
        String address = addressEdt.getText().toString().trim();
        String note = noteEdt.getText().toString().trim();

        String deliveryMethod = ((RadioButton) findViewById(deliveryMethodGroup.getCheckedRadioButtonId())).getText().toString();
        String paymentMethod = ((RadioButton) findViewById(paymentMethodGroup.getCheckedRadioButtonId())).getText().toString();

        String orderId = UUID.randomUUID().toString();
        long timestamp = System.currentTimeMillis();

        User user = new User(userId, fullName, phone, email, address);
        Order order = new Order(orderId, user, note, deliveryMethod, paymentMethod, cartItems, totalAmount, timestamp);

        OrderController orderController = new OrderController();
        orderController.createOrder(order, new OrderController.OnOrderCreatedListener() {
            @Override
            public void onSuccess() {
                cartRef.removeValue();
                Toast.makeText(CheckoutActivity.this, "Đặt hàng thành công!", Toast.LENGTH_LONG).show();
                finish();
            }

            @Override
            public void onFailure(String message) {
                Toast.makeText(CheckoutActivity.this, "Lỗi: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
