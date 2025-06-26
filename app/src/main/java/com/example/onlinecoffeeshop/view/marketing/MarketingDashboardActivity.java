package com.example.onlinecoffeeshop.view.marketing;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.onlinecoffeeshop.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MarketingDashboardActivity extends AppCompatActivity {
    private BarChart barChartTopProducts;
    private LineChart lineChartRevenue;
    private PieChart pieChartCategory;
    private TextView tvTotalRevenue;
    private DatabaseReference analyticsRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_marketing_dashboard);

        tvTotalRevenue = findViewById(R.id.tvTotalRevenue);
        barChartTopProducts = findViewById(R.id.barChartTopProducts);
        lineChartRevenue = findViewById(R.id.lineChartRevenue);
        pieChartCategory = findViewById(R.id.pieChartCategory);

        analyticsRef = FirebaseDatabase.getInstance().getReference();

        loadTopProducts();
        loadMonthlyRevenue();
        loadPopularCategories();
    }
    private void loadTopProducts() {
        analyticsRef.child("top_products").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<BarEntry> entries = new ArrayList<>();
                List<String> labels = new ArrayList<>();
                int i = 0;
                for (DataSnapshot child : snapshot.getChildren()) {
                    String name = child.child("name").getValue(String.class);
                    Long sold = child.child("sold").getValue(Long.class);
                    entries.add(new BarEntry(i, sold != null ? sold : 0));
                    labels.add(name != null ? name : "Unknown");
                    i++;
                }

                BarDataSet dataSet = new BarDataSet(entries, "Top Sold");
                dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
                BarData barData = new BarData(dataSet);
                barChartTopProducts.setData(barData);
                barChartTopProducts.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
                barChartTopProducts.getXAxis().setGranularity(1f);
                barChartTopProducts.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
                barChartTopProducts.invalidate();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void loadMonthlyRevenue() {
        analyticsRef.child("monthly_revenue").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Entry> entries = new ArrayList<>();
                List<String> labels = new ArrayList<>();
                float totalThisMonth = 0;

                int i = 0;
                for (DataSnapshot child : snapshot.getChildren()) {
                    String month = child.getKey();
                    Double revenue = child.getValue(Double.class);
                    if (month != null && revenue != null) {
                        entries.add(new Entry(i, revenue.floatValue()));
                        labels.add(month);

                        if (month.equals(getCurrentMonth())) {
                            totalThisMonth = revenue.floatValue();
                        }
                        i++;
                    }
                }

                tvTotalRevenue.setText("Tổng doanh thu tháng này: $" + totalThisMonth);

                LineDataSet dataSet = new LineDataSet(entries, "Monthly Revenue");
                dataSet.setCircleColors(ColorTemplate.MATERIAL_COLORS);
                LineData lineData = new LineData(dataSet);
                lineChartRevenue.setData(lineData);
                lineChartRevenue.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
                lineChartRevenue.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
                lineChartRevenue.getXAxis().setGranularity(1f);
                lineChartRevenue.invalidate();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
    private void loadPopularCategories() {
        analyticsRef.child("popular_categories").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<PieEntry> entries = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    String category = child.getKey();
                    Long value = child.getValue(Long.class);
                    if (category != null && value != null) {
                        entries.add(new PieEntry(value, category));
                    }
                }

                PieDataSet dataSet = new PieDataSet(entries, "Categories");
                dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                PieData pieData = new PieData(dataSet);
                pieChartCategory.setData(pieData);
                pieChartCategory.invalidate();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
    private String getCurrentMonth() {
        return new SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(new Date());
    }
}