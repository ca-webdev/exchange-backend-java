package ca.webdev.exchange.listeners;

public interface TradeListener {
    void handleTrade(long tradeTradeInEpochMillis, boolean isBuyTrade, double price, int size, String buyer, String seller);
}
