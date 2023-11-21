package ca.webdev.exchange.web.model;

public class RecentTrade {

    private long tradeTime;
    private double price;
    private int size;
    private String takerSide;

    public RecentTrade() {
    }

    public RecentTrade(long tradeTime, double price, int size, String takerSide) {
        this.tradeTime = tradeTime;
        this.price = price;
        this.size = size;
        this.takerSide = takerSide;
    }

    public long getTradeTime() {
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

    public void setTradeTime(long tradeTime) {
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
