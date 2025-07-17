package com.example.onlinecoffeeshop.view.admin;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onlinecoffeeshop.R;
import com.example.onlinecoffeeshop.adapter.UserManagementAdapter;
import com.example.onlinecoffeeshop.base.BaseActivity;
import com.example.onlinecoffeeshop.model.User;
import com.example.onlinecoffeeshop.view.auth.LoginActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class UserManagementActivity extends BaseActivity {
    private static final String TAG = "UserManagementActivity";
    
    private RecyclerView recyclerViewUsers;
    private UserManagementAdapter userAdapter;
    private ProgressBar progressBar;
    private EditText searchEditText;
    private Button btnSearch;
    private TextView tvTotalUsers, tvActiveUsers, tvAdminUsers;
    private LinearLayout emptyStateLayout;

    private List<User> userList;
    private List<User> filteredUserList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_management);
        
        // Check if user is manager
        if (!isUserManager()) {
            Toast.makeText(this, "Bạn không có quyền truy cập", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        initViews();
        setupBackButton();
        setupRecyclerView();
        setupSearchFunction();
        loadUsers();
    }
    
    private boolean isUserManager() {
        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return false;
        }
        
        // TODO: Check user role from Firestore
        // For now, assume user is manager if logged in
        return true;
    }
    
    private void initViews() {
        recyclerViewUsers = findViewById(R.id.recyclerViewUsers);
        progressBar = findViewById(R.id.progressBar);
        searchEditText = findViewById(R.id.searchEditText);
        btnSearch = findViewById(R.id.btnSearch);
        tvTotalUsers = findViewById(R.id.tvTotalUsers);
        tvActiveUsers = findViewById(R.id.tvActiveUsers);
        tvAdminUsers = findViewById(R.id.tvAdminUsers);
        emptyStateLayout = findViewById(R.id.emptyStateLayout);

        db = FirebaseFirestore.getInstance();
        userList = new ArrayList<>();
        filteredUserList = new ArrayList<>();
    }

    private void setupBackButton() {
        findViewById(R.id.back_btn).setOnClickListener(v -> finish());
    }

    private void setupSearchFunction() {
        btnSearch.setOnClickListener(v -> performSearch());

        searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            performSearch();
            return true;
        });
    }

    private void performSearch() {
        String query = searchEditText.getText().toString().trim().toLowerCase();

        filteredUserList.clear();

        if (TextUtils.isEmpty(query)) {
            filteredUserList.addAll(userList);
        } else {
            for (User user : userList) {
                if (user.getFullname() != null && user.getFullname().toLowerCase().contains(query) ||
                    user.getEmail() != null && user.getEmail().toLowerCase().contains(query) ||
                    user.getPhone() != null && user.getPhone().contains(query)) {
                    filteredUserList.add(user);
                }
            }
        }

        userAdapter.notifyDataSetChanged();
        updateEmptyState();
    }

    private void setupRecyclerView() {
        userAdapter = new UserManagementAdapter(this, filteredUserList, new UserManagementAdapter.OnUserActionListener() {
            @Override
            public void onEditUser(User user) {
                editUser(user);
            }
            
            @Override
            public void onBanUser(User user) {
                banUser(user, true);
            }

            @Override
            public void onUnbanUser(User user) {
                banUser(user, false);
            }
        });
        
        recyclerViewUsers.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewUsers.setAdapter(userAdapter);
    }
    
    private void loadUsers() {
        progressBar.setVisibility(View.VISIBLE);
        
        db.collection("users")
                .get()
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    
                    if (task.isSuccessful()) {
                        userList.clear();
                        
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            User user = document.toObject(User.class);
                            user.setUserId(document.getId());
                            userList.add(user);
                        }
                        
                        filteredUserList.clear();
                        filteredUserList.addAll(userList);
                        userAdapter.notifyDataSetChanged();
                        updateStatistics();
                        updateEmptyState();
                        Log.d(TAG, "Loaded " + userList.size() + " users");
                        
                    } else {
                        Log.e(TAG, "Error getting users: ", task.getException());
                        Toast.makeText(this, "Lỗi khi tải danh sách user", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    
    private void editUser(User user) {
        Intent intent = new Intent(this, UserDetailActivity.class);
        intent.putExtra("user_id", user.getUserId());
        intent.putExtra("user_name", user.getFullname());
        startActivity(intent);
    }

    private void banUser(User user, boolean isBan) {
        progressBar.setVisibility(View.VISIBLE);

        db.collection("users")
                .document(user.getUserId())
                .update("isBan", isBan)
                .addOnSuccessListener(aVoid -> {
                    progressBar.setVisibility(View.GONE);
                    String message = isBan ? "Đã ban user thành công" : "Đã unban user thành công";
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

                    // Update local data
                    user.setBan(isBan);
                    userAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Log.e(TAG, "Error updating user ban status: ", e);
                    String message = isBan ? "Lỗi khi ban user" : "Lỗi khi unban user";
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                });
    }

    private void updateStatistics() {
        int totalUsers = userList.size();
        int activeUsers = 0;
        int adminUsers = 0;

        for (User user : userList) {
            // Count active users (assuming all users are active for now)
            activeUsers++;

            // Count admin users
            if (user.getRole() != null &&
                (user.getRole().equals("admin") || user.getRole().equals("manager"))) {
                adminUsers++;
            }
        }

        tvTotalUsers.setText(String.valueOf(totalUsers));
        tvActiveUsers.setText(String.valueOf(activeUsers));
        tvAdminUsers.setText(String.valueOf(adminUsers));
    }

    private void updateEmptyState() {
        if (filteredUserList.isEmpty()) {
            recyclerViewUsers.setVisibility(View.GONE);
            emptyStateLayout.setVisibility(View.VISIBLE);
        } else {
            recyclerViewUsers.setVisibility(View.VISIBLE);
            emptyStateLayout.setVisibility(View.GONE);
        }
    }
}
