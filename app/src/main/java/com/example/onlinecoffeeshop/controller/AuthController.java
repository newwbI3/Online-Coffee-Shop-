package com.example.onlinecoffeeshop.controller;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import com.example.onlinecoffeeshop.MainActivity;
import com.example.onlinecoffeeshop.model.User;
import com.example.onlinecoffeeshop.view.auth.LoginActivity;
import com.example.onlinecoffeeshop.utils.RoleAuthenticator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AuthController {
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;

    public AuthController() {
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
    }

    // Register function
    public void signUp(Activity activity, String email, String password, User user) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = auth.getCurrentUser();
                        if (firebaseUser != null) {
                            user.setUid(firebaseUser.getUid());
                            user.setEmail(email);

                            // Create optimized user data map - only essential fields
                            Map<String, Object> userData = new HashMap<>();
                            userData.put("uid", firebaseUser.getUid());
                            userData.put("fullname", user.getFullname());
                            userData.put("email", email);
                            userData.put("phone", user.getPhone());
                            userData.put("address", user.getAddress());
                            userData.put("dob", user.getDob());
                            userData.put("role", user.getRole() != null ? user.getRole() : "user");
                            userData.put("isBan", false); // Default not banned
                            // Only save avatar if it's not empty
                            if (user.getAvatar() != null && !user.getAvatar().trim().isEmpty()) {
                                userData.put("avatar", user.getAvatar());
                            }

                            // Save optimized user data to Firestore
                            firestore.collection("users").document(firebaseUser.getUid())
                                    .set(userData)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(activity, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                                        navigateToLogin(activity);
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(activity, "Lỗi lưu thông tin: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        }
                    } else {
                        Toast.makeText(activity, "Đăng ký thất bại: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Login function
    public void signIn(Activity activity, String email, String password) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(activity, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                        // Check user role and navigate accordingly
                        RoleAuthenticator.checkUserRoleAndNavigate(activity);
                    } else {
                        Toast.makeText(activity, "Đăng nhập thất bại: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Reset password function
    public void resetPassword(Activity activity, String email) {
        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(activity, "Link đặt lại mật khẩu đã được gửi đến email của bạn", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(activity, "Có lỗi xảy ra: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    public boolean isUserLoggedIn() {
        return auth.getCurrentUser() != null;
    }

    public void signOut(Activity activity) {
        auth.signOut();
        Toast.makeText(activity, "Đăng xuất thành công", Toast.LENGTH_SHORT).show();
        navigateToLogin(activity);
    }

    private void navigateToLogin(Activity activity) {
        Intent intent = new Intent(activity, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(intent);
        activity.finish();
    }

    private void navigateToMain(Activity activity) {
        Intent intent = new Intent(activity, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(intent);
        activity.finish();
    }
}
