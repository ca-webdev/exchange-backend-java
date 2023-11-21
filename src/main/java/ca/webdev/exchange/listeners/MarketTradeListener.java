package ca.webdev.exchange.listeners;

import java.time.LocalTime;

public interface MarketTradeListener {
    void handleMarketTrade(int tradeId, LocalTime tradeTime, double price, int size, String buyer, String seller, boolean isTakerSideBuy);
}
