package com.example.tradeswift.main.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.tradeswift.R;
import com.example.tradeswift.main.models.Stock;
import com.example.tradeswift.main.models.StockDataHolder;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;
import java.util.List;

public class ChartFragment extends Fragment {
    private static final String STOCK_SYMBOL = "param1";
    private String mStockSymbol;
    private Stock mStock;
    private ArrayList<Float> mStockClosePrices;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mStockSymbol = getArguments().getString(STOCK_SYMBOL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chart, container, false);
        TextView companyName = view.findViewById(R.id.companyName);
        TextView stockSymbol = view.findViewById(R.id.stockSymbol);
        TextView currentPrice = view.findViewById(R.id.currentPrice);

        for (Stock stock : StockDataHolder.stocks) {
            if (stock.getSymbol().equals(mStockSymbol)) {
                mStock = stock;
                break;
            }
        }

        companyName.setText(mStock.getName());
        stockSymbol.setText(mStock.getSymbol());
        currentPrice.setText(String.format("$%.2f", mStock.getCurrentPrice()));
        mStockClosePrices = mStock.getStockPriceList();

        LineChart chart = view.findViewById(R.id.line_chart);
        setupChartAppearance(chart);
        setupChartData(chart,mStock.getPriceChangePercentage());

        return view;
    }

    private void setupChartAppearance(LineChart chart) {
        // General chart settings
        chart.setDrawGridBackground(false); // No grid background
        chart.getDescription().setEnabled(false); // No description text
        chart.setDrawBorders(false); // No border around chart
        chart.setTouchEnabled(true); // Enable touch interactions
        chart.setDragEnabled(true); // Enable drag
        chart.setScaleEnabled(true); // Enable zoom

        chart.setHighlightPerDragEnabled(true);
        chart.setHighlightPerTapEnabled(true);

        // Hide right Y-axis
        chart.getAxisRight().setEnabled(false);

        // Customize left Y-axis
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setDrawGridLines(true); // Light grid lines
        leftAxis.setGridColor(getResources().getColor(R.color.light_gray)); // Light gray grid lines
        leftAxis.setTextColor(getResources().getColor(R.color.black)); // Text color

        // Customize X-axis
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); // Position X-axis at bottom
        xAxis.setDrawGridLines(false); // No grid lines
        xAxis.setAxisMinimum(16.5f);
        float maxTime = 16.5f + (mStockClosePrices.size() / 60f);
        xAxis.setAxisMaximum(maxTime);
        xAxis.setTextColor(getResources().getColor(R.color.black)); // Text color
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int hours = (int) value;
                int minutes = (int) ((value - hours) * 60);
                return String.format("%02d:%02d", hours, minutes);
            }
        });

        // Disable legend
        chart.getLegend().setEnabled(false);
    }

    private void setupChartData(LineChart chart, double priceChange) {
        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < mStockClosePrices.size(); i += 5) {
            if (mStockClosePrices.get(i) != null) {
                float timeInHours = convertToDecimalHours(i);
                entries.add(new Entry(timeInHours, mStockClosePrices.get(i)));
            }
        }

        LineDataSet lineDataSet = new LineDataSet(entries, "Stocks");
        lineDataSet.setDrawCircles(false);
        lineDataSet.setLineWidth(2f);

        lineDataSet.setHighlightEnabled(true);
        lineDataSet.setDrawHorizontalHighlightIndicator(true);
        lineDataSet.setDrawVerticalHighlightIndicator(true);
        lineDataSet.setHighLightColor(getResources().getColor(R.color.black));

        lineDataSet.setDrawHighlightIndicators(true);
        lineDataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.format("$%.2f", value);
            }
        });
        // Set color based on price change
        int colorResId = (priceChange >= 0) ?
                R.color.positive_green :  // Use green for positive change
                R.color.negative_red;     // Use red for negative change

        lineDataSet.setColor(getResources().getColor(colorResId));
        lineDataSet.setDrawFilled(true);

        // Set gradient drawable based on price change
        int gradientResId = (priceChange >= 0) ?
                R.drawable.fade_green :   // Use green gradient for positive change
                R.drawable.fade_red;      // Use red gradient for negative change

        lineDataSet.setFillDrawable(getResources().getDrawable(gradientResId));
        lineDataSet.setDrawValues(false);

        LineData lineData = new LineData(lineDataSet);
        chart.setData(lineData);
        chart.invalidate();
    }

    private float convertToDecimalHours(int index) {
        float baseHour = 16.5f;
        float minutesAfterStart = index;
        return baseHour + (minutesAfterStart / 60f);
    }
}
