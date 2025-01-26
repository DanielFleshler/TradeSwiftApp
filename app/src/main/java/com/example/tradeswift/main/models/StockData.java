package com.example.tradeswift.main.models;

import java.util.ArrayList;

public class StockData {
    public static final String[] TOP_50_SP500_STOCKS = {
            // Technology
            "MSFT",  // Microsoft
            "AAPL",  // Apple
            "NVDA",  // NVIDIA
            "AVGO",  // Broadcom
            "META",  // Meta Platforms
            "GOOGL", // Alphabet Class A
            "GOOG",  // Alphabet Class C
            "AMD",   // Advanced Micro Devices
            "ADBE",  // Adobe
            "CRM",   // Salesforce

            // Healthcare
            "LLY",   // Eli Lilly
            "UNH",   // UnitedHealth Group
            "JNJ",   // Johnson & Johnson
            "MRK",   // Merck
            "ABBV",  // AbbVie
            "PFE",   // Pfizer

            // Consumer
            "AMZN",  // Amazon
            "WMT",   // Walmart
            "PG",    // Procter & Gamble
            "COST",  // Costco
            "PEP",   // PepsiCo
            "KO",    // Coca-Cola
            "MCD",   // McDonald's
            "NKE",   // Nike

            // Financial
            "BRK-B", // Berkshire Hathaway
            "JPM",   // JPMorgan Chase
            "V",     // Visa
            "MA",    // Mastercard
            "BAC",   // Bank of America

            // Energy
            "XOM",   // ExxonMobil
            "CVX",   // Chevron

            // Industrial
            "CAT",   // Caterpillar
            "RTX",   // Raytheon Technologies
            "HON",   // Honeywell
            "DE",    // John Deere

            // Communications
            "NFLX",  // Netflix
            "CMCSA", // Comcast
            "T",     // AT&T
            "VZ",    // Verizon

            // Others
            "HD",    // Home Depot
            "ORCL",  // Oracle
            "TMO",   // Thermo Fisher Scientific
            "ACN",   // Accenture
            "DHR",   // Danaher
            "LIN",   // Linde
            "INTU",  // Intuit
            "TXN",   // Texas Instruments
            "IBM",   // IBM
            "SPGI"   // S&P Global
    };

    public static String[] getAllStocks() {
        return TOP_50_SP500_STOCKS;
    }

    public static String[] getStocksBySector(String sector) {
        switch (sector.toLowerCase()) {
            case "technology":
                return new String[]{"MSFT", "AAPL", "NVDA", "AVGO", "META", "GOOGL", "GOOG", "AMD", "ADBE", "CRM"};
            case "healthcare":
                return new String[]{"LLY", "UNH", "JNJ", "MRK", "ABBV", "PFE"};
            case "consumer":
                return new String[]{"AMZN", "WMT", "PG", "COST", "PEP", "KO", "MCD", "NKE"};
            case "financial":
                return new String[]{"BRK-B", "JPM", "V", "MA", "BAC"};
            case "energy":
                return new String[]{"XOM", "CVX"};
            case "industrial":
                return new String[]{"CAT", "RTX", "HON", "DE"};
            case "communications":
                return new String[]{"NFLX", "CMCSA", "T", "VZ"};
            default:
                return new String[]{};
        }
    }
}