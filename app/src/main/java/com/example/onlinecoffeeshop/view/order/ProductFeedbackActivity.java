package com.example.onlinecoffeeshop.view.order;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.onlinecoffeeshop.R;
import com.example.onlinecoffeeshop.controller.FeedbackController;
import com.example.onlinecoffeeshop.helper.SessionManager;
import com.example.onlinecoffeeshop.model.Feedback;

public class ProductFeedbackActivity extends AppCompatActivity {

    private TextView tvProductName;
    private RatingBar ratingBar;
    private EditText etComment;
    private Button btnSubmitFeedback;

    private String productId;
    private String productName;
    private String orderId;
    private FeedbackController feedbackController;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_feedback);

        initViews();
        getIntentData();
        setupClickListeners();

        feedbackController = new FeedbackController();
        sessionManager = new SessionManager(this);
    }

    private void initViews() {
        tvProductName = findViewById(R.id.tv_product_name);
        ratingBar = findViewById(R.id.rating_bar);
        etComment = findViewById(R.id.et_comment);
        btnSubmitFeedback = findViewById(R.id.btn_submit_feedback);
    }

    private void getIntentData() {
        productId = getIntent().getStringExtra("productId");
        productName = getIntent().getStringExtra("productName");
        orderId = getIntent().getStringExtra("orderId");

        if (productName != null) {
            tvProductName.setText(productName);
        }
    }

    private void setupClickListeners() {
        btnSubmitFeedback.setOnClickListener(v -> submitFeedback());
    }

    private void submitFeedback() {
        float rating = ratingBar.getRating();
        String comment = etComment.getText().toString().trim();

        if (rating == 0) {
            Toast.makeText(this, "Vui lòng chọn số sao đánh giá", Toast.LENGTH_SHORT).show();
            return;
        }

        if (comment.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập nhận xét", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = sessionManager.getUserId();
        String userName = sessionManager.getUserName();

        Feedback feedback = new Feedback(
            productId,
            userId,
            userName,
            orderId,
            rating,
            comment,
            System.currentTimeMillis()
        );

        feedbackController.addFeedback(feedback, new FeedbackController.OnFeedbackAddedListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(ProductFeedbackActivity.this, "Cảm ơn bạn đã đánh giá sản phẩm!", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(ProductFeedbackActivity.this, "Lỗi: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
