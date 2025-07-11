package com.example.onlinecoffeeshop.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.onlinecoffeeshop.R;
import com.example.onlinecoffeeshop.adapter.CategoryAdapter;
import com.example.onlinecoffeeshop.adapter.PopularAdapter;
import com.example.onlinecoffeeshop.controller.HomeController;
import com.example.onlinecoffeeshop.model.Category;
import com.example.onlinecoffeeshop.model.Product;
import com.example.onlinecoffeeshop.view.product.ListProductActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private ImageView imgBanner, searchView;
    private ProgressBar progressBarBanner, progressBarCat, progressBarDrink;
    private RecyclerView recyclerViewCat, recyclerViewDrinks;
    private TextView listProductView;
    private EditText searchEditText;
    private HomeController controller;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        try {
            controller = new HomeController();
            initViews(view);
            setupListeners();
            loadData();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return view;
    }

    private void initViews(View view) {
        try {
            imgBanner = view.findViewById(R.id.bannerImg);
            searchView = view.findViewById(R.id.searchView);
            progressBarBanner = view.findViewById(R.id.progressBarBanner);
            progressBarCat = view.findViewById(R.id.progressBarCat);
            progressBarDrink = view.findViewById(R.id.progressBarDrink);
            recyclerViewCat = view.findViewById(R.id.categoryView);
            recyclerViewDrinks = view.findViewById(R.id.recyclerViewDrinks);
            listProductView = view.findViewById(R.id.listProductView);
            searchEditText = view.findViewById(R.id.searchTxt);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupListeners() {
        try {
            if (imgBanner != null) {
                imgBanner.setOnClickListener(v -> {
                    if (getActivity() != null) {
                        Intent intent = new Intent(getActivity(), ListProductActivity.class);
                        startActivity(intent);
                    }
                });
            }

            if (listProductView != null) {
                listProductView.setOnClickListener(v -> {
                    if (getActivity() != null) {
                        Intent intent = new Intent(getActivity(), ListProductActivity.class);
                        startActivity(intent);
                    }
                });
            }

            if (searchView != null) {
                searchView.setOnClickListener(v -> searchProduct());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadData() {
        try {
            loadBanner();
            loadCategories();
            loadPopularDrinks();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void searchProduct() {
        try {
            if (searchEditText != null && getActivity() != null) {
                String query = searchEditText.getText().toString().trim();
                if (!query.isEmpty()) {
                    Intent intent = new Intent(getActivity(), ListProductActivity.class);
                    intent.putExtra("search_query", query);
                    startActivity(intent);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadBanner() {
        try {
            if (progressBarBanner != null) {
                progressBarBanner.setVisibility(View.VISIBLE);
            }

            if (controller != null) {
                controller.loadBanner(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try {
                            if (snapshot.exists() && getActivity() != null && imgBanner != null && isAdded()) {
                                String bannerUrl = snapshot.getValue(String.class);
                                if (bannerUrl != null && !bannerUrl.isEmpty()) {
                                    Glide.with(HomeFragment.this)
                                            .load(bannerUrl)
                                            .into(imgBanner);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            if (progressBarBanner != null) {
                                progressBarBanner.setVisibility(View.GONE);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        if (progressBarBanner != null) {
                            progressBarBanner.setVisibility(View.GONE);
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (progressBarBanner != null) {
                progressBarBanner.setVisibility(View.GONE);
            }
        }
    }

    private void loadCategories() {
        try {
            if (progressBarCat != null) {
                progressBarCat.setVisibility(View.VISIBLE);
            }

            if (controller != null) {
                controller.loadCategories(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try {
                            List<Category> categories = new ArrayList<>();
                            for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                                Category category = childSnapshot.getValue(Category.class);
                                if (category != null) {
                                    categories.add(category);
                                }
                            }

                            if (getActivity() != null && recyclerViewCat != null && isAdded() && !categories.isEmpty()) {
                                recyclerViewCat.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
                                CategoryAdapter adapter = new CategoryAdapter(getActivity(), categories);
                                recyclerViewCat.setAdapter(adapter);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            if (progressBarCat != null) {
                                progressBarCat.setVisibility(View.GONE);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        if (progressBarCat != null) {
                            progressBarCat.setVisibility(View.GONE);
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (progressBarCat != null) {
                progressBarCat.setVisibility(View.GONE);
            }
        }
    }

    private void loadPopularDrinks() {
        try {
            if (progressBarDrink != null) {
                progressBarDrink.setVisibility(View.VISIBLE);
            }

            if (controller != null) {
                controller.loadPopularItems(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try {
                            List<Product> products = new ArrayList<>();
                            for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                                Product product = childSnapshot.getValue(Product.class);
                                if (product != null) {
                                    products.add(product);
                                }
                            }

                            if (getActivity() != null && recyclerViewDrinks != null && isAdded() && !products.isEmpty()) {
                                recyclerViewDrinks.setLayoutManager(new GridLayoutManager(getActivity(), 2));
                                PopularAdapter adapter = new PopularAdapter(getActivity(), products);
                                recyclerViewDrinks.setAdapter(adapter);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            if (progressBarDrink != null) {
                                progressBarDrink.setVisibility(View.GONE);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        if (progressBarDrink != null) {
                            progressBarDrink.setVisibility(View.GONE);
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (progressBarDrink != null) {
                progressBarDrink.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Clean up references to prevent memory leaks
        imgBanner = null;
        searchView = null;
        progressBarBanner = null;
        progressBarCat = null;
        progressBarDrink = null;
        recyclerViewCat = null;
        recyclerViewDrinks = null;
        listProductView = null;
        searchEditText = null;
        controller = null;
    }
}
