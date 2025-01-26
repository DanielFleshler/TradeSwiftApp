package com.example.tradeswift.main.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.tradeswift.main.models.Stock;
import com.example.tradeswift.main.models.User;

import java.util.ArrayList;

public class SharedViewModel extends ViewModel {
    private final MutableLiveData<User> userData = new MutableLiveData<>();
    private final MutableLiveData<ArrayList<Stock>> stockArrayList = new MutableLiveData<>();

    public void setUserData(User user) {
        userData.setValue(user);
    }

    public LiveData<User> getUserData() {
        return userData;
    }

    public void setStockList(ArrayList<Stock> stockList) {
        stockArrayList.setValue(stockList);
    }
    public LiveData<ArrayList<Stock>> getStockList() {
        return stockArrayList;
    }
}