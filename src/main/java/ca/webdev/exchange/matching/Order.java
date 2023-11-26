package ca.webdev.exchange.matching;

import java.util.UUID;

public class Order {
    private final UUID orderId;
    private final String owner;
    private final boolean isBuyOrder;
    private final double orderPrice;
    private final int orderSize;
    private double lastFilledPrice;
    private int remainingSize;

    public Order(UUID orderId, String owner, boolean isBuyOrder, double orderPrice, int orderSize, double lastFilledPrice, int remainingSize) {
        this.orderId = orderId;
        this.owner = owner;
        this.isBuyOrder = isBuyOrder;
        this.orderPrice = orderPrice;
        this.orderSize = orderSize;
        this.lastFilledPrice = lastFilledPrice;
        this.remainingSize = remainingSize;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public String getOwner() {
        return owner;
    }

    public boolean isBuyOrder() {
        return isBuyOrder;
    }

    public double getOrderPrice() {
        return orderPrice;
    }

    public int getOrderSize() {
        return orderSize;
    }

    public double getLastFilledPrice() {
        return lastFilledPrice;
    }

    public void setLastFilledPrice(double lastFilledPrice) {
        this.lastFilledPrice = lastFilledPrice;
    }

    public int getRemainingSize() {
        return remainingSize;
    }

    public void setRemainingSize(int remainingSize) {
        this.remainingSize = remainingSize;
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + orderId +
                ", owner='" + owner + '\'' +
                ", isBuyOrder=" + isBuyOrder +
                ", orderPrice=" + orderPrice +
                ", orderSize=" + orderSize +
                ", lastFilledPrice=" + lastFilledPrice +
                ", remainingSize=" + remainingSize +
                '}';
    }
}
