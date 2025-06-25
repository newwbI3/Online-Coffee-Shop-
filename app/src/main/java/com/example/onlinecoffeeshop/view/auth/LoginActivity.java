package com.example.onlinecoffeeshop.view.auth;

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

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvRegister, tvForgotPassword;
    private ProgressBar progressBar;

    private AuthController authController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();
        initController();
        setupClickListeners();

        // Check if user is already logged in
        if (authController.isUserLoggedIn()) {
            navigateToMain();
        }
    }

    private void initViews() {
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        tvRegister = findViewById(R.id.tv_register);
        tvForgotPassword = findViewById(R.id.tv_forgot_password);
        progressBar = findViewById(R.id.progress_bar);
    }

    private void initController() {
        authController = new AuthController(this);
    }

    private void setupClickListeners() {
        btnLogin.setOnClickListener(v -> loginUser());

        tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        tvForgotPassword.setOnClickListener(v -> showForgotPasswordDialog());
    }

    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (!validateInput(email, password)) {
            return;
        }

        showLoading(true);

        authController.loginUser(email, password)
                .addOnCompleteListener(task -> {
                    showLoading(false);

                    if (task.isSuccessful()) {
                        User user = task.getResult();
                        Log.d(TAG, "Login successful: " + user.getFullname());
                        Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                        navigateToMain();
                    } else {
                        String errorMessage = getErrorMessage(task.getException());
                        Log.e(TAG, "Login failed", task.getException());
                        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private boolean validateInput(String email, String password) {
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

        return true;
    }

    private void showForgotPasswordDialog() {
        // Create a simple dialog for password reset
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Quên mật khẩu");
        builder.setMessage("Nhập email để nhận link đặt lại mật khẩu:");

        final EditText input = new EditText(this);
        input.setInputType(android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        builder.setView(input);

        builder.setPositiveButton("Gửi", (dialog, which) -> {
            String email = input.getText().toString().trim();
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(this, "Vui lòng nhập email", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Email không hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }

            showLoading(true);
            authController.resetPassword(email)
                    .addOnCompleteListener(task -> {
                        showLoading(false);
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Link đặt lại mật khẩu đã được gửi đến email của bạn",
                                         Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(this, "Có lỗi xảy ra. Vui lòng thử lại",
                                         Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void showLoading(boolean show) {
        if (show) {
            progressBar.setVisibility(View.VISIBLE);
            btnLogin.setEnabled(false);
        } else {
            progressBar.setVisibility(View.GONE);
            btnLogin.setEnabled(true);
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
            if (message.contains("password is invalid")) {
                return "Mật khẩu không đúng";
            } else if (message.contains("no user record")) {
                return "Email không tồn tại";
            } else if (message.contains("network error")) {
                return "Lỗi kết nối mạng";
            }
        }

        return "Đăng nhập thất bại. Vui lòng thử lại";
    }
}
