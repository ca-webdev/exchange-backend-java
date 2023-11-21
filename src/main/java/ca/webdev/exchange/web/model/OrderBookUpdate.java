package ca.webdev.exchange.web.model;

import java.util.Map;

public class OrderBookUpdate {
    private Map<Double, Integer> bidOrderBook;
    private Map<Double, Integer> askOrderBook;

    public OrderBookUpdate() {

    }

    public OrderBookUpdate(Map<Double, Integer> bidOrderBook, Map<Double, Integer> askOrderBook) {
        this.bidOrderBook = bidOrderBook;
        this.askOrderBook = askOrderBook;
    }

    public Map<Double, Integer> getBidOrderBook() {
        return bidOrderBook;
    }

    public Map<Double, Integer> getAskOrderBook() {
        return askOrderBook;
    }

    public void setBidOrderBook(Map<Double, Integer> bidOrderBook) {
        this.bidOrderBook = bidOrderBook;
    }

    public void setAskOrderBook(Map<Double, Integer> askOrderBook) {
        this.askOrderBook = askOrderBook;
    }
}
