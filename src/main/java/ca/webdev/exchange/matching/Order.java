package ca.webdev.exchange.matching;

import java.util.UUID;

public class Order {
    private final UUID orderId;
    private final String owner;
    private final int initialSize;
    private int remainingSize;

    public Order(UUID orderId, String owner, int initialSize, int remainingSize) {
        this.orderId = orderId;
        this.owner = owner;
        this.initialSize = initialSize;
        this.remainingSize = remainingSize;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public String getOwner() {
        return owner;
    }

    public int getInitialSize() {
        return initialSize;
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
                ", initialSize=" + initialSize +
                ", remainingSize=" + remainingSize +
                '}';
    }
}
