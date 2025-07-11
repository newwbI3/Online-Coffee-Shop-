package com.example.onlinecoffeeshop.view.favorite;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.onlinecoffeeshop.base.BaseActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

        ImageView backBtnFav = findViewById(R.id.backBtnFav);
        backBtnFav.setOnClickListener(v -> finish());

        loadFavorites();
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
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(FavoriteActivity.this, "Failed to load favorites", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
