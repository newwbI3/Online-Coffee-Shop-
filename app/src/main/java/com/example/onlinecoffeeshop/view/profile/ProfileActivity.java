package com.example.onlinecoffeeshop.view.profile;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.onlinecoffeeshop.base.BaseActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.onlinecoffeeshop.R;
import com.example.onlinecoffeeshop.controller.AuthController;
import com.example.onlinecoffeeshop.model.User;
import com.example.onlinecoffeeshop.view.auth.LoginActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.Locale;

public class ProfileActivity extends BaseActivity {
    private static final String TAG = "ProfileActivity";
    
    private ImageView ivAvatar;
    private EditText etFullname, etDob, etAddress, etPhone;
    private TextView tvEmail, tvRole;
    private Button btnUpdateProfile, btnChangePassword, btnLogout;
    private ProgressBar progressBar;
    
    private AuthController authController;
    private User currentUser;
    private Calendar calendar;
    private SimpleDateFormat dateFormat;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        
        setupToolbar();
        initViews();
        initController();
        setupClickListeners();
        loadUserProfile();
        
        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    }
    
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Thông tin cá nhân");
        }
    }
    
    private void initViews() {
        ivAvatar = findViewById(R.id.iv_avatar);
        etFullname = findViewById(R.id.et_fullname);
        etDob = findViewById(R.id.et_dob);
        etAddress = findViewById(R.id.et_address);
        etPhone = findViewById(R.id.et_phone);
        tvEmail = findViewById(R.id.tv_email);
        tvRole = findViewById(R.id.tv_role);
        btnUpdateProfile = findViewById(R.id.btn_update_profile);
        btnChangePassword = findViewById(R.id.btn_change_password);
        btnLogout = findViewById(R.id.btn_logout);
        progressBar = findViewById(R.id.progress_bar);
    }
    
    private void initController() {
        authController = new AuthController();
    }
    
    private void setupClickListeners() {
//        btnUpdateProfile.setOnClickListener(v -> updateProfile());
//        btnChangePassword.setOnClickListener(v -> showChangePasswordDialog());

        etDob.setOnClickListener(v -> showDatePicker());
        etDob.setFocusable(false);
        etDob.setClickable(true);

        ivAvatar.setOnClickListener(v -> {
            // TODO: Implement avatar selection
            Toast.makeText(this, "Chức năng chọn avatar sẽ được cập nhật sau", Toast.LENGTH_SHORT).show();
        });

        // Logout button click listener
        btnLogout.setOnClickListener(v -> showLogoutDialog());
    }
    
    private void loadUserProfile() {
        showLoading(true);

        // Simplified - create empty user for now
        currentUser = new User();
        currentUser.setFullname("User");
        currentUser.setEmail("user@example.com");
        currentUser.setDob("");
        currentUser.setAddress("");
        currentUser.setPhone("");
        currentUser.setRole("user");

        showLoading(false);
        displayUserInfo();
    }
    
    private void displayUserInfo() {
        if (currentUser != null) {
            etFullname.setText(currentUser.getFullname());
            etDob.setText(currentUser.getDob());
            etAddress.setText(currentUser.getAddress());
            etPhone.setText(currentUser.getPhone());
            tvEmail.setText(currentUser.getEmail());
            
            // Display role in Vietnamese
            String roleText = getRoleDisplayName(currentUser.getRole());
            tvRole.setText(roleText);
            
            // TODO: Load avatar image
            // Glide.with(this).load(currentUser.getAvatar()).into(ivAvatar);
        }
    }
    
    private String getRoleDisplayName(String role) {
        switch (role) {
            case "admin":
                return "Quản trị viên";
            case "marketer":
                return "Nhân viên Marketing";
            case "inventory":
                return "Nhân viên Kho";
            case "user":
            default:
                return "Khách hàng";
        }
    }
    
    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    etDob.setText(dateFormat.format(calendar.getTime()));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }
    

    
    private boolean validateInput(String fullname, String dob, String address, String phone) {
        if (TextUtils.isEmpty(fullname)) {
            etFullname.setError("Họ tên không được để trống");
            etFullname.requestFocus();
            return false;
        }
        
        if (fullname.length() < 2) {
            etFullname.setError("Họ tên phải có ít nhất 2 ký tự");
            etFullname.requestFocus();
            return false;
        }
        
        if (TextUtils.isEmpty(dob)) {
            etDob.setError("Ngày sinh không được để trống");
            etDob.requestFocus();
            return false;
        }
        
        if (TextUtils.isEmpty(address)) {
            etAddress.setError("Địa chỉ không được để trống");
            etAddress.requestFocus();
            return false;
        }
        
        if (TextUtils.isEmpty(phone)) {
            etPhone.setError("Số điện thoại không được để trống");
            etPhone.requestFocus();
            return false;
        }
        
        if (!phone.matches("^[0-9]{10,11}$")) {
            etPhone.setError("Số điện thoại không hợp lệ (10-11 số)");
            etPhone.requestFocus();
            return false;
        }
        
        return true;
    }
    
//    private void showChangePasswordDialog() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        View dialogView = getLayoutInflater().inflate(R.layout.dialog_change_password, null);
//        builder.setView(dialogView);
//
//        EditText etCurrentPassword = dialogView.findViewById(R.id.et_current_password);
//        EditText etNewPassword = dialogView.findViewById(R.id.et_new_password);
//        EditText etConfirmPassword = dialogView.findViewById(R.id.et_confirm_password);
//
//        builder.setTitle("Đổi mật khẩu");
//        builder.setPositiveButton("Đổi mật khẩu", null);
//        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
//
//        AlertDialog dialog = builder.create();
//        dialog.setOnShowListener(dialogInterface -> {
//            Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
//            button.setOnClickListener(view -> {
//                String currentPassword = etCurrentPassword.getText().toString().trim();
//                String newPassword = etNewPassword.getText().toString().trim();
//                String confirmPassword = etConfirmPassword.getText().toString().trim();
//
//                if (validatePasswordInput(currentPassword, newPassword, confirmPassword,
//                                        etCurrentPassword, etNewPassword, etConfirmPassword)) {
//                    changePassword(newPassword);
//                    dialog.dismiss();
//                }
//            });
//        });
//
//        dialog.show();
//    }
    
    private boolean validatePasswordInput(String currentPassword, String newPassword, 
                                        String confirmPassword, EditText etCurrentPassword, 
                                        EditText etNewPassword, EditText etConfirmPassword) {
        if (TextUtils.isEmpty(currentPassword)) {
            etCurrentPassword.setError("Mật khẩu hiện tại không được để trống");
            etCurrentPassword.requestFocus();
            return false;
        }
        
        if (TextUtils.isEmpty(newPassword)) {
            etNewPassword.setError("Mật khẩu mới không được để trống");
            etNewPassword.requestFocus();
            return false;
        }
        
        if (newPassword.length() < 6) {
            etNewPassword.setError("Mật khẩu mới phải có ít nhất 6 ký tự");
            etNewPassword.requestFocus();
            return false;
        }
        
        if (TextUtils.isEmpty(confirmPassword)) {
            etConfirmPassword.setError("Xác nhận mật khẩu không được để trống");
            etConfirmPassword.requestFocus();
            return false;
        }
        
        if (!newPassword.equals(confirmPassword)) {
            etConfirmPassword.setError("Mật khẩu xác nhận không khớp");
            etConfirmPassword.requestFocus();
            return false;
        }
        
        return true;
    }
    
//    private void changePassword(String newPassword) {
//        showLoading(true);
//
//        authController.changePassword(newPassword);
//    }
    private void showLoading(boolean show) {
        if (show) {
            progressBar.setVisibility(View.VISIBLE);
            btnUpdateProfile.setEnabled(false);
            btnChangePassword.setEnabled(false);
        } else {
            progressBar.setVisibility(View.GONE);
            btnUpdateProfile.setEnabled(true);
            btnChangePassword.setEnabled(true);
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        
        if (id == android.R.id.home) {
            finish();
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
                .setIcon(R.drawable.ic_logout)
                .setPositiveButton("Đăng xuất", (dialog, which) -> {
                    // Show loading while logging out
                    showLoading(true);

                    // Use AuthController's signOut method (it handles navigation)
                    authController.signOut(this);
                })
                .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss())
                .setCancelable(false)
                .show();
    }
}
