package com.example.onlinecoffeeshop.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.onlinecoffeeshop.R;
import com.example.onlinecoffeeshop.view.auth.LoginActivity;
import com.example.onlinecoffeeshop.view.profile.ProfileActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileFragment extends Fragment {
    private TextView userNameTxt, userEmailTxt;
    private LinearLayout editProfileLayout, changePasswordLayout, logoutLayout, helpLayout;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        try {
            initViews(view);
            setupFirebase();
            loadUserInfo();
            setupClickListeners();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return view;
    }

    private void initViews(View view) {
        try {
            userNameTxt = view.findViewById(R.id.userNameTxt);
            userEmailTxt = view.findViewById(R.id.userEmailTxt);
            editProfileLayout = view.findViewById(R.id.editProfileLayout);
            changePasswordLayout = view.findViewById(R.id.changePasswordLayout);
            logoutLayout = view.findViewById(R.id.logoutLayout);
            helpLayout = view.findViewById(R.id.helpLayout);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupFirebase() {
        try {
            mAuth = FirebaseAuth.getInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadUserInfo() {
        try {
            if (mAuth != null) {
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if (currentUser != null) {
                    String displayName = currentUser.getDisplayName();
                    String email = currentUser.getEmail();

                    if (userNameTxt != null) {
                        userNameTxt.setText(displayName != null ? displayName : "User");
                    }
                    if (userEmailTxt != null) {
                        userEmailTxt.setText(email != null ? email : "");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupClickListeners() {
        try {
            if (editProfileLayout != null) {
                editProfileLayout.setOnClickListener(v -> {
                    try {
                        if (getActivity() != null) {
                            Intent intent = new Intent(getActivity(), ProfileActivity.class);
                            startActivity(intent);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (getContext() != null) {
                            Toast.makeText(getContext(), "Error opening profile", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            if (changePasswordLayout != null) {
                changePasswordLayout.setOnClickListener(v -> {
                    try {
                        if (getContext() != null) {
                            Toast.makeText(getContext(), "Change Password - Coming Soon", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }

            if (helpLayout != null) {
                helpLayout.setOnClickListener(v -> {
                    try {
                        if (getContext() != null) {
                            Toast.makeText(getContext(), "Help & Support - Coming Soon", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }

            if (logoutLayout != null) {
                logoutLayout.setOnClickListener(v -> {
                    try {
                        logout();
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (getContext() != null) {
                            Toast.makeText(getContext(), "Error during logout", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void logout() {
        try {
            if (mAuth != null) {
                mAuth.signOut();
            }
            if (getActivity() != null) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                getActivity().finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (getContext() != null) {
                Toast.makeText(getContext(), "Error during logout", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            // Clean up references
            userNameTxt = null;
            userEmailTxt = null;
            editProfileLayout = null;
            changePasswordLayout = null;
            logoutLayout = null;
            helpLayout = null;
            mAuth = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
