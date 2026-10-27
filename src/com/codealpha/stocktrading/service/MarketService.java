package com.codealpha.stocktrading.service;

import com.codealpha.stocktrading.model.Stock;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Manages market equities, live data display, and price simulations.
 */
public class MarketService {
    private final List<Stock> stocks;
    private static final String MARKET_FILE = "data/stocks.csv";
    private final Random random;

    public MarketService() {
        this.stocks = new ArrayList<>();
        this.random = new Random();
        loadMarketData();
    }

    public List<Stock> getAllStocks() {
        return new ArrayList<>(stocks);
    }

    public Stock getStockBySymbol(String symbol) {
        if (symbol == null) return null;
        String target = symbol.toUpperCase().trim();
        for (Stock s : stocks) {
            if (s.getSymbol().equals(target)) {
                return s;
            }
        }
        return null;
    }

    public Map<String, Double> getCurrentMarketPricesMap() {
        Map<String, Double> prices = new HashMap<>();
        for (Stock s : stocks) {
            prices.put(s.getSymbol(), s.getCurrentPrice());
        }
        return prices;
    }

    /**
     * Simulates market price ticks (fluctuates prices up or down randomly by -3% to +3%).
     */
    public void simulateMarketTick() {
        for (Stock s : stocks) {
            double percentChange = (random.nextDouble() * 6.0 - 3.0) / 100.0; // -3% to +3%
            double newPrice = s.getCurrentPrice() * (1.0 + percentChange);
            if (newPrice < 1.0) newPrice = 1.0; // Minimum floor price
            // Round to 2 decimal places
            newPrice = Math.round(newPrice * 100.0) / 100.0;
            s.updatePrice(newPrice);
        }
        saveMarketData();
    }

    public void saveMarketData() {
        File file = new File(MARKET_FILE);
        if (file.getParentFile() != null) {
            file.getParentFile().mkdirs();
        }
        try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
            for (Stock s : stocks) {
                pw.println(s.getSymbol() + "," + s.getCompanyName() + "," + s.getCurrentPrice());
            }
        } catch (IOException e) {
            System.err.println("Warning: Could not save market data: " + e.getMessage());
        }
    }

    public final void loadMarketData() {
        File file = new File(MARKET_FILE);
        if (!file.exists()) {
            seedDefaultStocks();
            return;
        }
        stocks.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    try {
                        String symbol = parts[0].trim();
                        String name = parts[1].trim();
                        double price = Double.parseDouble(parts[2].trim());
                        stocks.add(new Stock(symbol, name, price));
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Warning: Could not load market data: " + e.getMessage());
        }
        
        if (stocks.isEmpty()) {
            seedDefaultStocks();
        }
    }

    private void seedDefaultStocks() {
        stocks.clear();
        stocks.add(new Stock("AAPL", "Apple Inc.", 185.50));
        stocks.add(new Stock("TSLA", "Tesla Inc.", 240.20));
        stocks.add(new Stock("GOOGL", "Alphabet Inc.", 142.80));
        stocks.add(new Stock("AMZN", "Amazon.com Inc.", 155.10));
        stocks.add(new Stock("MSFT", "Microsoft Corp.", 410.30));
        saveMarketData();
    }
}