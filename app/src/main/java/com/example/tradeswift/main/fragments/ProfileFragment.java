package com.example.tradeswift.main.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.tradeswift.R;
import com.example.tradeswift.main.models.PortfolioCalculator;
import com.example.tradeswift.main.models.StockDataHolder;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.Locale;

import com.example.tradeswift.main.viewmodels.SharedViewModel;

public class ProfileFragment extends Fragment {
    private TextView user_name;
    private TextView accountBalance;
    private TextView portfolioValue;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private PortfolioCalculator portfolioCalculator;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        DatabaseReference myRef = mDatabase.child("users").child(currentUser.getUid());


        user_name = view.findViewById(R.id.user_name);
        accountBalance = view.findViewById(R.id.account_balance);
        portfolioValue = view.findViewById(R.id.portfolio_value);

        myRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot dataSnapshot = task.getResult();
                user_name.setText(dataSnapshot.child("name").getValue().toString());
                accountBalance.setText(String.format("$%.2f",dataSnapshot.child("accountBalance").getValue(Double.class)));
            } else {
                Log.d("firebase", String.valueOf(task.getResult().getValue()));
            }

        });

        portfolioCalculator = new PortfolioCalculator(myRef.child("stockList"), StockDataHolder.stocks,portfolioValue);
        portfolioCalculator.calculatePortfolioValue();

        return view;
    }
}