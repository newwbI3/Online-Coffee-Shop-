package com.example.onlinecoffeeshop.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onlinecoffeeshop.R;
import com.example.onlinecoffeeshop.adapter.FavouriteAdapter;
import com.example.onlinecoffeeshop.model.FavouriteItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FavoriteFragment extends Fragment {
    private RecyclerView recyclerViewFavorites;
    private TextView emptyFavoritesTxt;
    private FavouriteAdapter favoriteAdapter;
    private List<FavouriteItem> favoriteItems;
    private DatabaseReference favoritesRef;
    private FirebaseAuth mAuth;
    private ValueEventListener favoritesListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);

        try {
            initViews(view);
            setupFirebase();
            loadFavoriteItems();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return view;
    }

    private void initViews(View view) {
        try {
            recyclerViewFavorites = view.findViewById(R.id.recyclerViewFavorites);
            emptyFavoritesTxt = view.findViewById(R.id.emptyFavoritesTxt);

            favoriteItems = new ArrayList<>();
            if (recyclerViewFavorites != null && getContext() != null) {
                recyclerViewFavorites.setLayoutManager(new GridLayoutManager(getContext(), 2));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupFirebase() {
        try {
            mAuth = FirebaseAuth.getInstance();
            if (mAuth.getCurrentUser() != null) {
                favoritesRef = FirebaseDatabase.getInstance().getReference("Favourite")
                        .child(mAuth.getCurrentUser().getUid());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadFavoriteItems() {
        try {
            if (favoritesRef == null) {
                showEmptyFavorites();
                return;
            }

            favoritesListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try {
                        if (favoriteItems != null) {
                            favoriteItems.clear();
                        } else {
                            favoriteItems = new ArrayList<>();
                        }

                        for (DataSnapshot favoriteSnapshot : snapshot.getChildren()) {
                            FavouriteItem item = favoriteSnapshot.getValue(FavouriteItem.class);
                            if (item != null) {
                                favoriteItems.add(item);
                            }
                        }

                        if (isAdded() && getActivity() != null) {
                            updateUI();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (isAdded()) {
                            showEmptyFavorites();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    if (isAdded() && getContext() != null) {
                        Toast.makeText(getContext(), "Failed to load favorites", Toast.LENGTH_SHORT).show();
                        showEmptyFavorites();
                    }
                }
            };

            favoritesRef.addValueEventListener(favoritesListener);
        } catch (Exception e) {
            e.printStackTrace();
            showEmptyFavorites();
        }
    }

    private void updateUI() {
        try {
            if (favoriteItems == null || favoriteItems.isEmpty()) {
                showEmptyFavorites();
            } else {
                showFavoriteItems();
            }
        } catch (Exception e) {
            e.printStackTrace();
            showEmptyFavorites();
        }
    }

    private void showEmptyFavorites() {
        try {
            if (emptyFavoritesTxt != null) {
                emptyFavoritesTxt.setVisibility(View.VISIBLE);
            }
            if (recyclerViewFavorites != null) {
                recyclerViewFavorites.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showFavoriteItems() {
        try {
            if (emptyFavoritesTxt != null) {
                emptyFavoritesTxt.setVisibility(View.GONE);
            }
            if (recyclerViewFavorites != null) {
                recyclerViewFavorites.setVisibility(View.VISIBLE);
            }

            if (favoriteAdapter == null && mAuth.getCurrentUser() != null && getContext() != null && favoriteItems != null) {
                favoriteAdapter = new FavouriteAdapter(getContext(), favoriteItems, mAuth.getCurrentUser().getUid());
                if (recyclerViewFavorites != null) {
                    recyclerViewFavorites.setAdapter(favoriteAdapter);
                }
            } else if (favoriteAdapter != null) {
                favoriteAdapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeFavorite(String productId) {
        try {
            if (favoritesRef != null && productId != null) {
                favoritesRef.child(productId).removeValue();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            // Remove Firebase listener to prevent memory leaks
            if (favoritesRef != null && favoritesListener != null) {
                favoritesRef.removeEventListener(favoritesListener);
            }

            // Clean up references
            recyclerViewFavorites = null;
            emptyFavoritesTxt = null;
            favoriteAdapter = null;
            favoriteItems = null;
            favoritesRef = null;
            favoritesListener = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
