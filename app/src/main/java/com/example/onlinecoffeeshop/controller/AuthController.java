package com.example.onlinecoffeeshop.controller;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import com.example.onlinecoffeeshop.MainActivity;
import com.example.onlinecoffeeshop.model.User;
import com.example.onlinecoffeeshop.view.auth.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

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

                            // Save user to Firestore
                            firestore.collection("users").document(firebaseUser.getUid())
                                    .set(user)
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
                        navigateToMain(activity);
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
