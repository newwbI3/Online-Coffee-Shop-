package com.example.onlinecoffeeshop.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.onlinecoffeeshop.R;
import com.example.onlinecoffeeshop.controller.FeedbackController;
import com.example.onlinecoffeeshop.helper.SessionManager;
import com.example.onlinecoffeeshop.model.CartItem;
import com.example.onlinecoffeeshop.view.order.ProductFeedbackActivity;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class FeedbackItemAdapter extends RecyclerView.Adapter<FeedbackItemAdapter.FeedbackItemViewHolder> {

    private final Context context;
    private final List<CartItem> items;
    private final String orderId;
    private final FeedbackController feedbackController;
    private final SessionManager sessionManager;

    public FeedbackItemAdapter(Context context, List<CartItem> items, String orderId) {
        this.context = context;
        this.items = items;
        this.orderId = orderId;
        this.feedbackController = new FeedbackController();
        this.sessionManager = new SessionManager(context);
    }

    @NonNull
    @Override
    public FeedbackItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_feedback_product, parent, false);
        return new FeedbackItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedbackItemViewHolder holder, int position) {
        CartItem item = items.get(position);

        try {
            Glide.with(context).load(item.getImageUrl()).into(holder.ivProductImage);
            holder.tvProductTitle.setText(item.getTitle());
            holder.tvProductPrice.setText(formatCurrency(item.getPrice()));
            holder.tvProductQuantity.setText("Số lượng: " + item.getQuantity());

            // Check if user already provided feedback for this product in this order
            String userId = sessionManager.getUserId();
            if (userId != null) {
                feedbackController.checkUserFeedbackForProduct(userId, item.getProductId(), orderId,
                    new FeedbackController.OnFeedbackLoadedListener() {
                        @Override
                        public void onSuccess(List<com.example.onlinecoffeeshop.model.Feedback> feedbacks) {
                            if (feedbacks.isEmpty()) {
                                holder.btnFeedback.setText("Đánh giá");
                                holder.btnFeedback.setEnabled(true);
                                holder.btnFeedback.setOnClickListener(v -> {
                                    try {
                                        Intent intent = new Intent(context, ProductFeedbackActivity.class);
                                        intent.putExtra("productId", item.getProductId());
                                        intent.putExtra("productName", item.getTitle());
                                        intent.putExtra("orderId", orderId);
                                        context.startActivity(intent);
                                    } catch (Exception e) {
                                        Toast.makeText(context, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                holder.btnFeedback.setText("Đã đánh giá");
                                holder.btnFeedback.setEnabled(false);
                            }
                        }

                        @Override
                        public void onFailure(String error) {
                            holder.btnFeedback.setText("Đánh giá");
                            holder.btnFeedback.setEnabled(true);
                            holder.btnFeedback.setOnClickListener(v -> {
                                try {
                                    Intent intent = new Intent(context, ProductFeedbackActivity.class);
                                    intent.putExtra("productId", item.getProductId());
                                    intent.putExtra("productName", item.getTitle());
                                    intent.putExtra("orderId", orderId);
                                    context.startActivity(intent);
                                } catch (Exception e) {
                                    Toast.makeText(context, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
            }
        } catch (Exception e) {
            Toast.makeText(context, "Lỗi khi tải sản phẩm: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class FeedbackItemViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProductImage;
        TextView tvProductTitle, tvProductPrice, tvProductQuantity;
        Button btnFeedback;

        public FeedbackItemViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProductImage = itemView.findViewById(R.id.iv_product_image);
            tvProductTitle = itemView.findViewById(R.id.tv_product_title);
            tvProductPrice = itemView.findViewById(R.id.tv_product_price);
            tvProductQuantity = itemView.findViewById(R.id.tv_product_quantity);
            btnFeedback = itemView.findViewById(R.id.btn_feedback);
        }
    }

    private String formatCurrency(double value) {
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        return format.format(value).replace("₫", "VNĐ");
    }
}
