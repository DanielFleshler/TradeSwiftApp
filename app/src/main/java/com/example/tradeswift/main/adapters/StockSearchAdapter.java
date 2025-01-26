package com.example.tradeswift.main.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.tradeswift.R;
import com.example.tradeswift.main.models.Stock;
import java.util.ArrayList;

public class StockSearchAdapter extends RecyclerView.Adapter<StockSearchAdapter.SearchViewHolder> {

    private ArrayList<Stock> stockDataSet;
    private Stock displayedStock;

    public StockSearchAdapter(ArrayList<Stock> stock_ArrayList) {
        stockDataSet = stock_ArrayList;
        displayedStock = null;
    }

    public void updateList(Stock filteredStock) {
        displayedStock = filteredStock;
        notifyDataSetChanged();
    }

    public void clearDisplay() {
        displayedStock = null;
        notifyDataSetChanged();
    }

    public static class SearchViewHolder extends RecyclerView.ViewHolder {
        TextView textViewStockSymbol;
        TextView textViewStockName;
        TextView textViewStockPrice;
        TextView textViewStockChange;

        public SearchViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewStockSymbol = itemView.findViewById(R.id.stock_symbol);
            textViewStockName = itemView.findViewById(R.id.stock_name);
            textViewStockPrice = itemView.findViewById(R.id.stock_price);
            textViewStockChange = itemView.findViewById(R.id.stock_change);
        }
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_stock, parent, false);
        return new SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
        if (displayedStock != null) {
            holder.textViewStockSymbol.setText(displayedStock.getSymbol());
            holder.textViewStockName.setText(displayedStock.getName());
            holder.textViewStockPrice.setText(String.valueOf(displayedStock.getCurrentPrice()));

            double priceChange = displayedStock.getPriceChangePercentage();
            holder.textViewStockChange.setText(String.format("%.2f%%", priceChange));

            int colorResId = priceChange >= 0 ? R.color.success : R.color.error;
            holder.textViewStockChange.setTextColor(
                    holder.itemView.getContext().getColor(colorResId)
            );
        }
    }

    @Override
    public int getItemCount() {
        return displayedStock != null ? 1 : 0;
    }

    public Stock filterStock(String query) {
        if (query == null || query.isEmpty()) {
            return null;
        }

        query = query.toLowerCase().trim();

        for (Stock stock : stockDataSet) {
            if (stock.getSymbol().toLowerCase().equals(query)) {
                return stock;
            }
            if(stock.getName().toLowerCase().equals(query)){
                return stock;
            }
        }
        return null;
    }
}