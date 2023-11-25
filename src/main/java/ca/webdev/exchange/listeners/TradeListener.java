package ca.webdev.exchange.listeners;

public interface TradeListener {
    void handleTrade(long tradeTradeInEpochMillis, boolean isBuyOrder, double price, int size, String buyer, String seller);
}
