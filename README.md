# CodeAlpha - Stock Trading Platform & Portfolio Tracker

A robust, multi-threaded desktop stock trading application built in **Java (Swing)** using an **MVC (Model-View-Controller)** architecture. Developed as part of the CodeAlpha Java Programming internship.

---

## 🚀 Features & Architecture

* **MVC Architecture**: Clean separation of concerns with structured packages (`model`, `service`, `ui`).
* **Live Market Simulation**: Background worker threads simulate real-time stock price fluctuations (-3% to +3% every 4 seconds) with automatic ticker updates.
* **Virtual Trading Engine**: Execute real-time `BUY` and `SELL` orders with automated cash validation and portfolio share tracking.
* **Financial Ledger & Audit Trail**: Full transaction history log tracking transaction IDs, order types, share quantities, execution prices, and precise timestamps.
* **Data Persistence**: Local CSV file storage ensures cash balances, portfolio holdings, and market catalog feeds persist across sessions.

---

## 📂 Project Directory Structure

```text
CodeAlpha_StockTradingPlatform/
│
├── data/
│   ├── stocks.csv         # Market stock catalog and price database
│   └── portfolio.csv      # User cash balance and holdings records
│
├── src/
│   └── com/
│       └── codealpha/
│           └── stocktrading/
│               ├── model/
│               │   ├── Portfolio.java
│               │   ├── Stock.java
│               │   └── Transaction.java
│               ├── service/
│               │   ├── MarketService.java
│               │   └── TradingService.java
│               ├── ui/
│               │   └── TradingPlatformGUI.java
│               └── Main.java
│
└── README.md