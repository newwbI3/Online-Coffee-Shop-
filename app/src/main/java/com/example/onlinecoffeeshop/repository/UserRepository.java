package com.example.onlinecoffeeshop.repository;

import com.example.onlinecoffeeshop.model.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class UserRepository {
    private static final String COLLECTION_NAME = "users";
    private final FirebaseFirestore db;
    
    public UserRepository() {
        this.db = FirebaseFirestore.getInstance();
    }
    
    // Create or update user
    public Task<Void> saveUser(User user) {
        if (user.getUid() != null) {
            return db.collection(COLLECTION_NAME)
                    .document(user.getUid())
                    .set(user);
        } else {
            throw new IllegalArgumentException("User UID cannot be null");
        }
    }
    
    // Get user by UID
    public Task<DocumentSnapshot> getUserByUid(String uid) {
        return db.collection(COLLECTION_NAME)
                .document(uid)
                .get();
    }
    
    // Get user by email
    public Task<QuerySnapshot> getUserByEmail(String email) {
        return db.collection(COLLECTION_NAME)
                .whereEqualTo("email", email)
                .limit(1)
                .get();
    }
    
    // Update user profile
    public Task<Void> updateUserProfile(String uid, String fullname, String dob, 
                                       String avatar, String address, String phone) {
        DocumentReference userRef = db.collection(COLLECTION_NAME).document(uid);
        
        return userRef.update(
                "fullname", fullname,
                "dob", dob,
                "avatar", avatar,
                "address", address,
                "phone", phone
        );
    }
    
    // Update user role (admin only)
    public Task<Void> updateUserRole(String uid, String role) {
        if (!User.VALID_ROLES.contains(role.toLowerCase())) {
            throw new IllegalArgumentException("Invalid role: " + role);
        }
        
        return db.collection(COLLECTION_NAME)
                .document(uid)
                .update("role", role.toLowerCase());
    }
    
    // Get all users (admin only)
    public Task<QuerySnapshot> getAllUsers() {
        return db.collection(COLLECTION_NAME)
                .orderBy("fullname")
                .get();
    }
    
    // Get users by role
    public Task<QuerySnapshot> getUsersByRole(String role) {
        if (!User.VALID_ROLES.contains(role.toLowerCase())) {
            throw new IllegalArgumentException("Invalid role: " + role);
        }
        
        return db.collection(COLLECTION_NAME)
                .whereEqualTo("role", role.toLowerCase())
                .orderBy("fullname")
                .get();
    }
    
    // Delete user (admin only)
    public Task<Void> deleteUser(String uid) {
        return db.collection(COLLECTION_NAME)
                .document(uid)
                .delete();
    }
    
    // Check if user exists
    public Task<DocumentSnapshot> checkUserExists(String uid) {
        return db.collection(COLLECTION_NAME)
                .document(uid)
                .get();
    }
    
    // Search users by name
    public Task<QuerySnapshot> searchUsersByName(String searchTerm) {
        return db.collection(COLLECTION_NAME)
                .whereGreaterThanOrEqualTo("fullname", searchTerm)
                .whereLessThanOrEqualTo("fullname", searchTerm + "\uf8ff")
                .orderBy("fullname")
                .get();
    }
}
