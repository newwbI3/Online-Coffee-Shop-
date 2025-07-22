package com.example.onlinecoffeeshop.view.order;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AutoCompleteTextView;
import android.widget.Filter;
import android.text.Editable;
import android.text.TextWatcher;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.onlinecoffeeshop.MainActivity;
import com.example.onlinecoffeeshop.R;
import com.example.onlinecoffeeshop.controller.OrderController;
import com.example.onlinecoffeeshop.model.CartItem;
import com.example.onlinecoffeeshop.model.Order;
import com.example.onlinecoffeeshop.model.OrderStatusUpdate;
import com.example.onlinecoffeeshop.model.Province;
import com.example.onlinecoffeeshop.model.ProvinceResponse;
import com.example.onlinecoffeeshop.model.Commune;
import com.example.onlinecoffeeshop.model.CommuneResponse;
import com.example.onlinecoffeeshop.model.User;
import com.example.onlinecoffeeshop.service.AddressApiService;
import com.example.onlinecoffeeshop.util.ApiClient;
import com.example.onlinecoffeeshop.view.auth.LoginActivity;
import com.example.onlinecoffeeshop.view.product.ListProductActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckoutActivity extends AppCompatActivity {
    private EditText fullNameEdt, phoneEdt, emailEdt, detailAddressEdt, noteEdt;
    private AutoCompleteTextView provinceSearchView, wardSearchView;
    private RadioGroup deliveryMethodGroup, paymentMethodGroup;
    private TextView totalTxt;
    private Button btnPlaceOrder;

    private List<CartItem> cartItems;
    private double totalAmount = 0;
    private double originalAmount = 0; // Tiền hàng gốc không bao gồm phí ship
    private static final double DELIVERY_FEE = 2.0; // Phí giao hàng $2

    private FirebaseAuth mAuth;
    private String userId;
    private DatabaseReference cartRef;

    // Address data
    private List<Province> provinceList = new ArrayList<>();
    private List<Commune> communeList = new ArrayList<>();
    private Province selectedProvince = null;
    private Commune selectedCommune = null;

    // API Service
    private AddressApiService addressApiService;

    // Vietnamese phone number regex
    private static final String PHONE_PATTERN = "(84|0[3|5|7|8|9])+([0-9]{8})\\b";
    private static final Pattern phonePattern = Pattern.compile(PHONE_PATTERN);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(this, "Vui lòng đăng nhập để tiếp tục", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        userId = mAuth.getCurrentUser().getUid();
        cartRef = FirebaseDatabase.getInstance().getReference("Cart").child(userId);

        initViews();
        setupValidation();
        loadCartItems();

        btnPlaceOrder.setOnClickListener(v -> {
            if (validateAllInputs()) {
                placeOrder();
            }
        });
    }

    private void initViews() {
        fullNameEdt = findViewById(R.id.fullName);
        phoneEdt = findViewById(R.id.phone);
        emailEdt = findViewById(R.id.email);
        detailAddressEdt = findViewById(R.id.detailAddress);
        noteEdt = findViewById(R.id.note);
        provinceSearchView = findViewById(R.id.provinceSearchView);
        wardSearchView = findViewById(R.id.wardSearchView);
        deliveryMethodGroup = findViewById(R.id.deliveryMethod);
        paymentMethodGroup = findViewById(R.id.paymentMethod);
        totalTxt = findViewById(R.id.totalTxt);
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder);

        // Initialize API service
        addressApiService = ApiClient.getAddressApiService();

        // Load provinces from API
        loadProvincesFromAPI();

        // Setup delivery method change listener
        setupDeliveryMethodListener();
    }

    private void setupDeliveryMethodListener() {
        deliveryMethodGroup.setOnCheckedChangeListener((group, checkedId) -> {
            updateTotalWithDeliveryFee();
            updateAddressFieldsVisibility(checkedId);
        });
    }

    private void updateAddressFieldsVisibility(int selectedDeliveryId) {
        boolean isHomeDelivery = selectedDeliveryId == R.id.homeDelivery;

        // Show/hide address fields based on delivery method
        provinceSearchView.setEnabled(isHomeDelivery);
        wardSearchView.setEnabled(isHomeDelivery && selectedProvince != null);
        detailAddressEdt.setEnabled(isHomeDelivery);

        if (!isHomeDelivery) {
            // Clear address validation errors when pickup is selected
            provinceSearchView.setError(null);
            wardSearchView.setError(null);
            detailAddressEdt.setError(null);
        }
    }

    private void updateTotalWithDeliveryFee() {
        int selectedDeliveryId = deliveryMethodGroup.getCheckedRadioButtonId();
        boolean isHomeDelivery = selectedDeliveryId == R.id.homeDelivery;

        if (isHomeDelivery) {
            totalAmount = originalAmount + DELIVERY_FEE;
            totalTxt.setText(String.format("Tổng: %.2f$ (bao gồm phí giao hàng %.2f$)", totalAmount, DELIVERY_FEE));
        } else {
            totalAmount = originalAmount;
            totalTxt.setText(String.format("Tổng: %.2f$", totalAmount));
        }
    }

    private void setupValidation() {
        // Real-time validation for phone number
        phoneEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                validatePhoneNumber();
            }
        });

        // Real-time validation for email
        emailEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                validateEmail();
            }
        });

        // Real-time validation for full name
        fullNameEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                validateFullName();
            }
        });
    }

    private boolean validateFullName() {
        String fullName = fullNameEdt.getText().toString().trim();
        if (TextUtils.isEmpty(fullName)) {
            fullNameEdt.setError("Vui lòng nhập họ và tên");
            return false;
        } else if (fullName.length() < 2) {
            fullNameEdt.setError("Họ và tên phải có ít nhất 2 ký tự");
            return false;
        } else if (!fullName.matches("[a-zA-ZÀ-ỹ\\s]+")) {
            fullNameEdt.setError("Họ và tên chỉ được chứa chữ cái và khoảng trắng");
            return false;
        } else {
            fullNameEdt.setError(null);
            return true;
        }
    }

    private boolean validatePhoneNumber() {
        String phone = phoneEdt.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            phoneEdt.setError("Vui lòng nhập số điện thoại");
            return false;
        } else if (!phonePattern.matcher(phone).matches()) {
            phoneEdt.setError("Số điện thoại không hợp lệ (VD: 0912345678 hoặc 84912345678)");
            return false;
        } else {
            phoneEdt.setError(null);
            return true;
        }
    }

    private boolean validateEmail() {
        String email = emailEdt.getText().toString().trim();
        // Email is optional, so only validate if not empty
        if (!TextUtils.isEmpty(email)) {
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailEdt.setError("Email không hợp lệ");
                return false;
            } else {
                emailEdt.setError(null);
                return true;
            }
        } else {
            emailEdt.setError(null);
            return true; // Email is optional
        }
    }

    private boolean validateAddress() {
        // Only validate address if home delivery is selected
        int selectedDeliveryId = deliveryMethodGroup.getCheckedRadioButtonId();
        boolean isHomeDelivery = selectedDeliveryId == R.id.homeDelivery;

        if (!isHomeDelivery) {
            return true; // Skip address validation for pickup
        }

        boolean isValid = true;

        // Validate province selection
        if (selectedProvince == null) {
            provinceSearchView.setError("Vui lòng chọn tỉnh/thành phố");
            isValid = false;
        } else {
            provinceSearchView.setError(null);
        }

        // Validate ward selection
        if (selectedCommune == null) {
            wardSearchView.setError("Vui lòng chọn phường/xã");
            isValid = false;
        } else {
            wardSearchView.setError(null);
        }

        // Validate detail address
        String detailAddress = detailAddressEdt.getText().toString().trim();
        if (TextUtils.isEmpty(detailAddress)) {
            detailAddressEdt.setError("Vui lòng nhập số nhà, tên đường");
            isValid = false;
        } else if (detailAddress.length() < 5) {
            detailAddressEdt.setError("Địa chỉ chi tiết phải có ít nhất 5 ký tự");
            isValid = false;
        } else {
            detailAddressEdt.setError(null);
        }

        return isValid;
    }

    private boolean validateDeliveryMethod() {
        int selectedDeliveryId = deliveryMethodGroup.getCheckedRadioButtonId();
        if (selectedDeliveryId == -1) {
            Toast.makeText(this, "Vui lòng chọn phương thức giao hàng", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean validatePaymentMethod() {
        int selectedPaymentId = paymentMethodGroup.getCheckedRadioButtonId();
        if (selectedPaymentId == -1) {
            Toast.makeText(this, "Vui lòng chọn phương thức thanh toán", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean validateAllInputs() {
        boolean isFullNameValid = validateFullName();
        boolean isPhoneValid = validatePhoneNumber();
        boolean isEmailValid = validateEmail();
        boolean isAddressValid = validateAddress();
        boolean isDeliveryValid = validateDeliveryMethod();
        boolean isPaymentValid = validatePaymentMethod();

        return isFullNameValid && isPhoneValid && isEmailValid &&
               isAddressValid && isDeliveryValid && isPaymentValid;
    }

    private void loadProvincesFromAPI() {
        Call<ProvinceResponse> call = addressApiService.getProvinces();
        call.enqueue(new Callback<ProvinceResponse>() {
            @Override
            public void onResponse(Call<ProvinceResponse> call, Response<ProvinceResponse> response) {
                if (response.isSuccessful() && response.body() != null &&
                    response.body().getProvinces() != null && !response.body().getProvinces().isEmpty()) {
                    provinceList = response.body().getProvinces();
                    setupProvinceSearchView();
                    Log.d("ADDRESS_API", "Successfully loaded " + provinceList.size() + " provinces");
                } else {
                    Log.e("ADDRESS_API", "Failed to load provinces. Response code: " + response.code());
                    loadFallbackProvinces();
                }
            }

            @Override
            public void onFailure(Call<ProvinceResponse> call, Throwable t) {
                Log.e("ADDRESS_API", "Error loading provinces: " + t.getMessage(), t);
                loadFallbackProvinces();
            }
        });
    }

    private void loadFallbackProvinces() {
        // Fallback data for major Vietnamese provinces/cities
        provinceList = new ArrayList<>();
        provinceList.add(new Province("01", "Hà Nội", "Thành phố Trung ương"));
        provinceList.add(new Province("79", "Thành phố Hồ Chí Minh", "Thành phố Trung ương"));
        provinceList.add(new Province("48", "Đà Nẵng", "Thành phố Trung ương"));
        provinceList.add(new Province("92", "Cần Thơ", "Thành phố Trung ương"));
        provinceList.add(new Province("31", "Hải Phòng", "Thành phố Trung ương"));
        provinceList.add(new Province("26", "Vĩnh Phúc", "Tỉnh"));
        provinceList.add(new Province("27", "Bắc Ninh", "Tỉnh"));
        provinceList.add(new Province("30", "Hải Dương", "Tỉnh"));
        provinceList.add(new Province("33", "Hưng Yên", "Tỉnh"));
        provinceList.add(new Province("35", "Thái Bình", "Tỉnh"));

        setupProvinceSearchView();
        Toast.makeText(this, "Đã tải dữ liệu tỉnh/thành phố cơ bản", Toast.LENGTH_SHORT).show();
    }

    // Helper method to remove Vietnamese accents
    private String removeAccents(String input) {
        if (input == null) return "";
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{M}", "").toLowerCase();
    }

    // Custom ArrayAdapter for Vietnamese search
    private class VietnameseSearchAdapter extends ArrayAdapter<String> {
        private List<String> originalItems;
        private List<String> filteredItems;

        public VietnameseSearchAdapter(Context context, List<String> items) {
            super(context, android.R.layout.simple_dropdown_item_1line, items);
            this.originalItems = new ArrayList<>(items);
            this.filteredItems = new ArrayList<>(items);
        }

        @Override
        public int getCount() {
            return filteredItems.size();
        }

        @Override
        public String getItem(int position) {
            return filteredItems.get(position);
        }

        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults results = new FilterResults();
                    List<String> suggestions = new ArrayList<>();

                    if (constraint != null) {
                        String filterPattern = removeAccents(constraint.toString().trim());

                        for (String item : originalItems) {
                            String normalizedItem = removeAccents(item);
                            // Check if the normalized item contains the normalized search pattern
                            if (normalizedItem.contains(filterPattern)) {
                                suggestions.add(item);
                            }
                        }
                    } else {
                        suggestions.addAll(originalItems);
                    }

                    results.values = suggestions;
                    results.count = suggestions.size();
                    return results;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    filteredItems.clear();
                    if (results != null && results.count > 0) {
                        @SuppressWarnings("unchecked")
                        List<String> suggestions = (List<String>) results.values;
                        filteredItems.addAll(suggestions);
                    }
                    notifyDataSetChanged();
                }
            };
        }
    }

    private void setupProvinceSearchView() {
        List<String> provinceNames = new ArrayList<>();

        // Filter out any invalid entries and ensure we only add valid province names
        for (Province province : provinceList) {
            String provinceName = province.getName();
            if (provinceName != null && !provinceName.trim().isEmpty() &&
                !provinceName.toLowerCase().contains("số lượng") &&
                !provinceName.toLowerCase().contains("count") &&
                !provinceName.matches(".*\\d+.*lượng.*") &&
                !provinceName.matches(".*số lượng.*")) {
                provinceNames.add(provinceName.trim());
            }
        }

        // Setup AutoCompleteTextView with Vietnamese search functionality
        VietnameseSearchAdapter searchAdapter = new VietnameseSearchAdapter(this, provinceNames);
        provinceSearchView.setAdapter(searchAdapter);
        provinceSearchView.setThreshold(1); // Start filtering after 1 character

        // Handle province selection from search
        provinceSearchView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedProvinceName = (String) parent.getItemAtPosition(position);
            // Find the corresponding Province object
            for (Province province : provinceList) {
                if (province.getName().equals(selectedProvinceName)) {
                    selectedProvince = province;
                    loadCommunesFromAPI(selectedProvince.getCode());
                    provinceSearchView.setError(null);
                    Log.d("PROVINCE_SEARCH", "Selected: " + selectedProvinceName + " (Code: " + province.getCode() + ")");
                    break;
                }
            }
        });

        // Handle manual text input validation
        provinceSearchView.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String enteredText = provinceSearchView.getText().toString().trim();
                if (!enteredText.isEmpty()) {
                    // Try to find exact match (both with and without accents)
                    Province matchedProvince = null;
                    String normalizedEnteredText = removeAccents(enteredText);

                    for (Province province : provinceList) {
                        String provinceName = province.getName();
                        String normalizedProvinceName = removeAccents(provinceName);

                        if (provinceName.equalsIgnoreCase(enteredText) ||
                            normalizedProvinceName.equals(normalizedEnteredText)) {
                            matchedProvince = province;
                            // Set the correct name with proper accents
                            provinceSearchView.setText(provinceName);
                            break;
                        }
                    }

                    if (matchedProvince != null) {
                        selectedProvince = matchedProvince;
                        loadCommunesFromAPI(selectedProvince.getCode());
                        provinceSearchView.setError(null);
                    } else {
                        // No exact match found, clear selection
                        selectedProvince = null;
                        clearWardSelection();
                        provinceSearchView.setError("Tỉnh/thành phố không hợp lệ");
                    }
                }
            }
        });
    }

    private void loadCommunesFromAPI(String provinceCode) {
        Call<CommuneResponse> call = addressApiService.getCommunes(provinceCode);
        call.enqueue(new Callback<CommuneResponse>() {
            @Override
            public void onResponse(Call<CommuneResponse> call, Response<CommuneResponse> response) {
                if (response.isSuccessful() && response.body() != null &&
                    response.body().getCommunes() != null && !response.body().getCommunes().isEmpty()) {
                    communeList = response.body().getCommunes();
                    setupWardSearchView();
                    Log.d("ADDRESS_API", "Successfully loaded " + communeList.size() + " communes for province " + provinceCode);
                } else {
                    Log.e("ADDRESS_API", "Failed to load communes. Response code: " + response.code());
                    loadFallbackCommunes(provinceCode);
                }
            }

            @Override
            public void onFailure(Call<CommuneResponse> call, Throwable t) {
                Log.e("ADDRESS_API", "Error loading communes: " + t.getMessage(), t);
                loadFallbackCommunes(provinceCode);
            }
        });
    }

    private void loadFallbackCommunes(String provinceCode) {
        communeList = new ArrayList<>();

        // Add fallback commune data based on province
        switch (provinceCode) {
            case "01": // Hà Nội
                communeList.add(new Commune("00004", "Phường Ba Đình", "Phường", "01", "Thành phố Hà Nội"));
                communeList.add(new Commune("00008", "Phường Ngọc Hà", "Phường", "01", "Thành phố Hà Nội"));
                communeList.add(new Commune("00010", "Phường Điện Biên", "Phường", "01", "Thành phố Hà Nội"));
                communeList.add(new Commune("00013", "Phường Đội Cấn", "Phường", "01", "Thành phố Hà Nội"));
                communeList.add(new Commune("00016", "Phường Liễu Giai", "Phường", "01", "Thành phố Hà Nội"));
                break;
            case "79": // TP.HCM
                communeList.add(new Commune("26734", "Phường Bến Nghé", "Phường", "79", "Thành phố Hồ Chí Minh"));
                communeList.add(new Commune("26737", "Phường Bến Thành", "Phường", "79", "Thành phố Hồ Chí Minh"));
                communeList.add(new Commune("26740", "Phường Nguyễn Thái Bình", "Phường", "79", "Thành phố Hồ Chí Minh"));
                communeList.add(new Commune("26743", "Phường Phạm Ngũ Lão", "Phường", "79", "Thành phố Hồ Chí Minh"));
                communeList.add(new Commune("26746", "Phường Cầu Ông Lãnh", "Phường", "79", "Thành phố Hồ Chí Minh"));
                break;
            default:
                // Generic fallback
                communeList.add(new Commune("000", "Phường Trung tâm", "Phường", provinceCode, selectedProvince.getName()));
                communeList.add(new Commune("001", "Xã Thị trấn", "Xã", provinceCode, selectedProvince.getName()));
                break;
        }

        setupWardSearchView();
        Toast.makeText(this, "Đã tải dữ liệu phường/xã cơ bản", Toast.LENGTH_SHORT).show();
    }

    // ...existing code until setupWardSearchView...

    private void setupWardSearchView() {
        List<String> communeNames = new ArrayList<>();

        // Only add actual commune names, no count or summary info
        for (Commune commune : communeList) {
            String communeName = commune.getName();
            if (communeName != null && !communeName.trim().isEmpty() &&
                !communeName.toLowerCase().contains("số lượng") &&
                !communeName.toLowerCase().contains("count")) {
                communeNames.add(communeName.trim());
            }
        }

        // Enable ward search view
        wardSearchView.setEnabled(true);

        // Setup AutoCompleteTextView with Vietnamese search functionality
        VietnameseSearchAdapter searchAdapter = new VietnameseSearchAdapter(this, communeNames);
        wardSearchView.setAdapter(searchAdapter);
        wardSearchView.setThreshold(1); // Start filtering after 1 character

        // Handle ward selection from search
        wardSearchView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedCommuneName = (String) parent.getItemAtPosition(position);
            // Find the corresponding Commune object
            for (Commune commune : communeList) {
                if (commune.getName().equals(selectedCommuneName)) {
                    selectedCommune = commune;
                    wardSearchView.setError(null);
                    Log.d("WARD_SEARCH", "Selected: " + selectedCommuneName + " (Code: " + commune.getCode() + ")");
                    break;
                }
            }
        });

        // Handle manual text input validation
        wardSearchView.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String enteredText = wardSearchView.getText().toString().trim();
                if (!enteredText.isEmpty()) {
                    // Try to find exact match (both with and without accents)
                    Commune matchedCommune = null;
                    String normalizedEnteredText = removeAccents(enteredText);

                    for (Commune commune : communeList) {
                        String communeName = commune.getName();
                        String normalizedCommuneName = removeAccents(communeName);

                        if (communeName.equalsIgnoreCase(enteredText) ||
                            normalizedCommuneName.equals(normalizedEnteredText)) {
                            matchedCommune = commune;
                            // Set the correct name with proper accents
                            wardSearchView.setText(communeName);
                            break;
                        }
                    }

                    if (matchedCommune != null) {
                        selectedCommune = matchedCommune;
                        wardSearchView.setError(null);
                    } else {
                        // No exact match found, clear selection
                        selectedCommune = null;
                        wardSearchView.setError("Phường/xã không hợp lệ");
                    }
                }
            }
        });
    }

    private void clearWardSelection() {
        wardSearchView.setText("");
        wardSearchView.setEnabled(false);
        selectedCommune = null;
        communeList.clear();
    }

    private void loadCartItems() {
        cartItems = new ArrayList<>();
        cartRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot itemSnap : snapshot.getChildren()) {
                    CartItem item = itemSnap.getValue(CartItem.class);
                    if (item != null) {
                        cartItems.add(item);
                    }
                }

                // Get original amount from Intent (without delivery fee)
                originalAmount = getIntent().getDoubleExtra("totalAmount", 0.0);
                updateTotalWithDeliveryFee(); // Calculate total with/without delivery fee
                Log.d("CHECKOUT_LOG", "Cart items loaded: " + cartItems.size());
                btnPlaceOrder.setEnabled(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CheckoutActivity.this, "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validateInputs() {
        String fullName = fullNameEdt.getText().toString().trim();
        String phone = phoneEdt.getText().toString().trim();
        String email = emailEdt.getText().toString().trim();

        // Clear all previous errors
        fullNameEdt.setError(null);
        phoneEdt.setError(null);
        emailEdt.setError(null);

        // Validate full name
        if (TextUtils.isEmpty(fullName)) {
            fullNameEdt.setError("Vui lòng nhập họ tên");
            fullNameEdt.requestFocus();
            return false;
        }

        if (fullName.length() < 2) {
            fullNameEdt.setError("Họ tên phải có ít nhất 2 ký tự");
            fullNameEdt.requestFocus();
            return false;
        }

        // Check if name contains numbers or special characters
        if (!fullName.matches("^[a-zA-ZÀ-ỹ\\s]+$")) {
            fullNameEdt.setError("Họ tên chỉ được chứa chữ cái và khoảng trắng");
            fullNameEdt.requestFocus();
            return false;
        }

        // Validate phone number using the provided regex
        if (TextUtils.isEmpty(phone)) {
            phoneEdt.setError("Vui lòng nhập số điện thoại");
            phoneEdt.requestFocus();
            return false;
        }

        // Fixed regex pattern as requested: /(84|0[3|5|7|8|9])+([0-9]{8})\b/g
        String phoneRegex = "^(84|0[35789])([0-9]{8})$";
        if (!phone.matches(phoneRegex)) {
            phoneEdt.setError("Số điện thoại không đúng định dạng.\nVD: 0901234567, 0356781234, 84901234567");
            phoneEdt.requestFocus();
            return false;
        }

        // Validate email if provided
        if (!TextUtils.isEmpty(email)) {
            String emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
            if (!email.matches(emailPattern)) {
                emailEdt.setError("Email không đúng định dạng");
                emailEdt.requestFocus();
                return false;
            }
        }

        // Check delivery method and validate address accordingly
        int selectedDeliveryId = deliveryMethodGroup.getCheckedRadioButtonId();
        boolean isHomeDelivery = selectedDeliveryId == R.id.homeDelivery;

        if (isHomeDelivery) {
            // Validate address components only for home delivery
            if (selectedProvince == null) {
                Toast.makeText(this, "Vui lòng chọn tỉnh/thành phố", Toast.LENGTH_SHORT).show();
                return false;
            }

            if (selectedCommune == null) {
                Toast.makeText(this, "Vui lòng chọn phường/xã", Toast.LENGTH_SHORT).show();
                return false;
            }

            // Validate detailed address
            String detailAddress = detailAddressEdt.getText().toString().trim();
            if (TextUtils.isEmpty(detailAddress)) {
                detailAddressEdt.setError("Vui lòng nhập số nhà, tên đường");
                detailAddressEdt.requestFocus();
                return false;
            }

            if (detailAddress.length() < 5) {
                detailAddressEdt.setError("Địa chỉ chi tiết phải có ít nhất 5 ký tự");
                detailAddressEdt.requestFocus();
                return false;
            }

            // Check for meaningful address content
            if (detailAddress.matches("^[0-9\\s]*$")) {
                detailAddressEdt.setError("Địa chỉ phải chứa tên đường, không chỉ số nhà");
                detailAddressEdt.requestFocus();
                return false;
            }
        }

        // Validate delivery method selection
        if (deliveryMethodGroup.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Vui lòng chọn phương thức giao hàng", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Validate payment method selection
        if (paymentMethodGroup.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Vui lòng chọn phương thức thanh toán", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void placeOrder() {
        String fullName = fullNameEdt.getText().toString().trim();
        String phone = phoneEdt.getText().toString().trim();
        String email = emailEdt.getText().toString().trim();
        String note = noteEdt.getText().toString().trim();
        List<OrderStatusUpdate> statusHistory = new ArrayList<>();
        statusHistory.add(new OrderStatusUpdate("processing", System.currentTimeMillis()));

        // Handle address based on delivery method
        String completeAddress;
        int selectedDeliveryId = deliveryMethodGroup.getCheckedRadioButtonId();
        boolean isHomeDelivery = selectedDeliveryId == R.id.homeDelivery;

        if (isHomeDelivery) {
            String detailAddress = detailAddressEdt.getText().toString().trim();
            String provinceName = selectedProvince != null ? selectedProvince.getName() : "";
            String wardName = selectedCommune != null ? selectedCommune.getName() : "";
            completeAddress = detailAddress + ", " + wardName + ", " + provinceName;
        } else {
            completeAddress = "Nhận tại cửa hàng"; // Default address for pickup
        }

        String deliveryMethod = ((RadioButton) findViewById(deliveryMethodGroup.getCheckedRadioButtonId())).getText().toString();
        String paymentMethod = ((RadioButton) findViewById(paymentMethodGroup.getCheckedRadioButtonId())).getText().toString();
        String orderId = UUID.randomUUID().toString();
        long timestamp = System.currentTimeMillis();

        // Log order information
        Log.d("CHECKOUT_LOG", "FullName: " + fullName);
        Log.d("CHECKOUT_LOG", "Phone: " + phone);
        Log.d("CHECKOUT_LOG", "Email: " + email);
        Log.d("CHECKOUT_LOG", "Complete Address: " + completeAddress);
        Log.d("CHECKOUT_LOG", "IsHomeDelivery: " + isHomeDelivery);
        Log.d("CHECKOUT_LOG", "DeliveryMethod: " + deliveryMethod);
        Log.d("CHECKOUT_LOG", "PaymentMethod: " + paymentMethod);
        Log.d("CHECKOUT_LOG", "Note: " + note);
        Log.d("CHECKOUT_LOG", "TotalAmount: " + totalAmount);
        Log.d("CHECKOUT_LOG", "OriginalAmount: " + originalAmount);
        Log.d("CHECKOUT_LOG", "DeliveryFee: " + (isHomeDelivery ? DELIVERY_FEE : 0));

        // Create User object for the order
        User user = new User(userId, fullName, phone, email, completeAddress);

        // Create order using the correct constructor
        Order order = new Order(
                orderId,
                user,
                note,
                deliveryMethod,
                paymentMethod,
                cartItems,
                totalAmount,
                timestamp,
                "Đang xử lý",
                statusHistory // ✅ Add this
        );
        OrderController orderController = new OrderController();
        orderController.createOrder(order, new OrderController.OnOrderCreatedListener() {
            @Override
            public void onSuccess() {
                cartRef.removeValue();
                Toast.makeText(CheckoutActivity.this, "Đặt hàng thành công!", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(CheckoutActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(String message) {
                Toast.makeText(CheckoutActivity.this, "Lỗi: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void clearCart() {
        cartRef.removeValue()
            .addOnSuccessListener(aVoid -> {
                // Cart cleared successfully
                Log.d("CART", "Cart cleared for user " + userId);
                // Navigate to order confirmation screen
                startActivity(new Intent(CheckoutActivity.this, MainActivity.class));
                finish();
            })
            .addOnFailureListener(e -> {
                // Failed to clear cart
                Toast.makeText(CheckoutActivity.this, "Lỗi khi xóa giỏ hàng. Vui lòng kiểm tra lại.", Toast.LENGTH_SHORT).show();
                Log.e("CART_ERROR", "Failed to clear cart", e);
            });
    }
}
