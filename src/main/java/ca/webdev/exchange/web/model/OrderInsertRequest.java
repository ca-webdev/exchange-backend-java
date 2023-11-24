package ca.webdev.exchange.web.model;

public class OrderInsertRequest {
    private String side;
    private double price;
    private int size;

    public OrderInsertRequest() {
    }

    public OrderInsertRequest(String side, double price, int size) {
        this.side = side;
        this.price = price;
        this.size = size;
    }

    public String getSide() {
        return side;
    }

    public double getPrice() {
        return price;
    }

    public int getSize() {
        return size;
    }

    public void setSide(String side) {
        this.side = side;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
