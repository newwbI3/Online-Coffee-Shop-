package com.example.onlinecoffeeshop.view.auth;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.onlinecoffeeshop.MainActivity;
import com.example.onlinecoffeeshop.R;
import com.example.onlinecoffeeshop.controller.AuthController;
import com.example.onlinecoffeeshop.model.User;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";

    private EditText etFullname, etEmail, etPassword, etConfirmPassword,
                     etDob, etAddress, etPhone;
    private Button btnRegister;
    private TextView tvLogin;
    private ProgressBar progressBar;

    private AuthController authController;
    private Calendar calendar;
    private SimpleDateFormat dateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initViews();
        initController();
        setupClickListeners();

        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    }

    private void initViews() {
        etFullname = findViewById(R.id.et_fullname);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        etDob = findViewById(R.id.et_dob);
        etAddress = findViewById(R.id.et_address);
        etPhone = findViewById(R.id.et_phone);
        btnRegister = findViewById(R.id.btn_register);
        tvLogin = findViewById(R.id.tv_login);
        progressBar = findViewById(R.id.progress_bar);
    }

    private void initController() {
        authController = new AuthController(this);
    }

    private void setupClickListeners() {
        btnRegister.setOnClickListener(v -> registerUser());

        tvLogin.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        etDob.setOnClickListener(v -> showDatePicker());
        etDob.setFocusable(false);
        etDob.setClickable(true);
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

        // Set max date to today (user must be born before today)
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void registerUser() {
        String fullname = etFullname.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();
        String dob = etDob.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();

        if (!validateInput(fullname, email, password, confirmPassword, dob, address, phone)) {
            return;
        }

        showLoading(true);

        authController.registerUser(email, password, fullname, dob, address, phone)
                .addOnCompleteListener(task -> {
                    showLoading(false);

                    if (task.isSuccessful()) {
                        User user = task.getResult();
                        Log.d(TAG, "Registration successful: " + user.getFullname());
                        Toast.makeText(this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                        navigateToMain();
                    } else {
                        String errorMessage = getErrorMessage(task.getException());
                        Log.e(TAG, "Registration failed", task.getException());
                        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private boolean validateInput(String fullname, String email, String password,
                                 String confirmPassword, String dob, String address, String phone) {
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

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email không được để trống");
            etEmail.requestFocus();
            return false;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Email không hợp lệ");
            etEmail.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Mật khẩu không được để trống");
            etPassword.requestFocus();
            return false;
        }

        if (password.length() < 6) {
            etPassword.setError("Mật khẩu phải có ít nhất 6 ký tự");
            etPassword.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            etConfirmPassword.setError("Xác nhận mật khẩu không được để trống");
            etConfirmPassword.requestFocus();
            return false;
        }

        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Mật khẩu xác nhận không khớp");
            etConfirmPassword.requestFocus();
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

    private void showLoading(boolean show) {
        if (show) {
            progressBar.setVisibility(View.VISIBLE);
            btnRegister.setEnabled(false);
        } else {
            progressBar.setVisibility(View.GONE);
            btnRegister.setEnabled(true);
        }
    }

    private void navigateToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private String getErrorMessage(Exception exception) {
        if (exception == null) {
            return "Có lỗi xảy ra";
        }

        String message = exception.getMessage();
        if (message != null) {
            if (message.contains("email address is already in use")) {
                return "Email đã được sử dụng";
            } else if (message.contains("weak password")) {
                return "Mật khẩu quá yếu";
            } else if (message.contains("network error")) {
                return "Lỗi kết nối mạng";
            }
        }

        return "Đăng ký thất bại. Vui lòng thử lại";
    }
}
