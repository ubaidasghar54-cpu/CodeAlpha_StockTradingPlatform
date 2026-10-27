package com.codealpha.stocktrading.model;

/**
 * Represents an individual stock equity in the trading market.
 */
public class Stock {
    private String symbol;
    private String companyName;
    private double currentPrice;
    private double previousClose;

    public Stock(String symbol, String companyName, double currentPrice) {
        this.symbol = symbol.toUpperCase().trim();
        this.companyName = companyName.trim();
        this.currentPrice = currentPrice;
        this.previousClose = currentPrice;
    }

    public void updatePrice(double newPrice) {
        this.previousClose = this.currentPrice;
        this.currentPrice = newPrice;
    }

    public double getPriceChangePercentage() {
        if (previousClose == 0) return 0.0;
        return ((currentPrice - previousClose) / previousClose) * 100.0;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getCompanyName() {
        return companyName;
    }

    public double getCurrentPrice() {
        return currentPrice;
    }

    public double getPreviousClose() {
        return previousClose;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s - $%.2f (%.2f%%)", symbol, companyName, currentPrice, getPriceChangePercentage());
    }
}