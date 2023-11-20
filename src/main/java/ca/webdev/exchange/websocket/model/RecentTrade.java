package ca.webdev.exchange.websocket.model;

import java.time.LocalTime;

public class RecentTrade {

    private LocalTime tradeTime;
    private double price;
    private int size;

    public RecentTrade() {
    }

    public RecentTrade(LocalTime tradeTime, double price, int size) {
        this.tradeTime = tradeTime;
        this.price = price;
        this.size = size;
    }

    public LocalTime getTradeTime() {
        return tradeTime;
    }

    public double getPrice() {
        return price;
    }

    public int getSize() {
        return size;
    }

    public void setTradeTime(LocalTime tradeTime) {
        this.tradeTime = tradeTime;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
