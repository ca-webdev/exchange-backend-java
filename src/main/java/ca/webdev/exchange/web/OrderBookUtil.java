package ca.webdev.exchange.web;

import ca.webdev.exchange.matching.Order;

import java.util.Map;
import java.util.Queue;
import java.util.SortedMap;
import java.util.TreeMap;

public class OrderBookUtil {

    private OrderBookUtil() {
    }

    public static Map<Double, Integer> sumSizes(Map<Double, Queue<Order>> orderBook) {
        SortedMap<Double, Integer> sizeSummedOrderBook = new TreeMap<>();

        orderBook.forEach((priceLevel, orders) -> {
            int sum = orders.stream().mapToInt(Order::getRemainingSize).sum();
            sizeSummedOrderBook.put(priceLevel, sum);
        });

        return sizeSummedOrderBook;
    }
}
