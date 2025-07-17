package com.example.onlinecoffeeshop.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onlinecoffeeshop.R;
import com.example.onlinecoffeeshop.model.Feedback;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class UserFeedbackAdapter extends RecyclerView.Adapter<UserFeedbackAdapter.FeedbackViewHolder> {
    
    private Context context;
    private List<Feedback> feedbackList;
    private SimpleDateFormat dateFormat;
    
    public UserFeedbackAdapter(Context context, List<Feedback> feedbackList) {
        this.context = context;
        this.feedbackList = feedbackList;
        this.dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
    }
    
    @NonNull
    @Override
    public FeedbackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user_feedback, parent, false);
        return new FeedbackViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull FeedbackViewHolder holder, int position) {
        Feedback feedback = feedbackList.get(position);
        
        // Set feedback content
        holder.tvFeedbackText.setText(feedback.getFeedbackText() != null ? feedback.getFeedbackText() : "Không có nội dung");
        
        // Set rating
        if (feedback.getRating() > 0) {
            holder.ratingBar.setVisibility(View.VISIBLE);
            holder.ratingBar.setRating(feedback.getRating());
        } else {
            holder.ratingBar.setVisibility(View.GONE);
        }
        
        // Set timestamp
        if (feedback.getTimestamp() > 0) {
            Date date = new Date(feedback.getTimestamp());
            holder.tvTimestamp.setText(dateFormat.format(date));
        } else {
            holder.tvTimestamp.setText("Không rõ thời gian");
        }
        
        // Set product name if available
        if (feedback.getProductName() != null && !feedback.getProductName().isEmpty()) {
            holder.tvProductName.setVisibility(View.VISIBLE);
            holder.tvProductName.setText("Sản phẩm: " + feedback.getProductName());
        } else {
            holder.tvProductName.setVisibility(View.GONE);
        }
    }
    
    @Override
    public int getItemCount() {
        return feedbackList.size();
    }
    
    public static class FeedbackViewHolder extends RecyclerView.ViewHolder {
        TextView tvFeedbackText, tvTimestamp, tvProductName;
        RatingBar ratingBar;
        
        public FeedbackViewHolder(@NonNull View itemView) {
            super(itemView);
            
            tvFeedbackText = itemView.findViewById(R.id.tvFeedbackText);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            ratingBar = itemView.findViewById(R.id.ratingBar);
        }
    }
}
