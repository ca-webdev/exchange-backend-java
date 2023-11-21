package ca.webdev.exchange.websocket.model;

import java.time.LocalTime;

public class RecentTrade {

    private LocalTime tradeTime;
    private double price;
    private int size;
    private String takerSide;

    public RecentTrade() {
    }

    public RecentTrade(LocalTime tradeTime, double price, int size, String takerSide) {
        this.tradeTime = tradeTime;
        this.price = price;
        this.size = size;
        this.takerSide = takerSide;
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

    public String getTakerSide() {
        return takerSide;
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

    public void setTakerSide(String takerSide) {
        this.takerSide = takerSide;
    }
}
