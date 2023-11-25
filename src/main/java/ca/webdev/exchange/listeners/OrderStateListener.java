package ca.webdev.exchange.listeners;

import ca.webdev.exchange.matching.OrderStatus;

import java.util.UUID;

public interface OrderStateListener {
    void handleOrderState(UUID orderId, long orderStateTimeInMillis, boolean isBuyOrder, double price, int size, double filledPrice, int filledSize, OrderStatus orderStatus);

}
