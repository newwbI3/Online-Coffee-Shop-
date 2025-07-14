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

public class FeedbackAdapter extends RecyclerView.Adapter<FeedbackAdapter.FeedbackViewHolder> {

    private final Context context;
    private final List<Feedback> feedbackList;

    public FeedbackAdapter(Context context, List<Feedback> feedbackList) {
        this.context = context;
        this.feedbackList = feedbackList;
    }

    @NonNull
    @Override
    public FeedbackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_feedback, parent, false);
        return new FeedbackViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedbackViewHolder holder, int position) {
        Feedback feedback = feedbackList.get(position);

        holder.tvUserName.setText(feedback.getUserName());
        holder.ratingBar.setRating(feedback.getRating());
        holder.tvComment.setText(feedback.getComment());
        holder.tvDate.setText(formatDate(feedback.getTimestamp()));

        // Show rating text
        holder.tvRatingText.setText(String.format("%.1f/5", feedback.getRating()));
    }

    @Override
    public int getItemCount() {
        return feedbackList.size();
    }

    public static class FeedbackViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserName, tvComment, tvDate, tvRatingText;
        RatingBar ratingBar;

        public FeedbackViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.tv_user_name);
            tvComment = itemView.findViewById(R.id.tv_comment);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvRatingText = itemView.findViewById(R.id.tv_rating_text);
            ratingBar = itemView.findViewById(R.id.rating_bar);
        }
    }

    private String formatDate(long timestamp) {
        return new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                .format(new Date(timestamp));
    }
}
