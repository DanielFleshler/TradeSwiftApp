package com.example.tradeswift.main.fragments;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tradeswift.R;
import com.example.tradeswift.main.adapters.StockAdapter;
import com.example.tradeswift.main.models.PortfolioCalculator;
import com.example.tradeswift.main.models.Stock;
import com.example.tradeswift.main.models.StockDataHolder;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okio.Timeout;

public class PortfolioFragment extends Fragment {
    private TextView portfolio_value;
    private TextView total_gain_loss;
    private StockAdapter stockAdapter;
    private ArrayList<Stock> portfolioStocks = new ArrayList<>();
    private PortfolioCalculator portfolioCalculator;
    private DatabaseReference userStockListRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_portfolio, container, false);


        //Initialize TextView
        portfolio_value = view.findViewById(R.id.portfolio_value);
        total_gain_loss = view.findViewById(R.id.total_gain_loss);

        // RecyclerView setup
        RecyclerView recyclerView = view.findViewById(R.id.portfolio_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        // Initialize adapter and set it to the RecyclerView
        stockAdapter = new StockAdapter(portfolioStocks);
        recyclerView.setAdapter(stockAdapter);

        // Firebase setup
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        String userId = mAuth.getCurrentUser().getUid();
        userStockListRef = mDatabase.child("users").child(userId).child("stockList");

        fetchPortfolioStocks();

        portfolioCalculator = new PortfolioCalculator(userStockListRef,portfolioStocks,portfolio_value,total_gain_loss);
        portfolioCalculator.calculatePortfolioValue();
        return view;
    }


    private void fetchPortfolioStocks() {
        // Create a HashMap for quick lookups
        HashMap<String, Stock> stockMap = new HashMap<>();
        for (Stock stock : StockDataHolder.stocks) {
            stockMap.put(stock.getSymbol(), stock);
        }

        // Fetch data from Firebase
        userStockListRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                portfolioStocks.clear(); // Clear old data
                DataSnapshot dataSnapshot = task.getResult();

                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    String symbol = childSnapshot.getKey();
                    Stock stock = stockMap.get(symbol);
                    if (stock != null) {
                        portfolioStocks.add(stock);
                    }
                }

                stockAdapter.notifyDataSetChanged(); // Notify adapter of data changes
            } else {
                Log.e("PortfolioFragment", "Error fetching data", task.getException());
            }
        });
    }
}
