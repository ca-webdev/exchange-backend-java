package ca.webdev.exchange.web.model;

public class UserTrade {

    private long tradeTime;
    private String side;
    private double price;
    private int size;

    public UserTrade() {
    }

    public UserTrade(long tradeTime, String side, double price, int size) {
        this.tradeTime = tradeTime;
        this.side = side;
        this.price = price;
        this.size = size;
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

    public String getSide() {
        return side;
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

    public void setSide(String side) {
        this.side = side;
    }
}
