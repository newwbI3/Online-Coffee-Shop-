package com.example.onlinecoffeeshop.view.admin;

import android.content.Intent;
import android.os.Bundle;
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
import com.example.onlinecoffeeshop.adapter.UserFeedbackAdapter;
import com.example.onlinecoffeeshop.base.BaseActivity;
import com.example.onlinecoffeeshop.model.Feedback;
import com.example.onlinecoffeeshop.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserDetailActivity extends BaseActivity {
    private static final String TAG = "UserDetailActivity";
    
    private EditText etFullname, etEmail, etPhone, etAddress, etDob;
    private Button btnSaveChanges;
    private ProgressBar progressBar;
    private RecyclerView recyclerViewFeedbacks;
    private LinearLayout emptyStateFeedbacks;
    private TextView tvFeedbackCount;
    
    private String userId;
    private User currentUser;
    private FirebaseFirestore db;
    private DatabaseReference feedbackRef;
    
    private List<Feedback> feedbackList;
    private UserFeedbackAdapter feedbackAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);
        
        // Get user data from intent
        userId = getIntent().getStringExtra("user_id");
        String userName = getIntent().getStringExtra("user_name");
        
        if (userId == null) {
            Toast.makeText(this, "Lỗi: Không tìm thấy thông tin user", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        initViews();
        setupBackButton();
        setupRecyclerView();
        loadUserData();
        loadUserFeedbacks();
    }
    
    private void initViews() {
        etFullname = findViewById(R.id.etFullname);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etAddress = findViewById(R.id.etAddress);
        etDob = findViewById(R.id.etDob);
        btnSaveChanges = findViewById(R.id.btnSaveChanges);
        progressBar = findViewById(R.id.progressBar);
        recyclerViewFeedbacks = findViewById(R.id.recyclerViewFeedbacks);
        emptyStateFeedbacks = findViewById(R.id.emptyStateFeedbacks);
        tvFeedbackCount = findViewById(R.id.tvFeedbackCount);
        
        db = FirebaseFirestore.getInstance();
        feedbackRef = FirebaseDatabase.getInstance().getReference("feedbacks");
        feedbackList = new ArrayList<>();
        
        btnSaveChanges.setOnClickListener(v -> saveUserChanges());
    }
    
    private void setupBackButton() {
        findViewById(R.id.back_btn).setOnClickListener(v -> finish());
    }
    
    private void setupRecyclerView() {
        feedbackAdapter = new UserFeedbackAdapter(this, feedbackList);
        recyclerViewFeedbacks.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewFeedbacks.setAdapter(feedbackAdapter);
    }
    
    private void loadUserData() {
        progressBar.setVisibility(View.VISIBLE);
        
        db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    progressBar.setVisibility(View.GONE);
                    
                    if (documentSnapshot.exists()) {
                        currentUser = documentSnapshot.toObject(User.class);
                        if (currentUser != null) {
                            currentUser.setUserId(userId);
                            displayUserInfo();
                        }
                    } else {
                        Toast.makeText(this, "Không tìm thấy thông tin user", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Log.e(TAG, "Error loading user data: ", e);
                    Toast.makeText(this, "Lỗi khi tải thông tin user", Toast.LENGTH_SHORT).show();
                });
    }
    
    private void displayUserInfo() {
        if (currentUser != null) {
            etFullname.setText(currentUser.getFullname() != null ? currentUser.getFullname() : "");
            etEmail.setText(currentUser.getEmail() != null ? currentUser.getEmail() : "");
            etPhone.setText(currentUser.getPhone() != null ? currentUser.getPhone() : "");
            etAddress.setText(currentUser.getAddress() != null ? currentUser.getAddress() : "");
            etDob.setText(currentUser.getDob() != null ? currentUser.getDob() : "");
        }
    }
    
    private void loadUserFeedbacks() {
        feedbackRef.orderByChild("userId").equalTo(userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        feedbackList.clear();
                        
                        for (DataSnapshot feedbackSnapshot : snapshot.getChildren()) {
                            Feedback feedback = feedbackSnapshot.getValue(Feedback.class);
                            if (feedback != null) {
                                feedback.setFeedbackId(feedbackSnapshot.getKey());
                                feedbackList.add(feedback);
                            }
                        }
                        
                        feedbackAdapter.notifyDataSetChanged();
                        updateFeedbackUI();
                        
                        Log.d(TAG, "Loaded " + feedbackList.size() + " feedbacks for user: " + userId);
                    }
                    
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "Error loading feedbacks: ", error.toException());
                        Toast.makeText(UserDetailActivity.this, "Lỗi khi tải feedback", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    
    private void updateFeedbackUI() {
        tvFeedbackCount.setText("Feedback (" + feedbackList.size() + ")");
        
        if (feedbackList.isEmpty()) {
            recyclerViewFeedbacks.setVisibility(View.GONE);
            emptyStateFeedbacks.setVisibility(View.VISIBLE);
        } else {
            recyclerViewFeedbacks.setVisibility(View.VISIBLE);
            emptyStateFeedbacks.setVisibility(View.GONE);
        }
    }
    
    private void saveUserChanges() {
        String fullname = etFullname.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String dob = etDob.getText().toString().trim();
        
        if (fullname.isEmpty()) {
            etFullname.setError("Vui lòng nhập họ tên");
            return;
        }
        
        if (email.isEmpty()) {
            etEmail.setError("Vui lòng nhập email");
            return;
        }
        
        progressBar.setVisibility(View.VISIBLE);
        
        Map<String, Object> updates = new HashMap<>();
        updates.put("fullname", fullname);
        updates.put("email", email);
        updates.put("phone", phone);
        updates.put("address", address);
        updates.put("dob", dob);
        
        db.collection("users")
                .document(userId)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Đã cập nhật thông tin thành công", Toast.LENGTH_SHORT).show();
                    
                    // Update current user object
                    currentUser.setFullname(fullname);
                    currentUser.setEmail(email);
                    currentUser.setPhone(phone);
                    currentUser.setAddress(address);
                    currentUser.setDob(dob);
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Log.e(TAG, "Error updating user: ", e);
                    Toast.makeText(this, "Lỗi khi cập nhật thông tin", Toast.LENGTH_SHORT).show();
                });
    }
}
