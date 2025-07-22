package com.example.onlinecoffeeshop.view.manager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.example.onlinecoffeeshop.MainActivity;

import com.example.onlinecoffeeshop.R;
import com.example.onlinecoffeeshop.base.BaseActivity;
import com.example.onlinecoffeeshop.controller.AuthController;
import com.example.onlinecoffeeshop.model.User;
import com.example.onlinecoffeeshop.view.admin.AdminProductActivity;
import com.example.onlinecoffeeshop.view.admin.ChartActivity;
import com.example.onlinecoffeeshop.view.admin.OrderManagementActivity;
import com.example.onlinecoffeeshop.view.auth.LoginActivity;
import com.example.onlinecoffeeshop.view.product.ListProductActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ManagerActivity extends BaseActivity {
    private static final String TAG = "ManagerActivity";
    
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private AuthController authController;
    
    // UI Components
    private TextView tvWelcome, tvUserInfo, tvStats;
    private CardView cardProductManagement, cardOrderManagement, cardUserManagement, cardReports;
    private Button btn_logout;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager);
        initFirebase();
        setupToolbar();
        initViews();
        setupClickListeners();
        loadManagerData();
    }
    
    private void initFirebase() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        authController = new AuthController();
    }
    
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Quản lý hệ thống");
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
    }
    
    private void initViews() {
        tvWelcome = findViewById(R.id.tv_welcome);
        tvUserInfo = findViewById(R.id.tv_user_info);
        
        cardProductManagement = findViewById(R.id.card_product_management);
        cardOrderManagement = findViewById(R.id.card_order_management);
        cardUserManagement = findViewById(R.id.card_user_management);
        cardReports = findViewById(R.id.card_reports);
        btn_logout = findViewById(R.id.logout_btn);
    }
    
    private void setupClickListeners() {
        cardProductManagement.setOnClickListener(v -> {
            Intent intent = new Intent(ManagerActivity.this, AdminProductActivity.class);
            startActivity(intent);
        });

        cardOrderManagement.setOnClickListener(v -> {
            Intent intent = new Intent(ManagerActivity.this, OrderManagementActivity.class);
            startActivity(intent);
        });
        
        cardUserManagement.setOnClickListener(v -> {
            Intent intent = new Intent(ManagerActivity.this, com.example.onlinecoffeeshop.view.admin.UserManagementActivity.class);
            startActivity(intent);
        });
        
        cardReports.setOnClickListener(v -> {
            Toast.makeText(this, "Báo cáo thống kê", Toast.LENGTH_SHORT).show();
            // TODO: Navigate to ReportsActivity
            Intent intent = new Intent(ManagerActivity.this, ChartActivity.class);
            startActivity(intent);
        });
        btn_logout.setOnClickListener(v -> showLogoutDialog());
    }
    
    private void loadManagerData() {
        if (mAuth.getCurrentUser() == null) {
            redirectToLogin();
            return;
        }
        String userId = mAuth.getCurrentUser().getUid();
        Log.d(TAG, "Loading manager data for user: " + userId);
        
        db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        currentUser = documentSnapshot.toObject(User.class);
                        if (currentUser != null) {
                            displayManagerInfo();
                        }
                    } else {
                        Log.e(TAG, "Manager document not found");
                        redirectToLogin();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error loading manager data", e);
                    Toast.makeText(this, "Lỗi khi tải thông tin quản lý", Toast.LENGTH_SHORT).show();
                });
    }
    
    private void displayManagerInfo() {
        if (currentUser != null) {
            String welcomeText = "Chào mừng, " + (currentUser.getFullname() != null ? currentUser.getFullname() : "Quản lý");
            tvWelcome.setText(welcomeText);
            
            String userInfo = "Email: " + currentUser.getEmail() + "\n" +
                            "Vai trò: " + getRoleDisplayName(currentUser.getRole());
            tvUserInfo.setText(userInfo);
        }
    }
    
    private String getRoleDisplayName(String role) {
        switch (role != null ? role.toLowerCase() : "user") {
            case "admin":
                return "Quản trị viên";
            case "marketer":
                return "Nhân viên Marketing";
            case "inventory":
                return "Nhân viên Kho";
            case "manager":
                return "Quản lý";
            default:
                return "Người dùng";
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_manager, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        
        if (id == R.id.action_profile) {
            // TODO: Open manager profile
            Toast.makeText(this, "Hồ sơ cá nhân", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_settings) {
            Toast.makeText(this, "Cài đặt", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_logout) {
            showLogoutDialog();
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }
    
    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Đăng xuất")
                .setMessage("Bạn có chắc chắn muốn đăng xuất?")
                .setPositiveButton("Đăng xuất", (dialog, which) -> {
                    authController.signOut(this);
                })
                .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss())
                .show();
    }
    
    private void redirectToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }


    @Override
    protected void onResume() {
        super.onResume();
        // Verify user is still authenticated and has manager role
        if (mAuth.getCurrentUser() != null) {
            loadManagerData();
        } else {
            redirectToLogin();
        }
    }
}
