package com.example.tradeswift;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.tradeswift.main.adapters.StockAdapter;
import com.example.tradeswift.main.fragments.HomeFragment;
import com.example.tradeswift.main.models.Stock;
import com.example.tradeswift.main.models.User;
import com.example.tradeswift.main.viewmodels.SharedViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            getUserDataFromRealTimeDataBase(currentUser);
        }

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
            BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
            NavigationUI.setupWithNavController(bottomNav, navController);
            bottomNav.setOnItemSelectedListener(item -> {
                navController.popBackStack(navController.getGraph().getStartDestinationId(), false);
                return NavigationUI.onNavDestinationSelected(item, navController);
            });
        }
    }

    public void getUserDataFromRealTimeDataBase(FirebaseUser currentUser) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        String uid = currentUser.getUid();
        String email = currentUser.getEmail();
        System.out.println("Current User ID: " + uid);
        System.out.println("Current User Email: " + email);
        mDatabase.child("users").child(uid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(HomeActivity.this, "Canno't fetch user data", Toast.LENGTH_SHORT).show();
                } else {
                    DataSnapshot snapshot = task.getResult();
                    String name = snapshot.child("name").getValue(String.class);
                    Double accBalance = snapshot.child("accountBalance").getValue(Double.class);
                    System.out.println("Current User Name: " + name);
                    System.out.println("Current User Balance: " + accBalance);
                    User userData = new User(
                            name,
                            email,
                            accBalance
                    );
                }
            }
        });
    }
}
