package com.codealpha.stocktrading;

import com.codealpha.stocktrading.service.MarketService;
import com.codealpha.stocktrading.service.TradingService;
import com.codealpha.stocktrading.ui.TradingPlatformGUI;

import javax.swing.*;

/**
 * Entry point for the CodeAlpha Stock Trading Platform.
 */
public class Main {
    public static void main(String[] args) {
        // Set native OS look and feel for modern UI design
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        // Run GUI safely on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            MarketService marketService = new MarketService();
            TradingService tradingService = new TradingService(marketService);
            
            TradingPlatformGUI gui = new TradingPlatformGUI(marketService, tradingService);
            gui.setVisible(true);
        });
    }
}