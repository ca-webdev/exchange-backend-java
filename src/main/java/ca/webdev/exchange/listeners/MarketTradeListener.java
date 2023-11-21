package ca.webdev.exchange.listeners;

public interface MarketTradeListener {
    void handleMarketTrade(int tradeId, long tradeTimeInMillisecondEpoch, double price, int size, String buyer, String seller, boolean isTakerSideBuy);
}
