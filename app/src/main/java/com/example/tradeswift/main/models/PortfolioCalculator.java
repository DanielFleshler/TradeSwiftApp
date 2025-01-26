package com.example.tradeswift.main.models;

import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.example.tradeswift.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;


public class PortfolioCalculator {
    private final DatabaseReference userStockListRef;
    private final ArrayList<Stock> portfolioStocks;
    private TextView portfolio_value;

    private TextView total_gain_loss;


    public PortfolioCalculator(DatabaseReference userStockListRef, ArrayList<Stock> portfolioStocks, TextView portfolio_value) {
        this.userStockListRef = userStockListRef;
        this.portfolioStocks = portfolioStocks;
        this.portfolio_value = portfolio_value;
    }

    public PortfolioCalculator(DatabaseReference userStockListRef, ArrayList<Stock> portfolioStocks, TextView portfolio_value, TextView total_gain_loss) {
        this.userStockListRef = userStockListRef;
        this.portfolioStocks = portfolioStocks;
        this.portfolio_value = portfolio_value;
        this.total_gain_loss = total_gain_loss;
    }

    public void calculatePortfolioValue() {
        userStockListRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("PortfolioCalculator", "Error fetching portfolio data", task.getException());
                    return;
                }

                DataSnapshot dataSnapshot = task.getResult();
                double totalGainLoss = 0;
                double totalInvestmentValue = 0;

                for (DataSnapshot stockSnapshot : dataSnapshot.getChildren()) {
                    String symbol = stockSnapshot.getKey();
                    double currentPrice = 0;

                    for (Stock stock : portfolioStocks) {
                        if (stock.getSymbol().equals(symbol)) {
                            currentPrice = stock.getCurrentPrice();
                            break;
                        }
                    }

                    double totalStockPrice = 0;
                    double totalStockQuantity = 0;

                    for (DataSnapshot transactionSnapshot : stockSnapshot.getChildren()) {
                        double transactionPrice = Double.parseDouble(transactionSnapshot.child("price").getValue().toString());
                        double transactionQuantity = Double.parseDouble(transactionSnapshot.child("quantity").getValue().toString());
                        totalStockPrice += transactionPrice * transactionQuantity;
                        totalStockQuantity += transactionQuantity;
                    }

                    if (totalStockQuantity == 0) {
                        Log.w("PortfolioCalculator", "No transactions found for stock: " + symbol);
                        continue;
                    }

                    double averagePurchasePrice = totalStockPrice / totalStockQuantity;
                    double stockInvestmentValue = totalStockQuantity * currentPrice;
                    double stockGainLoss = stockInvestmentValue - (totalStockQuantity * averagePurchasePrice);

                    totalGainLoss += stockGainLoss;
                    totalInvestmentValue += stockInvestmentValue;
                    Log.d("Portfolio Calculator", "Portfolio Value" + totalInvestmentValue);
                }

                double percentageGainLoss = (totalGainLoss / totalInvestmentValue) * 100;
                portfolio_value.setText(String.format("$%.2f",totalInvestmentValue));
                if (total_gain_loss != null) {
                    int colorResId = totalGainLoss >= 0 ? R.color.success : R.color.error;
                    total_gain_loss.setText(String.format("$%.2f (%.2f %%)", totalGainLoss, percentageGainLoss));
                    total_gain_loss.setTextColor(ContextCompat.getColor(total_gain_loss.getContext(), colorResId));
                }
            }
        });

    }
}
