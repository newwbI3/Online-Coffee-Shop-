package com.example.onlinecoffeeshop.utils;

import android.view.View;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BadgeUtils {
    
    public static void showCartBadge(BottomNavigationView bottomNavigationView, int count) {
        if (bottomNavigationView == null) return;
        
        BadgeDrawable badge = bottomNavigationView.getOrCreateBadge(com.example.onlinecoffeeshop.R.id.nav_cart);
        if (count > 0) {
            badge.setVisible(true);
            badge.setNumber(count);
            badge.setBackgroundColor(bottomNavigationView.getContext().getColor(com.example.onlinecoffeeshop.R.color.error_color));
            badge.setBadgeTextColor(bottomNavigationView.getContext().getColor(com.example.onlinecoffeeshop.R.color.white));
        } else {
            badge.setVisible(false);
        }
    }
    
    public static void hideCartBadge(BottomNavigationView bottomNavigationView) {
        if (bottomNavigationView == null) return;
        
        BadgeDrawable badge = bottomNavigationView.getBadge(com.example.onlinecoffeeshop.R.id.nav_cart);
        if (badge != null) {
            badge.setVisible(false);
        }
    }
    
    public static void showNotificationBadge(BottomNavigationView bottomNavigationView, int count) {
        if (bottomNavigationView == null) return;
        
        BadgeDrawable badge = bottomNavigationView.getOrCreateBadge(com.example.onlinecoffeeshop.R.id.nav_orders);
        if (count > 0) {
            badge.setVisible(true);
            badge.setNumber(count);
            badge.setBackgroundColor(bottomNavigationView.getContext().getColor(com.example.onlinecoffeeshop.R.color.info_color));
            badge.setBadgeTextColor(bottomNavigationView.getContext().getColor(com.example.onlinecoffeeshop.R.color.white));
        } else {
            badge.setVisible(false);
        }
    }
}
