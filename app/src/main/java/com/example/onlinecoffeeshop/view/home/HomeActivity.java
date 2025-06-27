package com.example.onlinecoffeeshop.view.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.onlinecoffeeshop.R;
import com.example.onlinecoffeeshop.controller.AuthController;
import com.example.onlinecoffeeshop.view.auth.LoginActivity;

import java.util.Map;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = "HomeActivity";

    private TextView tvUserName;
    private AuthController authController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Use existing layout for now

        initViews();
        initController();
        loadUserData();
    }

    private void initViews() {
        // Use simple TextView for now
        tvUserName = new TextView(this);
        tvUserName.setText("Loading user...");
        setContentView(tvUserName);
    }

    private void initController() {
        authController = new AuthController();
    }


    private void loadUserData() {
        if (!authController.isUserLoggedIn()) {
            Log.w(TAG, "User not logged in, redirecting to login");
            navigateToLogin();
            return;
        }

        // Simplified - just show welcome message
        tvUserName.setText("Welcome, User!");
    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
