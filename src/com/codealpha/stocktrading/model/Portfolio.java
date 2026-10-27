package com.codealpha.stocktrading.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages user cash balance, stock share holdings, and transaction history.
 */
public class Portfolio {
    private double cashBalance;
    private final Map<String, Integer> holdings; // Ticker Symbol -> Quantity of Shares
    private final List<Transaction> transactions;

    public Portfolio(double initialCashBalance) {
        this.cashBalance = initialCashBalance;
        this.holdings = new HashMap<>();
        this.transactions = new ArrayList<>();
    }

    public double getCashBalance() {
        return cashBalance;
    }

    public void deposit(double amount) {
        if (amount > 0) {
            this.cashBalance += amount;
        }
    }

    public boolean withdraw(double amount) {
        if (amount > 0 && this.cashBalance >= amount) {
            this.cashBalance -= amount;
            return true;
        }
        return false;
    }

    public int getShares(String symbol) {
        return holdings.getOrDefault(symbol.toUpperCase().trim(), 0);
    }

    public void addShares(String symbol, int quantity) {
        String sym = symbol.toUpperCase().trim();
        int current = getShares(sym);
        holdings.put(sym, current + quantity);
    }

    public boolean removeShares(String symbol, int quantity) {
        String sym = symbol.toUpperCase().trim();
        int current = getShares(sym);
        if (current >= quantity) {
            int remaining = current - quantity;
            if (remaining == 0) {
                holdings.remove(sym);
            } else {
                holdings.put(sym, remaining);
            }
            return true;
        }
        return false;
    }

    public Map<String, Integer> getHoldings() {
        return new HashMap<>(holdings);
    }

    public void addTransaction(Transaction transaction) {
        this.transactions.add(transaction);
    }

    public List<Transaction> getTransactions() {
        return new ArrayList<>(transactions);
    }

    /**
     * Calculates the total portfolio net worth (Cash + Total Market Value of Stocks).
     */
    public double calculateTotalNetWorth(Map<String, Double> currentMarketPrices) {
        double totalStockValue = 0.0;
        for (Map.Entry<String, Integer> entry : holdings.entrySet()) {
            String symbol = entry.getKey();
            int shares = entry.getValue();
            double price = currentMarketPrices.getOrDefault(symbol, 0.0);
            totalStockValue += (shares * price);
        }
        return cashBalance + totalStockValue;
    }
}