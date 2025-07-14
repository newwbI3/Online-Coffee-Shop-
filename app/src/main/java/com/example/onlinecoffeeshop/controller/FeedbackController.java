package com.example.onlinecoffeeshop.controller;

import com.example.onlinecoffeeshop.model.Feedback;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FeedbackController {
    private DatabaseReference feedbackRef;

    public FeedbackController() {
        feedbackRef = FirebaseDatabase.getInstance().getReference("feedbacks");
    }

    public interface OnFeedbackAddedListener {
        void onSuccess();
        void onFailure(String error);
    }

    public interface OnFeedbackLoadedListener {
        void onSuccess(List<Feedback> feedbacks);
        void onFailure(String error);
    }

    public void addFeedback(Feedback feedback, OnFeedbackAddedListener listener) {
        String feedbackId = feedbackRef.push().getKey();
        if (feedbackId != null) {
            feedback.setFeedbackId(feedbackId);
            feedbackRef.child(feedbackId).setValue(feedback)
                .addOnSuccessListener(aVoid -> listener.onSuccess())
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
        } else {
            listener.onFailure("Không thể tạo ID feedback");
        }
    }

    public void getFeedbacksByProductId(String productId, OnFeedbackLoadedListener listener) {
        feedbackRef.orderByChild("productId").equalTo(productId)
            .addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    List<Feedback> feedbacks = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Feedback feedback = snapshot.getValue(Feedback.class);
                        if (feedback != null) {
                            feedbacks.add(feedback);
                        }
                    }
                    listener.onSuccess(feedbacks);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    listener.onFailure(databaseError.getMessage());
                }
            });
    }

    public void checkUserFeedbackForProduct(String userId, String productId, String orderId, OnFeedbackLoadedListener listener) {
        feedbackRef.orderByChild("userId").equalTo(userId)
            .addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    List<Feedback> feedbacks = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Feedback feedback = snapshot.getValue(Feedback.class);
                        if (feedback != null && feedback.getProductId().equals(productId) && feedback.getOrderId().equals(orderId)) {
                            feedbacks.add(feedback);
                        }
                    }
                    listener.onSuccess(feedbacks);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    listener.onFailure(databaseError.getMessage());
                }
            });
    }
}
