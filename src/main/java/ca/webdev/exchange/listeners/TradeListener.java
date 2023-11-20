package ca.webdev.exchange.listeners;

public interface TradeListener {
    void handleTrade(double price, int size, String buyer, String seller);
}
