package com.example.onlinecoffeeshop.view.favorite;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.onlinecoffeeshop.base.BaseActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onlinecoffeeshop.MainActivity;
import com.example.onlinecoffeeshop.R;
import com.example.onlinecoffeeshop.adapter.FavouriteAdapter;
import com.example.onlinecoffeeshop.controller.FavouriteController;
import com.example.onlinecoffeeshop.model.FavouriteItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FavoriteActivity extends BaseActivity {
    private RecyclerView recyclerViewFavorite;
    private FavouriteAdapter adapter;
    private List<FavouriteItem> favList;
    private FavouriteController favouriteController;
    private String userId;
    private LinearLayout emptyStateFavorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        userId = (FirebaseAuth.getInstance().getCurrentUser() != null)
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                :userId;
        favouriteController = new FavouriteController(userId);

        recyclerViewFavorite = findViewById(R.id.recyclerViewFavorite);
        recyclerViewFavorite.setLayoutManager(new LinearLayoutManager(this));
        favList = new ArrayList<>();
        adapter = new FavouriteAdapter(this, favList, userId);
        recyclerViewFavorite.setAdapter(adapter);

        emptyStateFavorite = findViewById(R.id.emptyStateFavorite);

        Button btnBrowseProducts = findViewById(R.id.btn_browse_products);
        btnBrowseProducts.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });

        loadFavorites();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload favorites when activity resumes
        loadFavorites();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remove listener to prevent memory leaks
        if (favouriteController != null) {
            favouriteController.removeListener();
        }
    }

    private void loadFavorites() {
        favouriteController.getFavourites(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                favList.clear();
                for (DataSnapshot item : snapshot.getChildren()) {
                    FavouriteItem fav = item.getValue(FavouriteItem.class);
                    if (fav != null) favList.add(fav);
                }
                adapter.notifyDataSetChanged();

                // Show/hide empty state
                if (favList.isEmpty()) {
                    recyclerViewFavorite.setVisibility(View.GONE);
                    emptyStateFavorite.setVisibility(View.VISIBLE);
                } else {
                    recyclerViewFavorite.setVisibility(View.VISIBLE);
                    emptyStateFavorite.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(FavoriteActivity.this, "Failed to load favorites", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
