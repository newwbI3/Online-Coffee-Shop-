package com.example.onlinecoffeeshop.utils;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.example.onlinecoffeeshop.MainActivity;
import com.example.onlinecoffeeshop.view.auth.LoginActivity;
import com.example.onlinecoffeeshop.view.manager.ManagerActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class RoleAuthenticator {
    private static final String TAG = "RoleAuthenticator";
    
    public interface RoleCheckCallback {
        void onRoleChecked(String role);
        void onError(String error);
    }
    
    /**
     * Check user role and navigate to appropriate activity
     */
    public static void checkUserRoleAndNavigate(Activity currentActivity) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        
        if (auth.getCurrentUser() == null) {
            navigateToLogin(currentActivity);
            return;
        }
        
        String userId = auth.getCurrentUser().getUid();
        Log.d(TAG, "Checking role for user: " + userId);
        
        FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String role = documentSnapshot.getString("role");
                        Boolean isBan = documentSnapshot.getBoolean("isBan");

                        Log.d(TAG, "User role: " + role + ", isBan: " + isBan);

                        // Check if user is banned
                        if (isBan != null && isBan) {
                            Log.w(TAG, "User is banned, redirecting to login");
                            // Sign out banned user
                            FirebaseAuth.getInstance().signOut();
                            navigateToLogin(currentActivity);
                            // Show ban message
                            android.widget.Toast.makeText(currentActivity,
                                "Tài khoản của bạn đã bị khóa. Vui lòng liên hệ admin.",
                                android.widget.Toast.LENGTH_LONG).show();
                            return;
                        }

                        navigateBasedOnRole(currentActivity, role);
                    } else {
                        Log.e(TAG, "User document not found");
                        navigateToLogin(currentActivity);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error checking user role", e);
                    navigateToLogin(currentActivity);
                });
    }
    
    /**
     * Check user role with callback
     */
    public static void checkUserRole(RoleCheckCallback callback) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        
        if (auth.getCurrentUser() == null) {
            callback.onError("User not authenticated");
            return;
        }
        
        String userId = auth.getCurrentUser().getUid();
        
        FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String role = documentSnapshot.getString("role");
                        callback.onRoleChecked(role != null ? role : "user");
                    } else {
                        callback.onError("User document not found");
                    }
                })
                .addOnFailureListener(e -> {
                    callback.onError("Error checking role: " + e.getMessage());
                });
    }
    
    /**
     * Navigate to appropriate activity based on user role
     */
    private static void navigateBasedOnRole(Activity currentActivity, String role) {
        Intent intent;
        
        switch (role != null ? role.toLowerCase() : "user") {
            case "admin":
            case "marketer":
            case "inventory":
            case "manager":
                Log.d(TAG, "Navigating to ManagerActivity for role: " + role);
                intent = new Intent(currentActivity, ManagerActivity.class);
                break;
            case "user":
            default:
                Log.d(TAG, "Navigating to MainActivity for role: " + role);
                intent = new Intent(currentActivity, MainActivity.class);
                break;
        }
        
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        currentActivity.startActivity(intent);
        currentActivity.finish();
    }
    
    /**
     * Navigate to login activity
     */
    private static void navigateToLogin(Activity currentActivity) {
        Log.d(TAG, "Navigating to LoginActivity");
        Intent intent = new Intent(currentActivity, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        currentActivity.startActivity(intent);
        currentActivity.finish();
    }
    
    /**
     * Check if user has manager privileges
     */
    public static boolean isManagerRole(String role) {
        if (role == null) return false;
        
        String lowerRole = role.toLowerCase();
        return lowerRole.equals("admin") || 
               lowerRole.equals("marketer") || 
               lowerRole.equals("inventory") || 
               lowerRole.equals("manager");
    }
    
    /**
     * Check if user has admin privileges
     */
    public static boolean isAdminRole(String role) {
        return role != null && role.toLowerCase().equals("admin");
    }
    
    /**
     * Get role display name in Vietnamese
     */
    public static String getRoleDisplayName(String role) {
        switch (role != null ? role.toLowerCase() : "user") {
            case "admin":
                return "Quản trị viên";
            case "marketer":
                return "Nhân viên Marketing";
            case "inventory":
                return "Nhân viên Kho";
            case "manager":
                return "Quản lý";
            case "user":
            default:
                return "Khách hàng";
        }
    }
}
