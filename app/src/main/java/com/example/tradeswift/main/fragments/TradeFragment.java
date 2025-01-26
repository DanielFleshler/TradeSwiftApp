package com.example.tradeswift.main.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tradeswift.R;
import com.example.tradeswift.main.adapters.StockAdapter;
import com.example.tradeswift.main.adapters.StockSearchAdapter;
import com.example.tradeswift.main.models.Stock;
import com.example.tradeswift.main.models.StockDataHolder;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TradeFragment extends Fragment {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private ArrayList<Stock> stockArrayList = new ArrayList<>();
    private Stock selectedStock;
    private double estimatedPrice;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trade, container, false);

        // Initialize Firebase instances
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        // Setup RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.search_results_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        // Initialize stock list and adapter
        stockArrayList = StockDataHolder.stocks;
        StockSearchAdapter stockSearchAdapter = new StockSearchAdapter(stockArrayList);
        recyclerView.setAdapter(stockSearchAdapter);

        // Initialize UI components
        SearchView searchView = view.findViewById(R.id.search_edit_text);
        EditText quantityEditText = view.findViewById(R.id.quantity);
        TextView estimatedPriceTextView = view.findViewById(R.id.estimated_price);
        MaterialButton buyButton = view.findViewById(R.id.buy_button);
        MaterialButton sellButton = view.findViewById(R.id.sell_button);

        // Setup search view listener
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    quantityEditText.setText("");
                }
                Stock filteredStock = stockSearchAdapter.filterStock(newText);
                stockSearchAdapter.updateList(filteredStock);
                selectedStock = filteredStock;

                return true;
            }
        });

        // Setup quantity text change listener
        quantityEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String quantityText = quantityEditText.getText().toString().trim();
                if (quantityText.isEmpty()) {
                    estimatedPriceTextView.setText("Estimated Total: $0.00");
                    return;
                }
                double quantity = Double.parseDouble(quantityText);
                if (selectedStock != null) {
                    double stockPrice = selectedStock.getCurrentPrice();
                    estimatedPrice = quantity * stockPrice;
                    estimatedPriceTextView.setText(String.format("Estimated Total: $%.2f", estimatedPrice));
                }
            }
        });

        // Setup buy button listener
        buyButton.setOnClickListener(v -> handleBuyButton(estimatedPrice, currentUser, quantityEditText));

        // Setup sell button listener
        sellButton.setOnClickListener(v -> handleSellButton(estimatedPrice, currentUser, quantityEditText));

        return view;
    }

    private void handleBuyButton(double estimatedPrice, FirebaseUser currentUser, EditText quantityEditText) {
        try {
            if (selectedStock == null) {
                Toast.makeText(getContext(), "Please select a stock first", Toast.LENGTH_SHORT).show();
                return;
            }

            String quantityText = quantityEditText.getText().toString();
            if (quantityText.isEmpty()) {
                Toast.makeText(getContext(), "Please enter quantity", Toast.LENGTH_SHORT).show();
                return;
            }

            int stockAmount = Integer.parseInt(quantityText);
            if (stockAmount <= 0) {
                Toast.makeText(getContext(), "Please enter a valid quantity", Toast.LENGTH_SHORT).show();
                return;
            }

            buyStock(currentUser, selectedStock, stockAmount);
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Please enter a valid number", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleSellButton(double estimatedPrice, FirebaseUser currentUser, EditText quantityEditText) {
        try {
            if (selectedStock == null) {
                Toast.makeText(getContext(), "Please select a stock first", Toast.LENGTH_SHORT).show();
                return;
            }

            String quantityText = quantityEditText.getText().toString();
            if (quantityText.isEmpty()) {
                Toast.makeText(getContext(), "Please enter quantity", Toast.LENGTH_SHORT).show();
                return;
            }

            int stockAmount = Integer.parseInt(quantityText);
            if (stockAmount <= 0) {
                Toast.makeText(getContext(), "Please enter a valid quantity", Toast.LENGTH_SHORT).show();
                return;
            }

            sellStock(estimatedPrice, currentUser, selectedStock, stockAmount);
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Please enter a valid number", Toast.LENGTH_SHORT).show();
        }
    }

    private void buyStock(FirebaseUser currentUser, Stock selectedStock, int stockAmount) {
        String key = currentUser.getUid();
        String stockSymbol = selectedStock.getSymbol();
        DatabaseReference myRef = mDatabase.child("users").child(key);

        myRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot dataSnapshot = task.getResult();

                Map<String, Object> transaction = new HashMap<>();
                transaction.put("quantity", stockAmount);
                transaction.put("price", selectedStock.getCurrentPrice());

                double accountBalance = Double.parseDouble(dataSnapshot.child("accountBalance").getValue().toString());

                if (estimatedPrice > accountBalance) {
                    Toast.makeText(getContext(), "Insufficient credits", Toast.LENGTH_SHORT).show();
                    return;
                }

                myRef.child("stockList").child(stockSymbol).push().setValue(transaction);

                myRef.child("accountBalance").setValue(accountBalance - estimatedPrice);
                Toast.makeText(getContext(), "Purchase successful", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void sellStock(double estimatedPrice, FirebaseUser currentUser, Stock selectedStock, int stockAmount) {
        String key = currentUser.getUid();
        String stockSymbol = selectedStock.getSymbol();
        DatabaseReference myRef = mDatabase.child("users").child(key);
        myRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    DataSnapshot dataSnapshot = task.getResult();
                    Map<String, Object> updates = new HashMap<>();
                    double accountBalance = Double.parseDouble(dataSnapshot.child("accountBalance").getValue().toString());

                    if (!dataSnapshot.child("stockList").hasChild(stockSymbol)) {
                        Toast.makeText(getContext(), "Stock not found", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    int totalStockQuantity = 0;
                    dataSnapshot = dataSnapshot.child("stockList").child(stockSymbol);

                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        totalStockQuantity += Integer.parseInt(childSnapshot.child("quantity").getValue().toString());
                    }
                    if (stockAmount > totalStockQuantity) {
                        Toast.makeText(getContext(), "Insufficient stocks amount", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    int tempStockAmount = stockAmount;
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        int curQuantity = Integer.parseInt(childSnapshot.child("quantity").getValue().toString());

                        if (curQuantity >= tempStockAmount) {
                            curQuantity -= tempStockAmount;
                            if (curQuantity == 0) {
                                myRef.child("accountBalance").setValue(accountBalance + estimatedPrice);
                                myRef.child("stockList").child(stockSymbol).child(childSnapshot.getKey()).removeValue();
                                return;
                            } else {
                                updates.put("quantity", curQuantity);
                                myRef.child("stockList").child(stockSymbol).child(childSnapshot.getKey()).updateChildren(updates);
                                myRef.child("accountBalance").setValue(accountBalance + estimatedPrice);
                            }
                        } else {
                            tempStockAmount -= curQuantity;
                            myRef.child("stockList").child(stockSymbol).child(childSnapshot.getKey()).removeValue();
                        }
                    }
                    Toast.makeText(getContext(), "Purchase failed", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
}

