package com.example.onlinecoffeeshop.base;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.onlinecoffeeshop.MainActivity;
import com.example.onlinecoffeeshop.R;
import com.example.onlinecoffeeshop.view.auth.LoginActivity;
import com.example.onlinecoffeeshop.view.cart.CartActivity;
import com.example.onlinecoffeeshop.view.favorite.FavoriteActivity;


import com.example.onlinecoffeeshop.view.order.OrderHistoryActivity;
import com.example.onlinecoffeeshop.view.profile.ProfileActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.example.onlinecoffeeshop.utils.BadgeUtils;

public abstract class BaseActivity extends AppCompatActivity {
    protected BottomNavigationView bottomNavigationView;
    protected FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        if (bottomNavigationView != null) {
            bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    return handleBottomNavigation(item);
                }
            });
            
            // Set selected item based on current activity
            setSelectedBottomNavItem();
        }
    }

    private boolean handleBottomNavigation(MenuItem item) {
        int itemId = item.getItemId();
        Intent intent = null;
        
        if (itemId == R.id.nav_home) {
            if (!(this instanceof MainActivity)) {
                intent = new Intent(this, MainActivity.class);
            }
        } else if (itemId == R.id.nav_favorite) {
            if (!(this instanceof FavoriteActivity)) {
                intent = new Intent(this, FavoriteActivity.class);
            }
        } else if (itemId == R.id.nav_cart) {
            if (!(this instanceof CartActivity)) {
                intent = new Intent(this, CartActivity.class);
            }
        } else if (itemId == R.id.nav_orders) {
            if (!(this instanceof OrderHistoryActivity)) {
                // Check if user is logged in for orders
                if (mAuth.getCurrentUser() == null) {
                    startActivity(new Intent(this, LoginActivity.class));
                    return true;
                }
                intent = new Intent(this, OrderHistoryActivity.class);
            }
        } else if (itemId == R.id.nav_profile) {
            if (!(this instanceof ProfileActivity)) {
                // Check if user is logged in for profile
                if (mAuth.getCurrentUser() == null) {
                    startActivity(new Intent(this, LoginActivity.class));
                    return true;
                }
                intent = new Intent(this, ProfileActivity.class);
            }
        }
        
        if (intent != null) {
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            // Add smooth fade transition animation
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            return true;
        }
        
        return true;
    }

    private void setSelectedBottomNavItem() {
        if (bottomNavigationView == null) return;
        
        if (this instanceof MainActivity) {
            bottomNavigationView.setSelectedItemId(R.id.nav_home);
        } else if (this instanceof FavoriteActivity) {
            bottomNavigationView.setSelectedItemId(R.id.nav_favorite);
        } else if (this instanceof CartActivity) {
            bottomNavigationView.setSelectedItemId(R.id.nav_cart);
        } else if (this instanceof OrderHistoryActivity) {
            bottomNavigationView.setSelectedItemId(R.id.nav_orders);
        } else if (this instanceof ProfileActivity) {
            bottomNavigationView.setSelectedItemId(R.id.nav_profile);
        }
    }

    // Method to hide bottom navigation if needed
    protected void hideBottomNavigation() {
        if (bottomNavigationView != null) {
            bottomNavigationView.setVisibility(View.GONE);
        }
    }

    // Method to show bottom navigation
    protected void showBottomNavigation() {
        if (bottomNavigationView != null) {
            bottomNavigationView.setVisibility(View.VISIBLE);
        }
    }

    // Method to update cart badge
    protected void updateCartBadge(int count) {
        BadgeUtils.showCartBadge(bottomNavigationView, count);
    }

    // Method to update notification badge
    protected void updateNotificationBadge(int count) {
        BadgeUtils.showNotificationBadge(bottomNavigationView, count);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Update selected item when activity resumes
        setSelectedBottomNavItem();

        // Update badges if needed
        // updateCartBadge(getCartItemCount()); // Implement this method in child activities
        // updateNotificationBadge(getNotificationCount()); // Implement this method in child activities
    }
}
