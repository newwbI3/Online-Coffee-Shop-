package com.example.onlinecoffeeshop.helper;

import android.graphics.Color;
import android.util.Log;

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
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.*;

public class DashboardChartHelper {

    private static String getMonthKey(long timestamp) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestamp);
        SimpleDateFormat sdf = new SimpleDateFormat("MM-yyyy", Locale.getDefault());
        return sdf.format(cal.getTime());
    }

    public static void loadRevenueChart(long fromDate, long toDate, BarChart chart) {
        FirebaseDatabase.getInstance().getReference("Orders")
                .orderByChild("timestamp")
                .startAt(fromDate)
                .endAt(toDate)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        Map<String, Float> revenueByMonth = new TreeMap<>();

                        for (DataSnapshot orderSnap : snapshot.getChildren()) {
                            Long timestamp = orderSnap.child("timestamp").getValue(Long.class);
                            Double total = orderSnap.child("total").getValue(Double.class);
                            if (timestamp != null && total != null) {
                                String monthKey = getMonthKey(timestamp);
                                revenueByMonth.put(monthKey,
                                        revenueByMonth.getOrDefault(monthKey, 0f) + total.floatValue());
                            }
                        }

                        List<BarEntry> entries = new ArrayList<>();
                        List<String> labels = new ArrayList<>();
                        int index = 0;
                        for (Map.Entry<String, Float> entry : revenueByMonth.entrySet()) {
                            entries.add(new BarEntry(index, entry.getValue()));
                            labels.add(entry.getKey());
                            index++;
                        }

                        BarDataSet dataSet = new BarDataSet(entries, "Doanh thu");
                        dataSet.setColors(Color.rgb(60, 220, 78));
                        dataSet.setValueTextColor(Color.BLACK);
                        dataSet.setValueTextSize(12f);

                        BarData data = new BarData(dataSet);
                        chart.setData(data);
                        chart.getDescription().setText("Doanh thu theo tháng");

                        XAxis xAxis = chart.getXAxis();
                        xAxis.setValueFormatter(new ValueFormatter() {
                            @Override
                            public String getFormattedValue(float value) {
                                int i = Math.round(value);
                                return i >= 0 && i < labels.size() ? labels.get(i) : "";
                            }
                        });
                        xAxis.setGranularity(1f);
                        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                        xAxis.setTextColor(Color.BLACK);
                        xAxis.setTextSize(12f);

                        chart.invalidate();
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Log.e("Chart", "Failed to load revenue chart", error.toException());
                    }
                });
    }

    public static void loadOrderCountChart(long fromDate, long toDate, LineChart chart) {
        FirebaseDatabase.getInstance().getReference("Orders")
                .orderByChild("timestamp")
                .startAt(fromDate)
                .endAt(toDate)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        Map<String, Integer> countByMonth = new TreeMap<>();

                        for (DataSnapshot orderSnap : snapshot.getChildren()) {
                            Long timestamp = orderSnap.child("timestamp").getValue(Long.class);
                            if (timestamp != null) {
                                String monthKey = getMonthKey(timestamp);
                                countByMonth.put(monthKey,
                                        countByMonth.getOrDefault(monthKey, 0) + 1);
                            }
                        }

                        List<Entry> entries = new ArrayList<>();
                        List<String> labels = new ArrayList<>();
                        int index = 0;
                        for (Map.Entry<String, Integer> entry : countByMonth.entrySet()) {
                            entries.add(new Entry(index, entry.getValue()));
                            labels.add(entry.getKey());
                            index++;
                        }

                        LineDataSet dataSet = new LineDataSet(entries, "Số đơn hàng");
                        dataSet.setColor(Color.BLUE);
                        dataSet.setCircleColor(Color.BLACK);
                        dataSet.setValueTextColor(Color.BLACK);
                        dataSet.setValueTextSize(12f);

                        LineData lineData = new LineData(dataSet);
                        chart.setData(lineData);
                        chart.getDescription().setText("Số đơn hàng theo tháng");

                        XAxis xAxis = chart.getXAxis();
                        xAxis.setValueFormatter(new ValueFormatter() {
                            @Override
                            public String getFormattedValue(float value) {
                                int i = Math.round(value);
                                return i >= 0 && i < labels.size() ? labels.get(i) : "";
                            }
                        });
                        xAxis.setGranularity(1f);
                        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                        xAxis.setTextColor(Color.BLACK);
                        xAxis.setTextSize(12f);

                        chart.invalidate();
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Log.e("Chart", "Failed to load order count chart", error.toException());
                    }
                });
    }

    public static void loadBestSellingProducts(long fromDate, long toDate, PieChart chart) {
        FirebaseDatabase.getInstance().getReference("Orders")
                .orderByChild("timestamp")
                .startAt(fromDate)
                .endAt(toDate)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        Map<String, Integer> productCounts = new HashMap<>();

                        for (DataSnapshot orderSnap : snapshot.getChildren()) {
                            for (DataSnapshot itemSnap : orderSnap.child("items").getChildren()) {
                                String title = itemSnap.child("title").getValue(String.class);
                                Integer quantity = itemSnap.child("quantity").getValue(Integer.class);
                                if (title != null && quantity != null) {
                                    productCounts.put(title,
                                            productCounts.getOrDefault(title, 0) + quantity);
                                }
                            }
                        }

                        List<PieEntry> entries = new ArrayList<>();
                        for (Map.Entry<String, Integer> entry : productCounts.entrySet()) {
                            entries.add(new PieEntry(entry.getValue(), entry.getKey()));
                        }

                        PieDataSet dataSet = new PieDataSet(entries, "Sản phẩm bán chạy");
                        dataSet.setColors(Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.MAGENTA);
                        dataSet.setValueTextSize(12f);
                        dataSet.setValueTextColor(Color.WHITE);

                        PieData data = new PieData(dataSet);
                        chart.setData(data);
                        chart.getDescription().setText("Top sản phẩm bán chạy");
                        chart.invalidate();
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Log.e("Chart", "Failed to load pie chart", error.toException());
                    }
                });
    }
}
