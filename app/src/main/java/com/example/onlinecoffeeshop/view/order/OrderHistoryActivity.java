package com.example.onlinecoffeeshop.view.order;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.onlinecoffeeshop.R;
import com.example.onlinecoffeeshop.base.BaseActivity;
import com.example.onlinecoffeeshop.view.auth.LoginActivity;

public class OrderHistoryActivity extends BaseActivity {
    private static final String TAG = "OrderHistoryActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        checkUserLogin();
    }

    private void checkUserLogin() {
        if (mAuth.getCurrentUser() == null) {
            // Redirect to login
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        // Show placeholder message
        Toast.makeText(this, "Chức năng lịch sử đơn hàng đang được phát triển", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkUserLogin();
    }
}
