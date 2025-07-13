package com.example.onlinecoffeeshop;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.onlinecoffeeshop.base.BaseActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.onlinecoffeeshop.adapter.CategoryAdapter;
import com.example.onlinecoffeeshop.adapter.PopularAdapter;
import com.example.onlinecoffeeshop.controller.HomeController;
import com.example.onlinecoffeeshop.model.Category;
import com.example.onlinecoffeeshop.model.Product;
import com.example.onlinecoffeeshop.view.auth.LoginActivity;
import com.example.onlinecoffeeshop.view.cart.CartActivity;
import com.example.onlinecoffeeshop.view.product.ListProductActivity;
import com.example.onlinecoffeeshop.utils.RoleAuthenticator;
import com.example.onlinecoffeeshop.view.profile.ProfileActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {
    private ImageView imgBanner, searchView;
    private ProgressBar progressBarBanner, progressBarCat, progressBarDrink;
    private RecyclerView recyclerViewCat, recyclerViewDrinks;
    private FirebaseAuth mAuth;
    private TextView listProductView;
    private EditText searchEditText;
    private HomeController controller;
    private LinearLayout cartLayout, profileLayout, favoriteLayout;
    private ValueEventListener bannerListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        controller = new HomeController();

        // Check user role on app start
        checkUserRoleOnStart();

        initView();

        searchProduct();
        loadBanner();
        loadCategories();
        loadPopularDrinks();
        ViewListProduct();
        OrderProduct();
        getLayoutProfile();
        getLayoutCart();
        getLayoutFavorite();
    }

    private void OrderProduct() {
        imgBanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ListProductActivity.class);
                startActivity(intent);
            }
        });
    }

    private void ViewListProduct() {
        listProductView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ListProductActivity.class);
                startActivity(intent);
            }
        });
    }

    private void getLayoutCart() {
        cartLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CartActivity.class);
                startActivity(intent);
            }
        });
    }

    private void getLayoutProfile() {
        profileLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mAuth.getCurrentUser() == null){
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    return;
                }
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });
    }

    private void getLayoutFavorite() {
        favoriteLayout.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, com.example.onlinecoffeeshop.view.favorite.FavoriteActivity.class);
            startActivity(intent);
        });
    }

    private void initView() {
        imgBanner = findViewById(R.id.imgBanner);
        progressBarBanner = findViewById(R.id.progressBarBanner);
        progressBarCat = findViewById(R.id.progressBarCat);
        progressBarDrink = findViewById(R.id.progressBarDrink);
        recyclerViewCat = findViewById(R.id.recyclerViewCat);
        recyclerViewDrinks = findViewById(R.id.recyclerViewDrinks);
        listProductView = findViewById(R.id.listProductView);
        cartLayout = findViewById(R.id.layoutCart);
        profileLayout = findViewById(R.id.layoutProfile);
        favoriteLayout = findViewById(R.id.layoutFavorite);
        mAuth = FirebaseAuth.getInstance();
        searchEditText = findViewById(R.id.searchTxt);
        searchView = findViewById(R.id.searchView);
    }

    private void searchProduct() {
        searchView.setOnClickListener(v -> {
            String query = searchEditText.getText().toString().trim();
            if (!query.isEmpty()) {
                Intent intent = new Intent(MainActivity.this, ListProductActivity.class);
                intent.putExtra("searchQuery", query);
                startActivity(intent);
            } else {
                Toast.makeText(MainActivity.this, "Vui lòng nhập từ khóa tìm kiếm", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadBanner() {
        bannerListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (isDestroyed() || isFinishing()) {
                    return; // Don't load if activity is destroyed
                }

                progressBarBanner.setVisibility(View.GONE);
                for (DataSnapshot item : snapshot.getChildren()) {
                    String url = item.child("url").getValue(String.class);
                    if (!isDestroyed() && !isFinishing()) {
                        Glide.with(MainActivity.this).load(url).into(imgBanner);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };

        controller.loadBanner(bannerListener);
    }

    private void loadCategories() {
        controller.loadCategories(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressBarCat.setVisibility(View.GONE);
                List<Category> categories = new ArrayList<>();
                for (DataSnapshot item : snapshot.getChildren()) {
                    Category cat = item.getValue(Category.class);
                    categories.add(cat);
                }
                recyclerViewCat.setLayoutManager(new LinearLayoutManager(MainActivity.this, RecyclerView.HORIZONTAL, false));
                recyclerViewCat.setAdapter(new CategoryAdapter(MainActivity.this, categories));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }




    private void loadPopularDrinks() {
        controller.loadPopularItems(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressBarDrink.setVisibility(View.GONE);
                List<Product> products = new ArrayList<>();
                for (DataSnapshot item : snapshot.getChildren()) {
                    Product p = item.getValue(Product.class);
                    products.add(p);
                }
                recyclerViewDrinks.setLayoutManager(new GridLayoutManager(MainActivity.this, 2));
                recyclerViewDrinks.setAdapter(new PopularAdapter(MainActivity.this, products));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void checkUserRoleOnStart() {
        RoleAuthenticator.checkUserRole(new RoleAuthenticator.RoleCheckCallback() {
            @Override
            public void onRoleChecked(String role) {
                // If user has manager role, redirect to ManagerActivity
                if (RoleAuthenticator.isManagerRole(role)) {
                    RoleAuthenticator.checkUserRoleAndNavigate(MainActivity.this);
                }
                // Otherwise, stay in MainActivity (normal user flow)
            }

            @Override
            public void onError(String error) {
                // If error checking role, redirect to login
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Remove Firebase listeners to prevent callbacks on destroyed activity
        if (bannerListener != null) {
            // Note: We would need reference to the DatabaseReference to remove listener
            // For now, the isDestroyed() check in callback prevents the crash
        }
    }
}