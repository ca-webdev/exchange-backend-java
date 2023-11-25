package ca.webdev.exchange.web.model;

public class OrderUpdate {
    private String orderId;
    private long orderUpdateTime;
    private String side;
    private double price;
    private int size;
    private double filledPrice;
    private int filledSize;
    private String orderStatus;

    public OrderUpdate() {
    }

    public OrderUpdate(String orderId, long orderUpdateTime, String side, double price, int size, double filledPrice, int filledSize, String orderStatus) {
        this.orderId = orderId;
        this.orderUpdateTime = orderUpdateTime;
        this.side = side;
        this.price = price;
        this.size = size;
        this.filledPrice = filledPrice;
        this.filledSize = filledSize;
        this.orderStatus = orderStatus;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public long getOrderUpdateTime() {
        return orderUpdateTime;
    }

    public void setOrderUpdateTime(long orderUpdateTime) {
        this.orderUpdateTime = orderUpdateTime;
    }

    public String getSide() {
        return side;
    }

    public void setSide(String side) {
        this.side = side;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public double getFilledPrice() {
        return filledPrice;
    }

    public void setFilledPrice(double filledPrice) {
        this.filledPrice = filledPrice;
    }

    public int getFilledSize() {
        return filledSize;
    }

    public void setFilledSize(int filledSize) {
        this.filledSize = filledSize;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }
}
