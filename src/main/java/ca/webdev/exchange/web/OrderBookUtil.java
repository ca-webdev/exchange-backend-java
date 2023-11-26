package ca.webdev.exchange.web;

import ca.webdev.exchange.matching.Order;

import java.util.Map;
import java.util.Queue;
import java.util.SortedMap;

public class OrderBookUtil {

    private OrderBookUtil() {
    }

    public static Map<Double, Integer> sumSizes(SortedMap<Double, Integer> sizeSummedOrderBook, Map<Double, Queue<Order>> orderBook) {

        orderBook.forEach((priceLevel, orders) -> {
            int sum = orders.stream().mapToInt(Order::getRemainingSize).sum();
            sizeSummedOrderBook.put(priceLevel, sum);
        });

        return sizeSummedOrderBook;
    }
}
