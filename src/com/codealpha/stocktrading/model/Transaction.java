package com.codealpha.stocktrading.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a completed buy or sell trade transaction.
 */
public class Transaction {
    public enum Type { BUY, SELL }

    private String transactionId;
    private Type type;
    private String symbol;
    private int quantity;
    private double pricePerShare;
    private LocalDateTime timestamp;

    public Transaction(String transactionId, Type type, String symbol, int quantity, double pricePerShare) {
        this.transactionId = transactionId;
        this.type = type;
        this.symbol = symbol.toUpperCase().trim();
        this.quantity = quantity;
        this.pricePerShare = pricePerShare;
        this.timestamp = LocalDateTime.now();
    }

    public double getTotalAmount() {
        return quantity * pricePerShare;
    }

    public String getFormattedTimestamp() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return timestamp.format(formatter);
    }

    public String getTransactionId() {
        return transactionId;
    }

    public Type getType() {
        return type;
    }

    public String getSymbol() {
        return symbol;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPricePerShare() {
        return pricePerShare;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s %d shares of %s @ $%.2f (Total: $%.2f) on %s",
                transactionId, type, quantity, symbol, pricePerShare, getTotalAmount(), getFormattedTimestamp());
    }
}