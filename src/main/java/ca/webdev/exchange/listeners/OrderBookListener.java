package ca.webdev.exchange.listeners;

import ca.webdev.exchange.matching.Order;

import java.util.Queue;
import java.util.SortedMap;

public interface OrderBookListener {
    void handleOrderBook(SortedMap<Double, Queue<Order>> bidOrderBook, SortedMap<Double, Queue<Order>> askOrderBook);
}
