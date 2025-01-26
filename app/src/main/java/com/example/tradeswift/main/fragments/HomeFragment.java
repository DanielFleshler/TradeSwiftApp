package com.example.tradeswift.main.fragments;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tradeswift.HomeActivity;
import com.example.tradeswift.R;
import com.example.tradeswift.main.adapters.StockAdapter;
import com.example.tradeswift.main.models.Stock;
import com.example.tradeswift.main.models.StockDataHolder;
import com.example.tradeswift.main.viewmodels.SharedViewModel;
import com.example.tradeswift.YahooFinanceService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class HomeFragment extends Fragment {
    private StockAdapter stockAdapter;
    private ArrayList<Stock> stockArrayList = new ArrayList<>();
    private YahooFinanceService yahooFinanceService = new YahooFinanceService();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        MaterialCardView depositCard = view.findViewById(R.id.deposit_amount);
        MaterialCardView withdrawCard = view.findViewById(R.id.withdraw_amount);
        withdrawCard.setOnClickListener(v -> showWithdrawModal());
        depositCard.setOnClickListener(v -> showDepositModal());

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        RecyclerView recyclerView = view.findViewById(R.id.stocks_recycler);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(layoutManager);


        stockAdapter = new StockAdapter(stockArrayList);
        recyclerView.setAdapter(stockAdapter);


        loadStockData();
        return view;
    }

    private void loadStockData() {
        new Thread(() -> {
            ArrayList<Stock> stocks = yahooFinanceService.getAllStocks();
                getActivity().runOnUiThread(() -> {
                    StockDataHolder.stocks = stocks;
                    stockArrayList.clear();
                    stockArrayList.addAll(stocks);
                    stockAdapter.notifyDataSetChanged();
                });
        }).start();
    }
    private void showDepositModal() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.deposit_modal_layout, null);


        TextInputEditText depositAmountEditText = dialogView.findViewById(R.id.deposit_amount);
        MaterialButton cancelButton = dialogView.findViewById(R.id.cancel_button);
        MaterialButton confirmButton = dialogView.findViewById(R.id.confirm_button);


        AlertDialog dialog = builder.setView(dialogView).create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();


        cancelButton.setOnClickListener(v -> dialog.dismiss());


        confirmButton.setOnClickListener(v -> {
            String amountStr = depositAmountEditText.getText().toString().trim();
            if (amountStr.isEmpty()) {
                Toast.makeText(getContext(), "Please enter an amount to deposit", Toast.LENGTH_SHORT).show();
                return;
            }
            double depositAmount = Double.parseDouble(amountStr);
            if (depositAmount <= 0) {
                Toast.makeText(getContext(), "Please enter a valid amount", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                mDatabase.child("users").child(currentUser.getUid()).child("accountBalance").get().addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DataSnapshot dataSnapshot = task.getResult();
                        double userAccountBalance = Double.parseDouble(dataSnapshot.getValue().toString());
                        mDatabase.child("users").child(currentUser.getUid()).child("accountBalance")
                                .setValue(userAccountBalance + depositAmount)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(getContext(), "Deposit successful", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(getContext(), "Deposit failed", Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        Toast.makeText(getContext(), "Failed to fetch account balance", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void showWithdrawModal() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.withdraw_modal_layout, null);


        TextInputEditText withdrawAmountEditText = dialogView.findViewById(R.id.withdraw_amount);
        MaterialButton cancelButton = dialogView.findViewById(R.id.cancel_button);
        MaterialButton confirmButton = dialogView.findViewById(R.id.confirm_button);

        AlertDialog dialog = builder.setView(dialogView).create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();


        cancelButton.setOnClickListener(v -> dialog.dismiss());


        confirmButton.setOnClickListener(v -> {
            String amountStr = withdrawAmountEditText.getText().toString().trim();
            if (amountStr.isEmpty()) {
                Toast.makeText(getContext(), "Please enter an amount to withdraw", Toast.LENGTH_SHORT).show();
                return;
            }
            double withdrawAmount = Double.parseDouble(amountStr);
            if (withdrawAmount <= 0) {
                Toast.makeText(getContext(), "Please enter a valid amount", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                mDatabase.child("users").child(currentUser.getUid()).child("accountBalance").get().addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DataSnapshot dataSnapshot = task.getResult();
                        double userAccountBalance = Double.parseDouble(dataSnapshot.getValue().toString());
                        if (withdrawAmount > userAccountBalance) {
                            Toast.makeText(getContext(), "Insufficient balance", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        mDatabase.child("users").child(currentUser.getUid()).child("accountBalance")
                                .setValue(userAccountBalance - withdrawAmount)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(getContext(), "Withdrawal successful", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                });
                    } else {
                        Toast.makeText(getContext(), "Failed to fetch account balance", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

}
