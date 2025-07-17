package com.example.onlinecoffeeshop.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onlinecoffeeshop.R;
import com.example.onlinecoffeeshop.model.User;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class UserManagementAdapter extends RecyclerView.Adapter<UserManagementAdapter.UserViewHolder> {
    
    private Context context;
    private List<User> userList;
    private OnUserActionListener listener;
    private SimpleDateFormat dateFormat;
    
    public interface OnUserActionListener {
        void onEditUser(User user);
        void onBanUser(User user);
        void onUnbanUser(User user);
    }
    
    public UserManagementAdapter(Context context, List<User> userList, OnUserActionListener listener) {
        this.context = context;
        this.userList = userList;
        this.listener = listener;
        this.dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    }
    
    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user_management, parent, false);
        return new UserViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        
        // Set user info
        String displayName = user.getFullname() != null ? user.getFullname() : "N/A";
        if (user.isBan()) {
            displayName += " (BANNED)";
        }
        holder.tvFullname.setText(displayName);
        holder.tvEmail.setText(user.getEmail() != null ? user.getEmail() : "N/A");
        holder.tvRole.setText(getRoleDisplayName(user.getRole()));
        holder.tvPhone.setText(user.getPhone() != null ? user.getPhone() : "N/A");
        
        // Set role color
        setRoleColor(holder.tvRole, user.getRole(), user.isBan());
        
        // Set date of birth
        if (user.getDob() != null && !user.getDob().isEmpty()) {
            holder.tvDob.setText(user.getDob());
        } else {
            holder.tvDob.setText("N/A");
        }
        
        // Set address
        if (user.getAddress() != null && !user.getAddress().isEmpty()) {
            holder.tvAddress.setText(user.getAddress());
        } else {
            holder.tvAddress.setText("N/A");
        }
        
        // Menu button click
        holder.btnMenu.setOnClickListener(v -> showPopupMenu(v, user));
    }
    
    private String getRoleDisplayName(String role) {
        if (role == null) return "User";
        
        switch (role.toLowerCase()) {
            case "admin":
                return "Admin";
            case "manager":
                return "Manager";
            case "marketer":
                return "Marketer";
            case "inventory":
                return "Inventory";
            case "user":
            default:
                return "User";
        }
    }
    
    private void setRoleColor(TextView textView, String role, boolean isBanned) {
        if (isBanned) {
            textView.setTextColor(context.getResources().getColor(android.R.color.black));
            textView.setBackgroundColor(context.getResources().getColor(android.R.color.holo_red_light));
            return;
        }

        if (role == null) role = "user";

        switch (role.toLowerCase()) {
            case "admin":
                textView.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
                break;
            case "manager":
                textView.setTextColor(context.getResources().getColor(android.R.color.holo_orange_dark));
                break;
            case "marketer":
                textView.setTextColor(context.getResources().getColor(android.R.color.holo_blue_dark));
                break;
            case "inventory":
                textView.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
                break;
            case "user":
            default:
                textView.setTextColor(context.getResources().getColor(R.color.grey));
                break;
        }
    }
    
    private void showPopupMenu(View view, User user) {
        PopupMenu popup = new PopupMenu(context, view);
        popup.getMenuInflater().inflate(R.menu.user_management_menu, popup.getMenu());

        // Show/hide ban/unban based on current status
        popup.getMenu().findItem(R.id.menu_ban_user).setVisible(!user.isBan());
        popup.getMenu().findItem(R.id.menu_unban_user).setVisible(user.isBan());

        popup.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            
            if (itemId == R.id.menu_edit) {
                listener.onEditUser(user);
                return true;
            } else if (itemId == R.id.menu_ban_user) {
                listener.onBanUser(user);
                return true;
            } else if (itemId == R.id.menu_unban_user) {
                listener.onUnbanUser(user);
                return true;
            }
            
            return false;
        });
        
        popup.show();
    }
    
    @Override
    public int getItemCount() {
        return userList.size();
    }
    
    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tvFullname, tvEmail, tvRole, tvPhone, tvDob, tvAddress;
        ImageView btnMenu;
        
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            
            tvFullname = itemView.findViewById(R.id.tvFullname);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvRole = itemView.findViewById(R.id.tvRole);
            tvPhone = itemView.findViewById(R.id.tvPhone);
            tvDob = itemView.findViewById(R.id.tvDob);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            btnMenu = itemView.findViewById(R.id.btnMenu);
        }
    }
}
