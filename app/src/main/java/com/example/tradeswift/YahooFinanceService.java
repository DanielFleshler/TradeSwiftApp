package com.example.tradeswift;

import android.util.Log;

import com.example.tradeswift.main.models.Stock;
import com.example.tradeswift.main.models.StockData;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class YahooFinanceService {

    public ArrayList<Stock> getAllStocks() {
        String[] symbols = StockData.getAllStocks();
        String sURL = "https://query1.finance.yahoo.com/v8/finance/chart";
        ExecutorService executor = Executors.newFixedThreadPool(10);
        ArrayList<Future<Stock>> futures = new ArrayList<>();

        for (String symbol : symbols) {
            futures.add(executor.submit(() -> fetchStockData(sURL, symbol)));
        }

        ArrayList<Stock> stockList = new ArrayList<>();
        for (Future<Stock> future : futures) {
            try {
                stockList.add(future.get());
            } catch (Exception e) {
                Log.d("Fetch Error", "Error fetching stock data: " + e.getMessage());
            }
        }

        executor.shutdown();
        return stockList;
    }

    private Stock fetchStockData(String baseURL, String symbol) {
        try {
            URL url = new URL(baseURL + "/" + symbol);
            HttpURLConnection request = (HttpURLConnection) url.openConnection();
            request.setRequestProperty("User-Agent", "Mozilla/5.0");
            request.setRequestProperty("Accept", "application/json");
            request.setConnectTimeout(5000);
            request.setReadTimeout(5000);

            JsonParser jp = new JsonParser();
            JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent()));

            JsonObject close = root.getAsJsonObject()
                    .getAsJsonObject("chart")
                    .getAsJsonArray("result")
                    .get(0)
                    .getAsJsonObject()
                    .getAsJsonObject("indicators")
                    .getAsJsonArray("quote")
                    .get(0)
                    .getAsJsonObject();

            ArrayList<Float> closePrices = new ArrayList<>();
            JsonArray closeArray = close.getAsJsonArray("close");
            for(JsonElement price: closeArray){
                if (!price.isJsonNull()) {
                    closePrices.add(price.getAsFloat());
                } else {
                    closePrices.add(null);
                }
            }
            JsonObject meta = root.getAsJsonObject()
                    .getAsJsonObject("chart")
                    .getAsJsonArray("result")
                    .get(0)
                    .getAsJsonObject()
                    .getAsJsonObject("meta");

            String companyName = meta.get("longName").getAsString();
            double currentPrice = meta.get("regularMarketPrice").getAsDouble();
            double previousClose = meta.get("previousClose").getAsDouble();

            double priceChange = currentPrice - previousClose;
            double priceChangePercentage = (priceChange / previousClose) * 100;

            return new Stock(symbol, companyName, currentPrice, priceChange, priceChangePercentage,closePrices);
        } catch (IOException e) {
            Log.d("Fetch Error", "Error fetching stock data for symbol: " + symbol);
            return null;
        }
    }

}


