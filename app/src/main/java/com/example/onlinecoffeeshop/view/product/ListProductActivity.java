package com.example.onlinecoffeeshop.view.product;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onlinecoffeeshop.R;
import com.example.onlinecoffeeshop.adapter.ProductAdapterWithViewTypes;
import com.example.onlinecoffeeshop.controller.ProductController;
import com.example.onlinecoffeeshop.model.Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ListProductActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private ProductController productController;
    private ProductAdapterWithViewTypes productAdapter;
    private LinearLayout backBtn;
    private LinearLayout emptyStateSearch;
    private TextView tvEmptySearchTitle, tvEmptySearchSubtitle;

    // Search and Filter UI components
    private EditText searchEditText;
    private ImageView filterBtn;
    private LinearLayout filterSection;
    private Button sortNameAscBtn, sortNameDescBtn, sortPriceAscBtn, sortPriceDescBtn,
                   sortRatingAscBtn, sortRatingDescBtn, clearFiltersBtn;

    // Layout and View UI components
    private ImageView listViewBtn, gridViewBtn;
    private Button itemsPerPage6Btn, itemsPerPage12Btn, itemsPerPage24Btn, itemsPerPage48Btn;

    // Pagination UI components
    private LinearLayout paginationSection;
    private Button prevPageBtn, nextPageBtn;
    private TextView currentPageTxt, productCountTxt, pageInfoTxt;

    // Data management
    private List<Product> allProducts = new ArrayList<>();
    private List<Product> filteredProducts = new ArrayList<>();
    private String currentSearchQuery = "";
    private String currentSortType = "none"; // none, name, price, rating
    private boolean isFilterVisible = false;
    private boolean isListView = true; // true for list view, false for grid view

    // Pagination variables
    private int itemsPerPage = 6;
    private int currentPage = 1;
    private int totalPages = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_list_product);

        initializeViews();
        setupListeners();
        initializeDefaultStates();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        productController = new ProductController();

        loadProducts();

        // Check if we should focus on search (coming from home screen quick action)
        handleSearchFocus();
    }

    private void initializeViews() {
        recyclerView = findViewById(R.id.cartView);
        progressBar = findViewById(R.id.progressBar3);
        backBtn = findViewById(R.id.back_btn);
        emptyStateSearch = findViewById(R.id.emptyStateSearch);
        tvEmptySearchTitle = findViewById(R.id.tv_empty_search_title);
        tvEmptySearchSubtitle = findViewById(R.id.tv_empty_search_subtitle);

        // Search and Filter components
        searchEditText = findViewById(R.id.searchEditText);
        filterBtn = findViewById(R.id.filterBtn);
        filterSection = findViewById(R.id.filterSection);
        sortNameAscBtn = findViewById(R.id.sortNameAscBtn);
        sortNameDescBtn = findViewById(R.id.sortNameDescBtn);
        sortPriceAscBtn = findViewById(R.id.sortPriceAscBtn);
        sortPriceDescBtn = findViewById(R.id.sortPriceDescBtn);
        sortRatingAscBtn = findViewById(R.id.sortRatingAscBtn);
        sortRatingDescBtn = findViewById(R.id.sortRatingDescBtn);
        clearFiltersBtn = findViewById(R.id.clearFiltersBtn);

        // Layout and View components
        listViewBtn = findViewById(R.id.listViewBtn);
        gridViewBtn = findViewById(R.id.gridViewBtn);
        itemsPerPage6Btn = findViewById(R.id.itemsPerPage6Btn);
        itemsPerPage12Btn = findViewById(R.id.itemsPerPage12Btn);
        itemsPerPage24Btn = findViewById(R.id.itemsPerPage24Btn);
        itemsPerPage48Btn = findViewById(R.id.itemsPerPage48Btn);

        // Pagination components
        paginationSection = findViewById(R.id.paginationSection);
        prevPageBtn = findViewById(R.id.prevPageBtn);
        nextPageBtn = findViewById(R.id.nextPageBtn);
        currentPageTxt = findViewById(R.id.currentPageTxt);
        productCountTxt = findViewById(R.id.productCountTxt);
        pageInfoTxt = findViewById(R.id.pageInfoTxt);
    }

    private void setupListeners() {
        backBtn.setOnClickListener(v -> finish());

        Button btnBrowseAll = findViewById(R.id.btn_browse_all);
        btnBrowseAll.setOnClickListener(v -> {
            clearSearchAndFilters();
            loadProducts();
        });

        // Search functionality
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentSearchQuery = s.toString().trim();
                applyFiltersAndPagination();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                currentSearchQuery = searchEditText.getText().toString().trim();
                applyFiltersAndPagination();
                return true;
            }
            return false;
        });

        // Filter toggle
        filterBtn.setOnClickListener(v -> toggleFilterSection());

        // Sort buttons
        sortNameAscBtn.setOnClickListener(v -> applySorting("name_asc"));
        sortNameDescBtn.setOnClickListener(v -> applySorting("name_desc"));
        sortPriceAscBtn.setOnClickListener(v -> applySorting("price_asc"));
        sortPriceDescBtn.setOnClickListener(v -> applySorting("price_desc"));
        sortRatingAscBtn.setOnClickListener(v -> applySorting("rating_asc"));
        sortRatingDescBtn.setOnClickListener(v -> applySorting("rating_desc"));
        clearFiltersBtn.setOnClickListener(v -> clearFilters());

        // Pagination
        prevPageBtn.setOnClickListener(v -> {
            if (currentPage > 1) {
                currentPage--;
                updateProductDisplay();
            }
        });

        nextPageBtn.setOnClickListener(v -> {
            if (currentPage < totalPages) {
                currentPage++;
                updateProductDisplay();
            }
        });

        // Layout and View options
        listViewBtn.setOnClickListener(v -> setLayoutView(true));
        gridViewBtn.setOnClickListener(v -> setLayoutView(false));

        itemsPerPage6Btn.setOnClickListener(v -> setItemsPerPage(6));
        itemsPerPage12Btn.setOnClickListener(v -> setItemsPerPage(12));
        itemsPerPage24Btn.setOnClickListener(v -> setItemsPerPage(24));
        itemsPerPage48Btn.setOnClickListener(v -> setItemsPerPage(48));
    }

    private void toggleFilterSection() {
        isFilterVisible = !isFilterVisible;
        filterSection.setVisibility(isFilterVisible ? View.VISIBLE : View.GONE);

        // Update filter button appearance
        filterBtn.setAlpha(isFilterVisible ? 1.0f : 0.7f);
    }

    private void applySorting(String sortType) {
        currentSortType = sortType;

        // Reset all button states
        resetSortButtonStates();

        // Set the active button to selected state
        switch (sortType) {
            case "name_asc":
                sortNameAscBtn.setSelected(true);
                break;
            case "name_desc":
                sortNameDescBtn.setSelected(true);
                break;
            case "price_asc":
                sortPriceAscBtn.setSelected(true);
                break;
            case "price_desc":
                sortPriceDescBtn.setSelected(true);
                break;
            case "rating_asc":
                sortRatingAscBtn.setSelected(true);
                break;
            case "rating_desc":
                sortRatingDescBtn.setSelected(true);
                break;
        }

        applyFiltersAndPagination();
    }

    private void resetSortButtonStates() {
        // Reset all buttons to unselected state
        sortNameAscBtn.setSelected(false);
        sortNameDescBtn.setSelected(false);
        sortPriceAscBtn.setSelected(false);
        sortPriceDescBtn.setSelected(false);
        sortRatingAscBtn.setSelected(false);
        sortRatingDescBtn.setSelected(false);
    }

    private void clearFilters() {
        currentSortType = "none";
        currentSearchQuery = "";
        searchEditText.setText("");
        resetSortButtonStates();
        applyFiltersAndPagination();
    }

    private void clearSearchAndFilters() {
        currentSearchQuery = "";
        currentSortType = "none";
        searchEditText.setText("");
        resetSortButtonStates();
        isFilterVisible = false;
        filterSection.setVisibility(View.GONE);
        filterBtn.setAlpha(0.7f);
    }

    private void loadProducts() {
        String categoryId = getIntent().getStringExtra("categoryId");

        progressBar.setVisibility(View.VISIBLE);

        productController.getAllProducts(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressBar.setVisibility(View.GONE);
                allProducts.clear();

                for (DataSnapshot item : snapshot.getChildren()) {
                    Product product = item.getValue(Product.class);
                    if (product == null) continue;

                    boolean matchCategory = categoryId == null || categoryId.equals(product.getCategoryId());
                    if (matchCategory) {
                        allProducts.add(product);
                    }
                }

                applyFiltersAndPagination();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ListProductActivity.this, "Lỗi tải sản phẩm", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void applyFiltersAndPagination() {
        // Filter products based on search query
        filteredProducts.clear();

        for (Product product : allProducts) {
            boolean matchSearch = currentSearchQuery.isEmpty() ||
                product.getTitle().toLowerCase().contains(currentSearchQuery.toLowerCase()) ||
                product.getDescription().toLowerCase().contains(currentSearchQuery.toLowerCase());

            if (matchSearch) {
                filteredProducts.add(product);
            }
        }

        // Apply sorting
        if (!currentSortType.equals("none")) {
            Collections.sort(filteredProducts, getComparator(currentSortType));
        }

        // Update pagination
        totalPages = Math.max(1, (int) Math.ceil((double) filteredProducts.size() / itemsPerPage));
        currentPage = Math.min(currentPage, totalPages);

        updateProductDisplay();
        updateInfoTexts();
    }

    private Comparator<Product> getComparator(String sortType) {
        switch (sortType) {
            case "name_asc":
                return (p1, p2) -> p1.getTitle().compareToIgnoreCase(p2.getTitle());
            case "name_desc":
                return (p1, p2) -> p2.getTitle().compareToIgnoreCase(p1.getTitle());
            case "price_asc":
                return (p1, p2) -> Double.compare(p1.getPrice(), p2.getPrice());
            case "price_desc":
                return (p1, p2) -> Double.compare(p2.getPrice(), p1.getPrice());
            case "rating_asc":
                return (p1, p2) -> Double.compare(p1.getRating(), p2.getRating()); // Lowest first
            case "rating_desc":
                return (p1, p2) -> Double.compare(p2.getRating(), p1.getRating()); // Highest first
            default:
                return null;
        }
    }

    private void updateProductDisplay() {
        // Calculate start and end indices for current page
        int startIndex = (currentPage - 1) * itemsPerPage;
        int endIndex = Math.min(startIndex + itemsPerPage, filteredProducts.size());

        // Get products for current page
        List<Product> currentPageProducts = new ArrayList<>();
        if (startIndex < filteredProducts.size()) {
            currentPageProducts = filteredProducts.subList(startIndex, endIndex);
        }

        // Initialize adapter if it doesn't exist
        if (productAdapter == null) {
            productAdapter = new ProductAdapterWithViewTypes(this, currentPageProducts);
            productAdapter.setViewType(isListView ? ProductAdapterWithViewTypes.VIEW_TYPE_LIST : ProductAdapterWithViewTypes.VIEW_TYPE_GRID);
            recyclerView.setAdapter(productAdapter);
        } else {
            // Update existing adapter
            productAdapter = new ProductAdapterWithViewTypes(this, currentPageProducts);
            productAdapter.setViewType(isListView ? ProductAdapterWithViewTypes.VIEW_TYPE_LIST : ProductAdapterWithViewTypes.VIEW_TYPE_GRID);
            recyclerView.setAdapter(productAdapter);
        }

        // Update pagination controls
        updatePaginationControls();

        // Show/hide empty state
        if (filteredProducts.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            if (emptyStateSearch != null) {
                emptyStateSearch.setVisibility(View.VISIBLE);
            }
            if (paginationSection != null) {
                paginationSection.setVisibility(View.GONE);
            }

            if (!currentSearchQuery.isEmpty()) {
                if (tvEmptySearchTitle != null) {
                    tvEmptySearchTitle.setText("Không tìm thấy \"" + currentSearchQuery + "\"");
                }
                if (tvEmptySearchSubtitle != null) {
                    tvEmptySearchSubtitle.setText("Thử tìm kiếm với từ khóa khác");
                }
            } else {
                if (tvEmptySearchTitle != null) {
                    tvEmptySearchTitle.setText("Không có sản phẩm");
                }
                if (tvEmptySearchSubtitle != null) {
                    tvEmptySearchSubtitle.setText("Danh mục này chưa có sản phẩm nào");
                }
            }
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            if (emptyStateSearch != null) {
                emptyStateSearch.setVisibility(View.GONE);
            }
            if (paginationSection != null) {
                paginationSection.setVisibility(totalPages > 1 ? View.VISIBLE : View.GONE);
            }
        }
    }

    private void updatePaginationControls() {
        currentPageTxt.setText(String.valueOf(currentPage));

        prevPageBtn.setEnabled(currentPage > 1);
        nextPageBtn.setEnabled(currentPage < totalPages);

        prevPageBtn.setAlpha(currentPage > 1 ? 1.0f : 0.5f);
        nextPageBtn.setAlpha(currentPage < totalPages ? 1.0f : 0.5f);
    }

    private void updateInfoTexts() {
        // Update product count
        int startItem = filteredProducts.isEmpty() ? 0 : (currentPage - 1) * itemsPerPage + 1;
        int endItem = Math.min(currentPage * itemsPerPage, filteredProducts.size());

        if (filteredProducts.isEmpty()) {
            productCountTxt.setText("Showing 0 products");
        } else {
            productCountTxt.setText(String.format("Showing %d-%d of %d products",
                startItem, endItem, filteredProducts.size()));
        }

        // Update page info
        pageInfoTxt.setText(String.format("Page %d of %d", currentPage, totalPages));
    }

    private void setLayoutView(boolean isListView) {
        this.isListView = isListView;
        if (isListView) {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            listViewBtn.setAlpha(1.0f);
            gridViewBtn.setAlpha(0.5f);
            listViewBtn.setSelected(true);
            gridViewBtn.setSelected(false);
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
            listViewBtn.setAlpha(0.5f);
            gridViewBtn.setAlpha(1.0f);
            listViewBtn.setSelected(false);
            gridViewBtn.setSelected(true);
        }

        // Update adapter with new view type
        if (productAdapter != null) {
            productAdapter.setViewType(isListView ? ProductAdapterWithViewTypes.VIEW_TYPE_LIST : ProductAdapterWithViewTypes.VIEW_TYPE_GRID);
        }
        updateProductDisplay();
    }

    private void setItemsPerPage(int itemsPerPage) {
        this.itemsPerPage = itemsPerPage;
        totalPages = (int) Math.ceil((double) filteredProducts.size() / this.itemsPerPage);
        currentPage = 1; // Reset to first page
        applyFiltersAndPagination();
    }

    private void initializeDefaultStates() {
        // Set default sorting to none
        currentSortType = "none";

        // Clear search query
        currentSearchQuery = "";

        // Hide filter section by default
        isFilterVisible = false;
        filterSection.setVisibility(View.GONE);
        filterBtn.setAlpha(0.7f);

        // Set default items per page
        itemsPerPage = 6;
        totalPages = (int) Math.ceil((double) filteredProducts.size() / itemsPerPage);
        currentPage = 1;

        // Set default layout to list view
        isListView = true;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void handleSearchFocus() {
        boolean shouldFocusSearch = getIntent().getBooleanExtra("focusSearch", false);
        if (shouldFocusSearch) {
            searchEditText.requestFocus();
            // Show keyboard
            searchEditText.postDelayed(() -> {
                android.view.inputmethod.InputMethodManager imm =
                    (android.view.inputmethod.InputMethodManager) getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.showSoftInput(searchEditText, android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT);
                }
            }, 100);
        }
    }
}
