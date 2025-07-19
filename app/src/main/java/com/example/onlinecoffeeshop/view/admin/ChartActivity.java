package com.example.onlinecoffeeshop.view.admin;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.onlinecoffeeshop.R;
import com.example.onlinecoffeeshop.helper.DashboardChartHelper;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;

import java.util.Calendar;

public class ChartActivity extends AppCompatActivity {
    private BarChart revenueBarChart;
    private LineChart orderLineChart;
    private PieChart productPieChart;
    private Button fromDateBtn, toDateBtn;

    private long fromDate = 0, toDate = System.currentTimeMillis();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chart);

        initXML();

        fromDateBtn.setOnClickListener(v -> showDatePicker(true));
        toDateBtn.setOnClickListener(v -> showDatePicker(false));

        loadAllCharts(); // load lần đầu

    }

    public void initXML(){
        fromDateBtn = findViewById(R.id.fromDateBtn);
        toDateBtn = findViewById(R.id.toDateBtn);
        revenueBarChart = findViewById(R.id.revenueBarChart);
        orderLineChart = findViewById(R.id.orderLineChart);
        productPieChart = findViewById(R.id.productPieChart);
    }
    private void showDatePicker(boolean isFromDate) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth, 0, 0);
                    long selected = calendar.getTimeInMillis();
                    if (isFromDate) {
                        fromDate = selected;
                        fromDateBtn.setText("From: " + DateFormat.format("dd/MM/yyyy", selected));
                    } else {
                        toDate = selected;
                        toDateBtn.setText("To: " + DateFormat.format("dd/MM/yyyy", selected));
                    }
                    loadAllCharts();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }
    private void loadAllCharts() {
        DashboardChartHelper.loadRevenueChart(fromDate, toDate, revenueBarChart);
        DashboardChartHelper.loadOrderCountChart(fromDate, toDate, orderLineChart);
        DashboardChartHelper.loadBestSellingProducts(fromDate, toDate, productPieChart);
    }
}