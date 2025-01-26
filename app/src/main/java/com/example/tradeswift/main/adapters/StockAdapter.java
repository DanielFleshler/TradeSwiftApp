package com.example.tradeswift.main.adapters;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tradeswift.R;
import com.example.tradeswift.main.fragments.ChartFragment;
import com.example.tradeswift.main.fragments.HomeFragment;
import com.example.tradeswift.main.models.Stock;


import java.util.ArrayList;

public class StockAdapter extends RecyclerView.Adapter<StockAdapter.MyViewHolder> {


    private ArrayList<Stock> stockArrayList;

    public StockAdapter(ArrayList<Stock> stock_ArrayList) {
        stockArrayList = stock_ArrayList;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        LinearLayout linearLayout;
        TextView textViewStockSymbol;
        TextView textViewStockName;
        TextView textViewStockPrice;
        TextView textViewStockChange;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewStockSymbol = itemView.findViewById(R.id.stock_symbol);
            textViewStockName = itemView.findViewById(R.id.stock_name);
            textViewStockPrice = itemView.findViewById(R.id.stock_price);
            textViewStockChange = itemView.findViewById(R.id.stock_change);
            linearLayout = itemView.findViewById(R.id.stock_layout);
        }
    }


    @NonNull
    @Override
    public StockAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_stock, parent, false);
        return new MyViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull StockAdapter.MyViewHolder holder, int position) {
        Stock currentStock = stockArrayList.get(position);
        holder.textViewStockSymbol.setText(currentStock.getSymbol());
        holder.textViewStockName.setText(currentStock.getName());
        holder.textViewStockPrice.setText(String.valueOf(currentStock.getCurrentPrice()));

        double priceChange = currentStock.getPriceChangePercentage();
        holder.textViewStockChange.setText(String.format("%.2f%%", priceChange));

        int colorResId = priceChange >= 0 ? R.color.success : R.color.error;
        holder.textViewStockChange.setTextColor(holder.itemView.getContext().getColor(colorResId));

        holder.linearLayout.setOnClickListener(v -> {
            String symbol = holder.textViewStockSymbol.getText().toString();
            Bundle bundle = new Bundle();
            bundle.putString("param1", symbol);
            NavController navController = Navigation.findNavController(v);
            if (navController.getCurrentDestination().getId() == R.id.homeFragment) {
                navController.navigate(R.id.action_homeFragment_to_chartFragment, bundle);
            }
        });
    }
    @Override
    public int getItemCount() {
        return stockArrayList.size();
    }
}
