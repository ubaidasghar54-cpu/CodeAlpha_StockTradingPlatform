package com.codealpha.stocktrading.ui;

import com.codealpha.stocktrading.model.Portfolio;
import com.codealpha.stocktrading.model.Stock;
import com.codealpha.stocktrading.model.Transaction;
import com.codealpha.stocktrading.service.MarketService;
import com.codealpha.stocktrading.service.TradingService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
//import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Map;

/**
 * Interactive Java Swing GUI for the CodeAlpha Stock Trading Platform.
 */
public class TradingPlatformGUI extends JFrame {
    private final MarketService marketService;
    private final TradingService tradingService;

    private JLabel cashLabel;
    private JLabel netWorthLabel;
    private JLabel portfolioValueLabel;

    private DefaultTableModel marketTableModel;
    private JTable marketTable;

    private DefaultTableModel holdingsTableModel;
    private JTable holdingsTable;

    private DefaultTableModel txnTableModel;
    private JTable txnTable;

    private JTextField tradeSymbolField;
    private JTextField tradeQuantityField;

    public TradingPlatformGUI(MarketService marketService, TradingService tradingService) {
        this.marketService = marketService;
        this.tradingService = tradingService;

        initializeUI();
        refreshAllViews();

        // Start background thread to simulate live market price updates every 4 seconds
        Timer marketTimer = new Timer(4000, e -> {
            marketService.simulateMarketTick();
            refreshAllViews();
        });
        marketTimer.start();
    }

    private void initializeUI() {
        setTitle("CodeAlpha - Stock Trading Platform & Portfolio Tracker");
        setSize(1150, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(12, 12));

        JPanel mainContainer = new JPanel(new BorderLayout(12, 12));
        mainContainer.setBorder(new EmptyBorder(15, 15, 15, 15));
        mainContainer.setBackground(new Color(245, 247, 250));

        // Top Financial KPI Cards
        mainContainer.add(createTopKPIPanel(), BorderLayout.NORTH);

        // Center Split: Left (Market Feed & Trading Action) + Right (Holdings & History Tabs)
        JSplitPane centerSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, createMarketPanel(), createRightTabsPanel());
        centerSplit.setDividerLocation(540);
        centerSplit.setResizeWeight(0.5);
        mainContainer.add(centerSplit, BorderLayout.CENTER);

        add(mainContainer);
    }

    private JPanel createTopKPIPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 12, 0));
        panel.setOpaque(false);

        cashLabel = new JLabel("$0.00", SwingConstants.CENTER);
        portfolioValueLabel = new JLabel("$0.00", SwingConstants.CENTER);
        netWorthLabel = new JLabel("$0.00", SwingConstants.CENTER);

        panel.add(createCard("Available Cash", cashLabel, new Color(37, 99, 235)));
        panel.add(createCard("Stock Portfolio Value", portfolioValueLabel, new Color(16, 185, 129)));
        panel.add(createCard("Total Net Worth", netWorthLabel, new Color(139, 92, 246)));
        return panel;
    }

    private JPanel createCard(String title, JLabel valueLabel, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 5, 0, 0, accentColor),
                new EmptyBorder(10, 15, 10, 15)
        ));

        JLabel titleLbl = new JLabel(title, SwingConstants.CENTER);
        titleLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        titleLbl.setForeground(new Color(100, 116, 139));

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        valueLabel.setForeground(accentColor);

        card.add(titleLbl, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    private JPanel createMarketPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setOpaque(false);

        // Market Table Container
        JPanel tableContainer = new JPanel(new BorderLayout());
        tableContainer.setBackground(Color.WHITE);
        tableContainer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(203, 213, 225)),
                        " Live Market Feed (Auto-Updating) ",
                        TitledBorder.LEFT, TitledBorder.TOP,
                        new Font("Segoe UI", Font.BOLD, 13), new Color(30, 41, 59)
                ),
                new EmptyBorder(8, 8, 8, 8)
        ));

        String[] cols = {"Symbol", "Company Name", "Price ($)", "Change (%)"};
        marketTableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        marketTable = new JTable(marketTableModel);
        marketTable.setRowHeight(26);
        marketTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        marketTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        marketTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Selection listener to auto-fill symbol field
        marketTable.getSelectionModel().addListSelectionListener(e -> {
            int row = marketTable.getSelectedRow();
            if (row >= 0) {
                tradeSymbolField.setText(marketTableModel.getValueAt(row, 0).toString());
            }
        });

        tableContainer.add(new JScrollPane(marketTable), BorderLayout.CENTER);
        panel.add(tableContainer, BorderLayout.CENTER);

        // Trading Execution Form Panel
        JPanel tradeForm = new JPanel(new GridLayout(3, 2, 8, 8));
        tradeForm.setBackground(Color.WHITE);
        tradeForm.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(203, 213, 225)),
                        " Execute Trade Order ",
                        TitledBorder.LEFT, TitledBorder.TOP,
                        new Font("Segoe UI", Font.BOLD, 13), new Color(30, 41, 59)
                ),
                new EmptyBorder(10, 12, 10, 12)
        ));

        tradeSymbolField = new JTextField();
        tradeQuantityField = new JTextField("1");

        tradeForm.add(new JLabel("Ticker Symbol:"));
        tradeForm.add(tradeSymbolField);
        tradeForm.add(new JLabel("Share Quantity:"));
        tradeForm.add(tradeQuantityField);

        JButton buyBtn = createStyledButton("BUY", new Color(16, 185, 129));
        buyBtn.addActionListener(e -> executeTrade(true));

        JButton sellBtn = createStyledButton("SELL", new Color(239, 68, 68));
        sellBtn.addActionListener(e -> executeTrade(false));

        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 8, 0));
        btnPanel.setOpaque(false);
        btnPanel.add(buyBtn);
        btnPanel.add(sellBtn);

        tradeForm.add(new JLabel("Action:"));
        tradeForm.add(btnPanel);

        panel.add(tradeForm, BorderLayout.SOUTH);
        return panel;
    }

    private JTabbedPane createRightTabsPanel() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));

        // Tab 1: Portfolio Holdings
        JPanel holdingsContainer = new JPanel(new BorderLayout());
        holdingsContainer.setBackground(Color.WHITE);
        String[] hCols = {"Symbol", "Shares Owned", "Current Price ($)", "Total Value ($)"};
        holdingsTableModel = new DefaultTableModel(hCols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        holdingsTable = new JTable(holdingsTableModel);
        holdingsTable.setRowHeight(26);
        holdingsTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        holdingsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        holdingsContainer.add(new JScrollPane(holdingsTable), BorderLayout.CENTER);
        tabbedPane.addTab("My Portfolio Holdings", holdingsContainer);

        // Tab 2: Transaction Audit Log History
        JPanel txnContainer = new JPanel(new BorderLayout());
        txnContainer.setBackground(Color.WHITE);
        String[] tCols = {"TXN ID", "Type", "Symbol", "Shares", "Price ($)", "Total ($)", "Timestamp"};
        txnTableModel = new DefaultTableModel(tCols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        txnTable = new JTable(txnTableModel);
        txnTable.setRowHeight(26);
        txnTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txnTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        txnContainer.add(new JScrollPane(txnTable), BorderLayout.CENTER);
        tabbedPane.addTab("Transaction History Log", txnContainer);

        return tabbedPane;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(6, 12, 6, 12));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void executeTrade(boolean isBuy) {
        try {
            String symbol = tradeSymbolField.getText().trim();
            int qty = Integer.parseInt(tradeQuantityField.getText().trim());

            boolean success;
            if (isBuy) {
                success = tradingService.buyStock(symbol, qty);
            } else {
                success = tradingService.sellStock(symbol, qty);
            }

            if (success) {
                JOptionPane.showMessageDialog(this, "Order executed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshAllViews();
            } else {
                JOptionPane.showMessageDialog(this, "Trade failed! Check available cash or share balance.", "Transaction Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid numeric share quantity.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshAllViews() {
        Map<String, Double> pricesMap = marketService.getCurrentMarketPricesMap();
        Portfolio portfolio = tradingService.getPortfolio();

        // 1. Update KPI Header
        cashLabel.setText(String.format("$%,.2f", portfolio.getCashBalance()));
        double stockVal = portfolio.calculateTotalNetWorth(pricesMap) - portfolio.getCashBalance();
        portfolioValueLabel.setText(String.format("$%,.2f", stockVal));
        netWorthLabel.setText(String.format("$%,.2f", portfolio.calculateTotalNetWorth(pricesMap)));

        // 2. Update Market Table
        marketTableModel.setRowCount(0);
        for (Stock s : marketService.getAllStocks()) {
            marketTableModel.addRow(new Object[]{
                    s.getSymbol(),
                    s.getCompanyName(),
                    String.format("$%.2f", s.getCurrentPrice()),
                    String.format("%.2f%%", s.getPriceChangePercentage())
            });
        }

        // 3. Update Holdings Table
        holdingsTableModel.setRowCount(0);
        for (var entry : portfolio.getHoldings().entrySet()) {
            String symbol = entry.getKey();
            int shares = entry.getValue();
            double price = pricesMap.getOrDefault(symbol, 0.0);
            holdingsTableModel.addRow(new Object[]{
                    symbol,
                    shares,
                    String.format("$%.2f", price),
                    String.format("$%,.2f", shares * price)
            });
        }

        // 4. Update Transaction History Table
        txnTableModel.setRowCount(0);
        for (Transaction txn : portfolio.getTransactions()) {
            txnTableModel.addRow(new Object[]{
                    txn.getTransactionId(),
                    txn.getType(),
                    txn.getSymbol(),
                    txn.getQuantity(),
                    String.format("$%.2f", txn.getPricePerShare()),
                    String.format("$%.2f", txn.getTotalAmount()),
                    txn.getFormattedTimestamp()
            });
        }
    }
}