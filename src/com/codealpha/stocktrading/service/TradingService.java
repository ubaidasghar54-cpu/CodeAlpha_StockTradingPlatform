package com.codealpha.stocktrading.service;

import com.codealpha.stocktrading.model.Portfolio;
import com.codealpha.stocktrading.model.Stock;
import com.codealpha.stocktrading.model.Transaction;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Handles buy/sell order executions, portfolio management, and file persistence.
 */
public class TradingService {
    private final Portfolio portfolio;
    private final MarketService marketService;
    private static final String PORTFOLIO_FILE = "data/portfolio.csv";
    private int transactionCounter = 1000;

    public TradingService(MarketService marketService) {
        this.marketService = marketService;
        this.portfolio = new Portfolio(10000.0); // Default starting virtual cash: $10,000.00
        loadPortfolioData();
    }

    public Portfolio getPortfolio() {
        return portfolio;
    }

    /**
     * Executes a BUY order for shares of a stock.
     */
    public boolean buyStock(String symbol, int quantity) {
        if (quantity <= 0) return false;
        Stock stock = marketService.getStockBySymbol(symbol);
        if (stock == null) return false;

        double totalCost = stock.getCurrentPrice() * quantity;
        if (portfolio.getCashBalance() >= totalCost) {
            if (portfolio.withdraw(totalCost)) {
                portfolio.addShares(stock.getSymbol(), quantity);
                
                String txId = "TXN-" + (++transactionCounter);
                Transaction txn = new Transaction(txId, Transaction.Type.BUY, stock.getSymbol(), quantity, stock.getCurrentPrice());
                portfolio.addTransaction(txn);
                
                savePortfolioData();
                return true;
            }
        }
        return false;
    }

    /**
     * Executes a SELL order for shares of a stock.
     */
    public boolean sellStock(String symbol, int quantity) {
        if (quantity <= 0) return false;
        Stock stock = marketService.getStockBySymbol(symbol);
        if (stock == null) return false;

        if (portfolio.getShares(stock.getSymbol()) >= quantity) {
            if (portfolio.removeShares(stock.getSymbol(), quantity)) {
                double totalRevenue = stock.getCurrentPrice() * quantity;
                portfolio.deposit(totalRevenue);

                String txId = "TXN-" + (++transactionCounter);
                Transaction txn = new Transaction(txId, Transaction.Type.SELL, stock.getSymbol(), quantity, stock.getCurrentPrice());
                portfolio.addTransaction(txn);

                savePortfolioData();
                return true;
            }
        }
        return false;
    }

    public void savePortfolioData() {
        File file = new File(PORTFOLIO_FILE);
        if (file.getParentFile() != null) {
            file.getParentFile().mkdirs();
        }
        try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
            // Line 1: Cash balance
            pw.println("CASH," + portfolio.getCashBalance());
            // Subsequent lines: Holdings (HOLDING,SYMBOL,QUANTITY)
            for (var entry : portfolio.getHoldings().entrySet()) {
                pw.println("HOLDING," + entry.getKey() + "," + entry.getValue());
            }
        } catch (IOException e) {
            System.err.println("Warning: Could not save portfolio data: " + e.getMessage());
        }
    }

    public final void loadPortfolioData() {
        File file = new File(PORTFOLIO_FILE);
        if (!file.exists()) {
            return; // Uses default $10,000 cash balance
        }
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",");
                if (parts.length >= 2) {
                    String type = parts[0].trim();
                    if (type.equals("CASH")) {
                        double cash = Double.parseDouble(parts[1].trim());
                        // Reset portfolio with loaded cash
                        double diff = cash - portfolio.getCashBalance();
                        if (diff > 0) portfolio.deposit(diff);
                        else if (diff < 0) portfolio.withdraw(-diff);
                    } else if (type.equals("HOLDING") && parts.length >= 3) {
                        String symbol = parts[1].trim();
                        int qty = Integer.parseInt(parts[2].trim());
                        portfolio.addShares(symbol, qty);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Warning: Could not load portfolio data: " + e.getMessage());
        }
    }
}