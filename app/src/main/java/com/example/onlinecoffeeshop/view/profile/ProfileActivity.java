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
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.onlinecoffeeshop.base.BaseActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.onlinecoffeeshop.R;
import com.example.onlinecoffeeshop.controller.AuthController;
import com.example.onlinecoffeeshop.model.User;
import com.example.onlinecoffeeshop.view.auth.LoginActivity;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
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
    private FirebaseFirestore db;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        
        setupToolbar();
        initViews();
        initController();
        setupClickListeners();

        // Test Firestore data first
        testFirestoreData();

        // Then load user data
        loadUserDataFromFirestore();

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
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        authController = new AuthController();
    }
    
    private void setupClickListeners() {
        btnUpdateProfile.setOnClickListener(v -> updateProfile());
        btnChangePassword.setOnClickListener(v -> showChangePasswordDialog());

        etDob.setOnClickListener(v -> showDatePicker());
        etDob.setFocusable(false);
        etDob.setClickable(true);

        ivAvatar.setOnClickListener(v -> {
            // TODO: Implement avatar selection
            Toast.makeText(this, "Chức năng chọn avatar sẽ được cập nhật sau", Toast.LENGTH_SHORT).show();
        });

        // Logout button click listener
        btnLogout.setOnClickListener(v -> showLogoutDialog());

        // Add refresh function for testing - long press on toolbar
        findViewById(R.id.toolbar).setOnLongClickListener(v -> {
            Toast.makeText(this, "Refreshing data...", Toast.LENGTH_SHORT).show();
            loadUserDataFromFirestore();
            return true;
        });
    }

    private void loadUserDataFromFirestore() {
        // Show loading
        showLoading(true);

        // Get current user ID
        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(this, "Người dùng chưa đăng nhập", Toast.LENGTH_SHORT).show();
            showLoading(false);
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();
        String userEmail = mAuth.getCurrentUser().getEmail();

        Log.d(TAG, "Loading user data for ID: " + userId);

        // Get data from Firestore
        db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    showLoading(false);

                    if (documentSnapshot.exists()) {
                        Log.d(TAG, "Document found, loading data...");

                        // Get data from document
                        String fullname = documentSnapshot.getString("fullname");
                        String dob = documentSnapshot.getString("dob");
                        String address = documentSnapshot.getString("address");
                        String phone = documentSnapshot.getString("phone");
                        String email = documentSnapshot.getString("email");
                        String role = documentSnapshot.getString("role");

                        Log.d(TAG, "Data loaded - Fullname: " + fullname + ", DOB: " + dob +
                              ", Address: " + address + ", Phone: " + phone);

                        // Set data to UI
                        etFullname.setText(fullname != null ? fullname : "");
                        etDob.setText(dob != null ? dob : "");
                        etAddress.setText(address != null ? address : "");
                        etPhone.setText(phone != null ? phone : "");
                        tvEmail.setText(email != null ? email : userEmail);

                        // Set role
                        String roleDisplay = getRoleDisplayName(role != null ? role : "user");
                        tvRole.setText(roleDisplay);

                        Toast.makeText(this, "Đã tải thông tin cá nhân", Toast.LENGTH_SHORT).show();

                    } else {
                        Log.d(TAG, "No document found, creating new profile...");
                        createNewUserProfileSimple(userId, userEmail);
                    }
                })
                .addOnFailureListener(e -> {
                    showLoading(false);
                    Log.e(TAG, "Error loading user data", e);
                    Toast.makeText(this, "Lỗi khi tải thông tin: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void createNewUserProfileSimple(String userId, String email) {
        Log.d(TAG, "Creating new user profile...");

        // Create new user data
        Map<String, Object> userData = new HashMap<>();
        userData.put("uid", userId);
        userData.put("fullname", "");
        userData.put("dob", "");
        userData.put("address", "");
        userData.put("phone", "");
        userData.put("email", email);
        userData.put("role", "user");
        userData.put("avatar", "");

        // Save to Firestore
        db.collection("users")
                .document(userId)
                .set(userData)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "New profile created successfully");
                    Toast.makeText(this, "Đã tạo hồ sơ mới", Toast.LENGTH_SHORT).show();

                    // Set empty data to UI
                    etFullname.setText("");
                    etDob.setText("");
                    etAddress.setText("");
                    etPhone.setText("");
                    tvEmail.setText(email);
                    tvRole.setText("Khách hàng");
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error creating new profile", e);
                    Toast.makeText(this, "Lỗi khi tạo hồ sơ: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void loadUserProfile() {
        showLoading(true);

        String userId = mAuth.getCurrentUser().getUid();
        String userEmail = mAuth.getCurrentUser().getEmail();

        db.collection("users")
                .document(userId)
                .get()
                .addOnCompleteListener(task -> {
                    showLoading(false);

                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d(TAG, "Document data: " + document.getData());
                            currentUser = document.toObject(User.class);
                            if (currentUser != null) {
                                Log.d(TAG, "User loaded - Fullname: " + currentUser.getFullname() +
                                      ", DOB: " + currentUser.getDob() +
                                      ", Address: " + currentUser.getAddress() +
                                      ", Phone: " + currentUser.getPhone());
                                // Set email from Firebase Auth if not in database
                                if (currentUser.getEmail() == null || currentUser.getEmail().isEmpty()) {
                                    currentUser.setEmail(userEmail);
                                }
                                ensureAllFieldsExist();
                                displayUserInfo();
                            } else {
                                Log.e(TAG, "Failed to convert document to User object");
                                Toast.makeText(this, "Lỗi khi chuyển đổi dữ liệu người dùng", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.d(TAG, "No such document, creating new user profile");
                            // Create new user profile if doesn't exist
                            createNewUserProfile(userId, userEmail);
                        }
                    } else {
                        Log.e(TAG, "Error getting document: ", task.getException());
                        Toast.makeText(this, "Lỗi khi tải thông tin: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Method to manually fix missing fields for existing users
    private void ensureAllFieldsExist() {
        if (currentUser == null) return;

        String userId = mAuth.getCurrentUser().getUid();
        Map<String, Object> updates = new HashMap<>();
        boolean needsUpdate = false;

        if (currentUser.getDob() == null) {
            updates.put("dob", "");
            currentUser.setDob("");
            needsUpdate = true;
        }

        if (currentUser.getAddress() == null) {
            updates.put("address", "");
            currentUser.setAddress("");
            needsUpdate = true;
        }

        if (currentUser.getPhone() == null) {
            updates.put("phone", "");
            currentUser.setPhone("");
            needsUpdate = true;
        }

        if (currentUser.getAvatar() == null) {
            updates.put("avatar", "");
            currentUser.setAvatar("");
            needsUpdate = true;
        }

        if (needsUpdate) {
            Log.d(TAG, "Updating missing fields: " + updates.keySet());
            db.collection("users")
                    .document(userId)
                    .update(updates)
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "Missing fields updated successfully");
                        displayUserInfo();
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Failed to update missing fields", e);
                    });
        }
    }

    private void createNewUserProfile(String userId, String email) {
        currentUser = new User();
        currentUser.setEmail(email);
        currentUser.setFullname("Người dùng mới");
        currentUser.setRole("user");
        currentUser.setDob("");
        currentUser.setAddress("");
        currentUser.setPhone("");
        currentUser.setAvatar("");

        // Save to Firestore
        db.collection("users")
                .document(userId)
                .set(currentUser)
                .addOnSuccessListener(aVoid -> {
                    displayUserInfo();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi khi tạo profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
    
    private void displayUserInfo() {
        if (currentUser != null) {
            Log.d(TAG, "Displaying user info:");
            Log.d(TAG, "Fullname: " + currentUser.getFullname());
            Log.d(TAG, "DOB: " + currentUser.getDob());
            Log.d(TAG, "Address: " + currentUser.getAddress());
            Log.d(TAG, "Phone: " + currentUser.getPhone());
            Log.d(TAG, "Email: " + currentUser.getEmail());
            Log.d(TAG, "Role: " + currentUser.getRole());

            // Set text with null checks
            etFullname.setText(currentUser.getFullname() != null ? currentUser.getFullname() : "");
            etDob.setText(currentUser.getDob() != null ? currentUser.getDob() : "");
            etAddress.setText(currentUser.getAddress() != null ? currentUser.getAddress() : "");
            etPhone.setText(currentUser.getPhone() != null ? currentUser.getPhone() : "");
            tvEmail.setText(currentUser.getEmail() != null ? currentUser.getEmail() : "");

            // Display role in Vietnamese
            String roleText = getRoleDisplayName(currentUser.getRole());
            tvRole.setText(roleText);

            // TODO: Load avatar image
            // Glide.with(this).load(currentUser.getAvatar()).into(ivAvatar);
        } else {
            Log.e(TAG, "currentUser is null in displayUserInfo");
        }
    }

    private void updateProfile() {
        if (!validateInput()) return;

        showLoading(true);

        String fullname = etFullname.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String dob = etDob.getText().toString().trim();

        String userId = mAuth.getCurrentUser().getUid();

        // Create user data map
        Map<String, Object> userData = new HashMap<>();
        userData.put("fullname", fullname);
        userData.put("phone", phone);
        userData.put("address", address);
        userData.put("dob", dob);
        userData.put("updatedAt", System.currentTimeMillis());

        // Update in Firestore
        db.collection("users")
                .document(userId)
                .update(userData)
                .addOnSuccessListener(aVoid -> {
                    showLoading(false);
                    Toast.makeText(this, "Cập nhật thông tin thành công!", Toast.LENGTH_SHORT).show();
                    // Update current user object
                    currentUser.setFullname(fullname);
                    currentUser.setPhone(phone);
                    currentUser.setAddress(address);
                    currentUser.setDob(dob);
                })
                .addOnFailureListener(e -> {
                    showLoading(false);
                    Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private boolean validateInput() {
        String fullname = etFullname.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();

        if (fullname.isEmpty()) {
            etFullname.setError("Vui lòng nhập họ tên");
            etFullname.requestFocus();
            return false;
        }

        if (phone.isEmpty()) {
            etPhone.setError("Vui lòng nhập số điện thoại");
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

    private void showChangePasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_change_password, null);
        builder.setView(dialogView);

        EditText etCurrentPassword = dialogView.findViewById(R.id.et_current_password);
        EditText etNewPassword = dialogView.findViewById(R.id.et_new_password);
        EditText etConfirmPassword = dialogView.findViewById(R.id.et_confirm_password);
        Button btnChangePassword = dialogView.findViewById(R.id.btn_change_password);
        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);

        AlertDialog dialog = builder.create();

        btnChangePassword.setOnClickListener(v -> {
            String currentPassword = etCurrentPassword.getText().toString().trim();
            String newPassword = etNewPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();

            if (validatePasswordInput(currentPassword, newPassword, confirmPassword)) {
                changePassword(currentPassword, newPassword, dialog);
            }
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private boolean validatePasswordInput(String currentPassword, String newPassword, String confirmPassword) {
        if (currentPassword.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập mật khẩu hiện tại", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (newPassword.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập mật khẩu mới", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (newPassword.length() < 6) {
            Toast.makeText(this, "Mật khẩu mới phải có ít nhất 6 ký tự", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(this, "Mật khẩu xác nhận không khớp", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void changePassword(String currentPassword, String newPassword, AlertDialog dialog) {
        showLoading(true);

        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        // Re-authenticate user with current password
        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPassword);

        user.reauthenticate(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Update password
                        user.updatePassword(newPassword)
                                .addOnCompleteListener(updateTask -> {
                                    showLoading(false);
                                    if (updateTask.isSuccessful()) {
                                        Toast.makeText(this, "Đổi mật khẩu thành công!", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    } else {
                                        Toast.makeText(this, "Lỗi khi đổi mật khẩu: " + updateTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        showLoading(false);
                        Toast.makeText(this, "Mật khẩu hiện tại không đúng", Toast.LENGTH_SHORT).show();
                    }
                });
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

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data when returning to this activity
        if (mAuth != null && mAuth.getCurrentUser() != null) {
            Log.d(TAG, "onResume - reloading user profile");
            loadUserDataFromFirestore();
        }
    }

    // Method to test and debug Firestore data
    private void testFirestoreData() {
        if (mAuth.getCurrentUser() == null) return;

        String userId = mAuth.getCurrentUser().getUid();
        Log.d(TAG, "=== TESTING FIRESTORE DATA ===");
        Log.d(TAG, "User ID: " + userId);

        db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    Log.d(TAG, "Document exists: " + documentSnapshot.exists());
                    if (documentSnapshot.exists()) {
                        Log.d(TAG, "All document data: " + documentSnapshot.getData());

                        // Test each field individually
                        Log.d(TAG, "fullname: '" + documentSnapshot.getString("fullname") + "'");
                        Log.d(TAG, "dob: '" + documentSnapshot.getString("dob") + "'");
                        Log.d(TAG, "address: '" + documentSnapshot.getString("address") + "'");
                        Log.d(TAG, "phone: '" + documentSnapshot.getString("phone") + "'");
                        Log.d(TAG, "email: '" + documentSnapshot.getString("email") + "'");
                        Log.d(TAG, "role: '" + documentSnapshot.getString("role") + "'");
                        Log.d(TAG, "avatar: '" + documentSnapshot.getString("avatar") + "'");
                    }
                    Log.d(TAG, "=== END TEST ===");
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error testing Firestore data", e);
                });
    }
}
